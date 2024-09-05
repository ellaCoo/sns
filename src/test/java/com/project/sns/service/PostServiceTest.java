package com.project.sns.service;

import com.project.sns.domain.Hashtag;
import com.project.sns.domain.Post;
import com.project.sns.domain.UserAccount;
import com.project.sns.dto.*;
import com.project.sns.repository.HashtagRepository;
import com.project.sns.repository.PostHashtagRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - Post")
@ExtendWith(MockitoExtension.class) //Mockito를 사용하여 테스트를 작성할 때 필요한 확장 기능을 제공
public class PostServiceTest {
    @InjectMocks //PostService 객체를 생성하고, @Mock으로 주입된 PostRepository를 이 객체에 주입
    private PostService sut; //sut는 : "System Under Test", 테스트하고자 하는 실제 서비스 클래스의 인스턴스

    @Mock //PostRepository를 Mock 객체로 생성
    private PostRepository postRepository; //이 Mock 객체는 실제 데이터베이스나 외부 시스템과 상호작용하지 않고, 테스트 시에 정의된 동작만을 수행
    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private HashtagService hashtagService;
    @Mock
    private PostHashtagRepository postHashtagRepository;

    @DisplayName("getPosts - 페이징 정보를 넘기면 페이징 처리하여 반환한다.")
    @Test
    void givenPageable_whenGetPosts_thenReturnsPostsList() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        List<Post> posts = List.of(
                createPost(1L),
                createPost(2L)
        );
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        given(postRepository.findAll(pageable)).willReturn(postPage);

        // When
        Page<PostWithLikesAndHashtagsDto> actual = sut.getPosts(pageable);

        // Then
        assertThat(actual).hasSize(2);
        then(postRepository).should().findAll(pageable);
    }

    @DisplayName("getPosts - user 정보와 페이징 정보를 넘기면 user의 포스트를 페이징 처리하여 반환한다.")
    @Test
    void givenUserInfoAndPageable_whenGetPosts_thenReturnsPostsList() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        List<Post> posts = List.of(
                createPost(1L),
                createPost(2L)
        );
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());
        UserAccountDto userAccountDto = createUserAccountDto();

        given(postRepository.findByUserAccount_UserId(userAccountDto.userId(), pageable)).willReturn(postPage);

        // When
        Page<PostWithLikesAndHashtagsDto> actual = sut.getPosts(userAccountDto, pageable);

        // Then
        assertThat(actual).hasSize(2);
        then(postRepository).should().findByUserAccount_UserId(userAccountDto.userId(), pageable);
    }

    @DisplayName("getPost - 포스트를 조회하면, 포스트를 반환한다.")
    @Test
    void givenPostId_whenSearchingPost_thenReturnsPost() {
        // Given
        Long postId = 1L;
        Post post = createPost(postId);
        PostWithLikesAndHashtagsDto expectedDto = PostWithLikesAndHashtagsDto.fromEntity(post);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // When
        PostWithLikesAndHashtagsDto dto = sut.getPost(postId);

        // Then
        // assertThat : assertj 라이브러리를 사용하여 dto 객체가 예상대로 생성되었는지 검증
        assertThat(dto).isEqualTo(expectedDto);
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

    @DisplayName("updatePost - 게시글 작성자와 로그인된 사용자가 동일할 때, 포스트의 수정 정보를 입력하면, 포스트를 수정한다.")
    @Test
    void givenModifiedPostInfo_whenUpdatingPost_thenUpdatesPost() {
        // Given
        Long postId = 1L;
        String userId = "ella";
        String updatedTitle = "Updated Title";
        String updatedContent = "Updated Content";
        String hashtagName = "hashtag";
        Set<String> updatedHashtags = Set.of(hashtagName);

        Post post = createPost(postId); // "title","content"
        UserAccount userAccount = createUserAccount(userId); // "ella"
        PostWithHashtagsDto dto = createPostWithHashtagsDto(updatedTitle, updatedContent, updatedHashtags);
        Set<Hashtag> hashtags = Set.of(Hashtag.of(hashtagName));

        given(postRepository.getReferenceById(postId)).willReturn(post);
        given(userAccountRepository.getReferenceById(userId)).willReturn(userAccount);
        willDoNothing().given(postHashtagRepository).deleteByPostId(postId);
        given(hashtagService.getExistedOrCreatedHashtagsByHashtagNames(updatedHashtags)).willReturn(hashtags);
        willDoNothing().given(postRepository).flush();
        willDoNothing().given(hashtagService).deleteUnusedHashtags(any());

        // When
        sut.updatePost(postId, dto);

        // Then
        then(postRepository).should().getReferenceById(postId);
        then(userAccountRepository).should().getReferenceById(userId);
        then(postHashtagRepository).should().deleteByPostId(postId);
        then(hashtagService).should().getExistedOrCreatedHashtagsByHashtagNames(updatedHashtags);
        then(postRepository).should().flush();
        then(hashtagService).should().deleteUnusedHashtags(any());

        // Verify that the title and content are updated
        assertThat(updatedTitle).isEqualTo(post.getTitle());
        assertThat(updatedContent).isEqualTo(post.getContent());

        // Verify that new hashtags are added
        assertThat(1).isEqualTo(post.getPostHashtags().size());
    }

    @DisplayName("updatePost - 없는 포스트의 수정 정보를 입력하면, 경고 로그를 찍고 아무것도 하지 않는다.")
    @Test
    void givenNoneExistentPostInfo_whenUpdatingPost_thenLogsWarningAndDoesNothing() {
        // Given
        PostWithHashtagsDto dto = createPostWithHashtagsDto();
        given(postRepository.getReferenceById(dto.postDto().id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updatePost(dto.postDto().id(), dto);

        // Then
        then(postRepository).should().getReferenceById(dto.postDto().id());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(postHashtagRepository).shouldHaveNoInteractions();
        then(hashtagService).shouldHaveNoInteractions();
    }

    @DisplayName("updatePost - 포스트 작성자가 아닌 사람이 수정 정보를 입력하면, 아무 것도 하지 않는다.")
    @Test
    void givenModifiedPostInfoWithDifferentUser_whenUpdatingPost_thenDoesNothing() {
        // Given
        Long postId = 1L;
        String differentUserId = "differentUserId";
        String hashtagName = "hashtag";
        Set<String> updatedHashtags = Set.of(hashtagName);

        Post post = createPost(postId);
        UserAccount userAccount = createUserAccount(differentUserId);
        PostWithHashtagsDto dto = createPostWithHashtagsDto("testTitle", "testContent", updatedHashtags);
        // dto's CreatedBy : ella

        given(postRepository.getReferenceById(postId)).willReturn(post);
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(userAccount);

        // When
        sut.updatePost(postId, dto);

        // Then
        then(postRepository).should().getReferenceById(postId);
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(postHashtagRepository).shouldHaveNoInteractions();
        then(hashtagService).shouldHaveNoInteractions();
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
        PostWithHashtagsDto dto = createPostWithHashtagsDto();
        Post post = createPost();
        String hashtagName = "hashtag";
        Set<Hashtag> hashtags = Set.of(Hashtag.of(hashtagName));

        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(createUserAccount());
        given(hashtagService.getExistedOrCreatedHashtagsByHashtagNames(any())).willReturn(hashtags);
        given(postRepository.save(any(Post.class))).willReturn(post);

        // When
        PostDto result = sut.createPost(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo(post.getTitle());
        assertThat(result.content()).isEqualTo(post.getContent());

        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(hashtagService).should().getExistedOrCreatedHashtagsByHashtagNames(any());
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

    private PostWithHashtagsDto createPostWithHashtagsDto() {
        return PostWithHashtagsDto.of(
                createPostDto(),
                createUserAccountDto(),
                createHashtagDtos()
        );
    }

    private Set<HashtagDto> createHashtagDtos() {
        return Set.of(
                HashtagDto.of("test1"),
                HashtagDto.of("test2"),
                HashtagDto.of("test3")
        );
    }

    private PostWithHashtagsDto createPostWithHashtagsDto(String title, String content, Set<String> hashtags) {
        PostDto postDto = PostDto.of(title, content);
        Set<HashtagDto> hashtagDtos = hashtags.stream()
                .map(hashtag -> HashtagDto.of(hashtag))
                .collect(Collectors.toSet());
        return PostWithHashtagsDto.of(postDto, createUserAccountDto(), hashtagDtos);
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
