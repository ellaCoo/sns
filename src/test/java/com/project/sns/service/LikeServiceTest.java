package com.project.sns.service;

import com.project.sns.domain.Like;
import com.project.sns.domain.Notification;
import com.project.sns.domain.Post;
import com.project.sns.domain.UserAccount;
import com.project.sns.domain.constant.NotificationType;
import com.project.sns.dto.LikeDto;
import com.project.sns.repository.LikeRepository;
import com.project.sns.repository.NotificationRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - Like")
@ExtendWith(MockitoExtension.class) //Mockito를 사용하여 테스트를 작성할 때 필요한 확장 기능을 제공
public class LikeServiceTest {
    @InjectMocks
    private LikeService sut;

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private NotificationRepository notificationRepository;

    @DisplayName("deleteLike - postId, userId 전달 받아서, 좋아요와 알림을 삭제한다.")
    @Test
    void givenPostIdAndUserId_whenDeletingLike_thenDeleteLikeAndNotification() {
        // Given
        Long postId = 1L;
        String userId = "ella";
        Like like = createLike(1L);
        LikeDto likeDto = LikeDto.of(postId, userId);

        given(likeRepository.findByUserAccount_userIdAndPost_Id(userId, postId)).willReturn(Optional.of(like));
        willDoNothing().given(likeRepository).delete(like);
        willDoNothing().given(notificationRepository).deleteByNotificationTypeAndOccurUserIdAndTargetId(NotificationType.NEW_LIKE_ON_POST, userId, 1L);

        // When
        sut.deleteLike(likeDto);

        // Then
        then(likeRepository).should().findByUserAccount_userIdAndPost_Id(userId, postId);
        then(likeRepository).should().delete(like);
        then(notificationRepository).should().deleteByNotificationTypeAndOccurUserIdAndTargetId(NotificationType.NEW_LIKE_ON_POST, userId, 1L);

        verify(likeRepository, times(1)).delete(like);
        verify(notificationRepository, times(1))
                .deleteByNotificationTypeAndOccurUserIdAndTargetId(NotificationType.NEW_LIKE_ON_POST, likeDto.userId(), like.getId());
    }

    @DisplayName("deleteLike - postId, userId 로 조회되는 좋아요가 없는 경우 에러를 반환한다.")
    @Test
    void testDeleteLike_EntityNotFound() {
        // given
        Long postId = 1L;
        String userId = "ella";
        LikeDto likeDto = LikeDto.of(postId, userId);

        when(likeRepository.findByUserAccount_userIdAndPost_Id(likeDto.userId(), likeDto.postId()))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(EntityNotFoundException.class, () -> sut.deleteLike(likeDto));

        verify(likeRepository, never()).delete(any());
        verify(notificationRepository, never()).deleteByNotificationTypeAndOccurUserIdAndTargetId(any(), anyString(), anyLong());
    }

    @DisplayName("createLike - postId, userId 전달 받아서, 좋아요와 알림을 저장한다.")
    @Test
    void givenPostIdAndUserId_whenCreatingLike_thenCreateLikeAndNotification() {
        // Given
        Long postId = 1L;
        String userId = "ella";
        LikeDto dto = LikeDto.of(postId, userId);
        Like entity = createLike(1L);

        given(postRepository.getReferenceById(postId)).willReturn(createPost(postId));
        given(userAccountRepository.getReferenceById(userId)).willReturn(createUserAccount(userId));
        given(likeRepository.save(any(Like.class))).willReturn(entity);
        given(notificationRepository.save(any(Notification.class))).willReturn(null);

        // When
        sut.createLike(dto);

        // Then
        then(postRepository).should().getReferenceById(postId);
        then(userAccountRepository).should().getReferenceById(userId);
        then(likeRepository).should().save(any(Like.class));
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

    private Like createLike(Long id) {
        Like like = Like.of(
                createPost(),
                createUserAccount()
        );
        ReflectionTestUtils.setField(like, "id", id);

        return like;
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
}
