package com.project.sns.service;

import com.project.sns.domain.Post;
import com.project.sns.domain.UserAccount;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.UserAccountDto;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 포스트")
@ExtendWith(MockitoExtension.class) //Mockito를 사용하여 테스트를 작성할 때 필요한 확장 기능을 제공
public class PostServiceTest {
    @InjectMocks //PostService 객체를 생성하고, @Mock으로 주입된 PostRepository를 이 객체에 주입
    private PostService sut; //sut는 : "System Under Test", 테스트하고자 하는 실제 서비스 클래스의 인스턴스

    @Mock //PostRepository를 Mock 객체로 생성
    private PostRepository postRepository; //이 Mock 객체는 실제 데이터베이스나 외부 시스템과 상호작용하지 않고, 테스트 시에 정의된 동작만을 수행
    @Mock
    private UserAccountRepository userAccountRepository;

    @DisplayName("getPost - 포스트 페이지를 호출하면 페이징 처리하여 반환한다.")
    @Test
    void givenNoParameters_whenGetPosts_thenReturnsArticlePage() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(postRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<PostDto> posts = sut.getPosts(pageable);

        // Then
        assertThat(posts).isEmpty();
        then(postRepository).should().findAll(pageable);
    }

    @DisplayName("getPost - 포스트를 조회하면, 포스트를 반환한다.")
    @Test
    void givenPostId_whenSearchingPost_thenReturnsPost() {
        // Given
        Long postId = 1L;
        Post post = createPost();
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // When
        PostDto dto = sut.getPost(postId);

        // Then
        // assertThat : assertj 라이브러리를 사용하여 dto 객체가 예상대로 생성되었는지 검증
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", post.getTitle())
                .hasFieldOrPropertyWithValue("content", post.getContent());
        then(postRepository).should().findById(postId); //모의된 메서드 호출을 검증
    }

    @DisplayName("getPost - 포스트가 없으면, 예외를 던진다.")
    @Test
    void givenNoneExistentPostId_whenSearchingPost_thenThrowsException() {
        // Given
        Long postId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getPost(postId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("포스트가 없습니다 - postId: " + postId);
        then(postRepository).should().findById(postId);
    }

    @DisplayName("updatePost - 포스트의 수정 정보를 입력하면, 포스트를 수정한다.")
    @Test
    void givenModifiedPostInfo_whenUpdatingPost_thenUpdatesPost() {
        // Given
        Post post = createPost();
        PostDto dto = createPostDto();

        given(postRepository.getReferenceById(dto.id())).willReturn(post);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());

        // When
        sut.updatePost(dto.id(), dto);

        // Then
        assertThat(post)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content());
        then(postRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("updatePost - 없는 포스트의 수정 정보를 입력하면, 경고 로그를 찍고 아무것도 하지 않는다.")
    @Test
    void givenNoneExistentPostInfo_whenUpdatingPost_thenLogsWarningAndDoesNothing() {
        // Given
        PostDto dto = createPostDto();
        given(postRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updatePost(dto.id(), dto);

        // Then
        then(postRepository).should().getReferenceById(dto.id());
        then(userAccountRepository).shouldHaveNoInteractions();
    }

    @DisplayName("updatePost - 포스트 작성자가 아닌 사람이 수정 정보를 입력하면, 아무 것도 하지 않는다.")
    @Test
    void givenModifiedPostInfoWithDifferentUser_whenUpdatingPost_thenDoesNothing() {
        // Given
        Long differentPostId = 10L;
        Post differentPost = createPost(differentPostId);
        differentPost.setUserAccount(createUserAccount("TEST MAN"));
        PostDto dto = createPostDto(); // user: ella
        given(postRepository.getReferenceById(differentPostId)).willReturn(differentPost);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(dto.userAccountDto().toEntity());

        // When
        sut.updatePost(differentPostId, dto);

        // Then
        then(postRepository).should().getReferenceById(differentPostId);
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
    }

    @DisplayName("deletePost - 포스트의 ID를 입력하면, 포스트를 삭제한다.")
    @Test
    void givenPostId_whenDeletingPost_thenDeletesPost() {
        // Given
        Long postId = 1L;
        String userId = "ella";
        willDoNothing().given(postRepository).deleteByIdAndUserAccount_UserId(postId, userId);

        // When
        sut.deletePost(1L, userId);

        // Then
        then(postRepository).should().deleteByIdAndUserAccount_UserId(postId, userId);
    }

    @DisplayName("createPost - 포스트 정보를 입력하면, 포스트를 생성한다.")
    @Test
    void givenPostId_whenSavingPost_thenSavesPost() {
        // Given
        PostDto dto = createPostDto();
        Post post = createPost();

        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(postRepository.save(any(Post.class))).willReturn(post);

        // When
        PostDto result = sut.createPost(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.content()).isEqualTo(post.getContent());

        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(postRepository).should().save(any(Post.class));
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
}
