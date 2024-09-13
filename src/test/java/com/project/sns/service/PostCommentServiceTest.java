package com.project.sns.service;

import com.project.sns.domain.Notification;
import com.project.sns.domain.Post;
import com.project.sns.domain.PostComment;
import com.project.sns.domain.UserAccount;
import com.project.sns.domain.constant.NotificationType;
import com.project.sns.dto.PostCommentDto;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.UserAccountDto;
import com.project.sns.repository.NotificationRepository;
import com.project.sns.repository.PostCommentRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@DisplayName("비즈니스 로직 - PostComment")
@ExtendWith(MockitoExtension.class) //Mockito를 사용하여 테스트를 작성할 때 필요한 확장 기능을 제공
class PostCommentServiceTest {
    @InjectMocks
    private PostCommentService sut;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostCommentRepository postCommentRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @DisplayName("searchPostComments - 포스트 ID로 조회하면, 해당하는 댓글 리스트를 반환한다.")
    @Test
    void givenPostId_whenSearchingPostComments_thenReturnsPostComments() {
        // Given
        Long postId = 1L;
        PostComment expectedParentComment = createPostComment(1L, "parent content");
        PostComment expectedChildComment = createPostComment(2L, "child content");
        expectedChildComment.setParentCommentId(expectedParentComment.getId());
        given(postCommentRepository.findByPost_Id(postId)).willReturn(List.of(
                expectedParentComment,
                expectedChildComment
        ));

        // When
        Set<PostCommentDto> actual = sut.searchPostComments(postId);

        // Then
        assertThat(actual).hasSize(2);
        assertThat(actual)
                .extracting("id", "postId", "parentCommentId", "content")// 지정된 필드들을 추출하여 검증
                .containsExactlyInAnyOrder(
                        tuple(1L, 1L, null, "parent content"),
                        tuple(2L, 1L, 1L, "child content")
                );
        then(postCommentRepository).should().findByPost_Id(postId);
    }

    @DisplayName("deletePostComment - 댓글 ID를 입력하면, 댓글과 알림을 삭제한다.")
    @Test
    void givenPostCommentId_whenDeletingPostComment_thenDeletesPostCommentAndNotification() {
        // Given
        Long postCommentId = 1L;
        String userId = "ella";
        PostComment postComment = createPostComment(postCommentId, "content");

        given(postCommentRepository.findByIdAndUserAccount_userId(postCommentId, userId)).willReturn(Optional.of(postComment));
        willDoNothing().given(postCommentRepository).delete(postComment);
        willDoNothing().given(notificationRepository).deleteByNotificationTypeAndOccurUserIdAndTargetId(NotificationType.NEW_COMMENT_ON_POST, userId, postCommentId);

        // When
        sut.deletePostComment(postCommentId, userId);

        // Then
        then(postCommentRepository).should().findByIdAndUserAccount_userId(postCommentId, userId);
        then(postCommentRepository).should().delete(postComment);
        then(notificationRepository).should().deleteByNotificationTypeAndOccurUserIdAndTargetId(NotificationType.NEW_COMMENT_ON_POST, userId, postCommentId);

        verify(postCommentRepository, times(1)).delete(postComment);
        verify(notificationRepository, times(1))
                .deleteByNotificationTypeAndOccurUserIdAndTargetId(NotificationType.NEW_COMMENT_ON_POST, userId, postCommentId);
    }

    @DisplayName("createPostComment - 부모 댓글 정보를 입력하면, 댓글과 알림을 저장한다.")
    @Test
    void givenPostCommentInfo_whenSavingPostComment_thenSavesPostCommentAndNotification() {
        // Given
        PostCommentDto dto = createPostCommentDto(null, "comment content");
        given(postRepository.getReferenceById(dto.postId())).willReturn(createPost());
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(postCommentRepository.save(any(PostComment.class))).willReturn(null);
        willDoNothing().given(postCommentRepository).flush();
        given(notificationRepository.save(any(Notification.class))).willReturn(null);

        // When
        sut.createPostComment(dto);

        // Then
        then(postRepository).should().getReferenceById(dto.postId());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(postCommentRepository).should(never()).getReferenceById(anyLong());
        then(postCommentRepository).should().save(any(PostComment.class));
        then(postCommentRepository).should().flush();
        then(notificationRepository).should().save(any(Notification.class));
    }

    @DisplayName("createPostComment - 댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 하지 않는다.")
    @Test
    void givenNoneExistPost_whenSavingPostComment_thenLogsSituationAndDoesNothing() {
        // Given
        PostCommentDto dto = createPostCommentDto(null, "comment content");
        given(postRepository.getReferenceById(dto.postId())).willThrow(EntityNotFoundException.class);

        // When
        sut.createPostComment(dto);

        // Then
        then(postRepository).should().getReferenceById(dto.postId());
        then(userAccountRepository).shouldHaveNoInteractions(); // userAccountRepository 모의 객체가 테스트 중에 어떤 메서드도 호출되지 않았음을 확인
        then(postCommentRepository).shouldHaveNoInteractions();
    }

    @DisplayName("createPostComment - 부모 댓글 ID와 댓글 정보를 입력하면, 대댓글과 알림을 저장한다.")
    @Test
    void givenParentCommentIdAndPostCommentInfo_whenSavingPostComment_thenSavesChildCommentAndNotification() {
        // Given
        Long parentCommentId = 1L;
        PostCommentDto child = createPostCommentDto(parentCommentId, "대댓글");
        PostComment parent = createPostComment(parentCommentId, "댓글");

        given(postRepository.getReferenceById(child.postId())).willReturn(createPost());
        given(userAccountRepository.getReferenceById(child.userAccountDto().userId())).willReturn(createUserAccount());
        given(postCommentRepository.getReferenceById(child.postId())).willReturn(parent);
        willDoNothing().given(postCommentRepository).flush();
        given(notificationRepository.save(any(Notification.class))).willReturn(null);

        // When
        sut.createPostComment(child);

        // Then
        assertThat(child.parentCommentId()).isNotNull();
        then(postRepository).should().getReferenceById(child.postId());
        then(userAccountRepository).should().getReferenceById(child.userAccountDto().userId());
        then(postCommentRepository).should().getReferenceById(child.parentCommentId());
        then(postCommentRepository).should(never()).save(any(PostComment.class));
        then(postCommentRepository).should().flush();
        then(notificationRepository).should().save(any(Notification.class));
    }


    private Post createPost() {
        return createPost(1L);
    }

    private Post createPost(Long id) {
        Post post = Post.of(
                createUserAccount(),
                "title",
                "content"
        );
        ReflectionTestUtils.setField(post, "id", id);

        return post;
    }

    private UserAccount createUserAccount() {
        return createUserAccount("ella");
    }

    private UserAccount createUserAccount(String userId) {
        return UserAccount.of(
                userId,
                "password",
                "ella@email.com",
                "Ella",
                null,
                null
        );
    }

    private PostDto createPostDto() {
        return PostDto.of(
                createUserAccountDto(),
                "title",
                "content"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "ella",
                "pw",
                "ella@mail.com",
                "ella",
                "memo"
        );
    }

    private PostComment createPostComment(Long id, String content) {
        PostComment postComment = PostComment.of(
                createPost(),
                createUserAccount(),
                content
        );
        ReflectionTestUtils.setField(postComment, "id", id);

        return postComment;
    }

    private  PostCommentDto createPostCommentDto(Long parentCommentId, String content) {
        return createPostCommentDto(1L, parentCommentId, content);
    }

    private PostCommentDto createPostCommentDto(Long id, Long parentCommentId, String content) {
        return PostCommentDto.of(
                id,
                1L,
                createUserAccountDto(),
                parentCommentId,
                content,
                LocalDateTime.now(),
                "ella",
                LocalDateTime.now(),
                "ella"
        );
    }
}