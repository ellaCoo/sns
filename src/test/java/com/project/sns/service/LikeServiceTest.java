package com.project.sns.service;

import com.project.sns.domain.Like;
import com.project.sns.domain.Post;
import com.project.sns.domain.UserAccount;
import com.project.sns.dto.LikeDto;
import com.project.sns.repository.LikeRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

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

    @DisplayName("deleteLike - postId, userId 전달 받아서, 좋아요를 삭제한다.")
    @Test
    void givenPostIdAndUserId_whenDeletingLike_thenDeleteLike() {
        // Given
        Long postId = 1L;
        String userId = "ella";
        LikeDto dto = LikeDto.of(postId, userId);
        willDoNothing().given(likeRepository).deleteByUserAccount_userIdAndPost_Id(userId, postId);

        // When
        sut.deleteLike(dto);

        // Then
        then(likeRepository).should().deleteByUserAccount_userIdAndPost_Id(userId, postId);
    }

    @DisplayName("createLike - postId, userId 전달 받아서, 좋아요를 저장한다.")
    @Test
    void givenPostIdAndUserId_whenCreatingLike_thenCreateLike() {
        // Given
        Long postId = 1L;
        String userId = "ella";
        LikeDto dto = LikeDto.of(postId, userId);
        Like entity = Like.of(createPost(), createUserAccount());

        given(postRepository.getReferenceById(postId)).willReturn(createPost(postId));
        given(userAccountRepository.getReferenceById(userId)).willReturn(createUserAccount(userId));
        given(likeRepository.save(any(Like.class))).willReturn(null);

        // When
        sut.createLike(dto);

        // Then
        then(postRepository).should().getReferenceById(postId);
        then(userAccountRepository).should().getReferenceById(userId);
        then(likeRepository).should().save(any(Like.class));
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
}
