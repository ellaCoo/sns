package com.project.sns.controller;

import com.project.sns.config.TestSecurityConfig;
import com.project.sns.domain.Hashtag;
import com.project.sns.domain.Like;
import com.project.sns.domain.constant.FormStatus;
import com.project.sns.dto.*;
import com.project.sns.dto.request.PostRequest;
import com.project.sns.dto.response.PostResponse;
import com.project.sns.dto.response.PostWithLikesAndHashtagAndCommentsResponse;
import com.project.sns.dto.response.PostWithLikesAndHashtagsResponse;
import com.project.sns.service.HashtagService;
import com.project.sns.service.NotificationService;
import com.project.sns.service.PostCommentService;
import com.project.sns.service.PostService;
import com.project.sns.util.FormDataEncoder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("컨트롤러 로직 - Post")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(PostController.class) //MVC 관련 빈들만 로드함 - 테스트 실행이 빠르고, 메모리 사용량 적음 | 서비스, 리포지토리 등의 빈은 로드되지 않음
class PostControllerTest {

    private final MockMvc mvc; //HTTP 요청을 모의(mock)하여 컨트롤러의 동작을 테스트 | 실제 웹 서버를 띄우지 않고도 컨트롤러의 요청-응답 흐름을 테스트할 수 있음
    private final FormDataEncoder formDataEncoder;

    // 필드주입
    @MockBean // ->MockBean이 생성자 주입 지원하지 않음
    private PostService postService;
    @MockBean
    private HashtagService hashtagService;
    @MockBean
    private NotificationService notificationService;

    // 생성자주입
    PostControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @DisplayName("[view][GET] 포스트 리스트 (피드) 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingPostsView_thenReturnsPostsView() throws Exception {
        // Given
        given(postService.getPosts(any(Pageable.class)))
                .willReturn(Page.empty());

        // When & Then
        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/index"))
                .andExpect(model().attributeExists("posts"));
        then(postService).should().getPosts(any(Pageable.class));
    }

    @DisplayName("[api][POST] 포스트 리스트 (피드) API - 페이징, CSRF 기능")
    @Test
    void givenPagingParams_whenPosts_thenReturnsPostsList() throws Exception {
        // Given
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.Direction.DESC, "createdAt");
        given(postService.getPosts(pageable)).willReturn(Page.empty());

        // When & Then
        mvc.perform(
                        post("/posts")
                                .queryParam("page", String.valueOf(pageNumber))
                                .with(csrf()) // CSRF 토큰 추가
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray()); // 반환된 내용이 배열인지 확인
    }

    @WithMockUser // user정보를 모킹해서 넣어줌
    @DisplayName("[view][GET] 포스트 페이지 반환 - 정상 호출")
    @Test
    void givenAuthorizedUser_whenRequestingPostView_thenReturnsPostView() throws Exception {
        // Given
        Long postId = 1L;
        given(postService.getPostWithLikesAndHashtagsAndComments(postId)).willReturn(createPostWithLikesAndHashtagsAndCommentDto());

        // When & Then
        mvc.perform(get("/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/detail"))
                .andExpect(model().attributeExists("post"));
        then(postService).should().getPostWithLikesAndHashtagsAndComments(postId);
    }

    @DisplayName("[view][GET] 포스트 페이지 반환 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequestingPostView_thenReturnsPostView() throws Exception {
        // Given
        Long postId = 1L;

        // When & Then
        mvc.perform(get("/posts/" + postId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(postService).shouldHaveNoInteractions(); // postService 모의 객체가 테스트 중에 어떤 메서드도 호출되지 않았음을 확인
    }

    @WithMockUser // user정보를 모킹해서 넣어줌
    @DisplayName("[view][GET] 포스트 수정 페이지 반환 - 정상 호출")
    @Test
    void givenAuthorizedUser_whenRequestingPostUpdateView_thenReturnsPostEditView() throws Exception {
        // Given
        Long postId = 1L;
        given(postService.getPost(postId)).willReturn(createPostWithLikesAndHashtagsDto());

        // When & Then
        mvc.perform(get("/posts/" + postId + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/form"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attributeExists("formStatus"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE));
        then(postService).should().getPost(postId);
    }

    @DisplayName("[view][GET] 포스트 수정 페이지 반환 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    void givenNothing_whenRequestingPostUpdateView_thenReturnsPostEditView() throws Exception {
        // Given
        Long postId = 1L;

        // When & Then
        mvc.perform(get("/posts/" + postId + "/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        then(postService).shouldHaveNoInteractions();
    }

    @WithUserDetails(value = "ellaTest", setupBefore = TestExecutionEvent.TEST_EXECUTION) // TestSecurityConfig에서 지정한 유저정보 사용 가능 | TEST_EXECUTION:테스트 직전에 셋업해라
    @DisplayName("[view][POST] 포스트 수정 - 정상 호출")
    @Test
    void givenUpdatedPostInfo_whenRequesting_thenUpdatePost() throws Exception {
        // Given
        Long postId = 1L; //테스트할 id
        PostRequest postRequest = PostRequest.of("new title", "new content", "hashtag");
        /*
         willDoNothing : updatePost 메서드 호출될 때 아무런 동작 하지 않도록
         eq(postId) : postId와 동일한 값이 전달될 때
         any(PostDto.class) : PostDto 타입의 아무 객체나 허용
         */
        willDoNothing().given(postService).updatePost(eq(postId), any(PostWithHashtagsDto.class));

        // When & Then
        mvc.perform(
                post("/posts/" + postId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(postRequest))
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts/" + postId))
                .andExpect(redirectedUrl("/posts/" + postId));
        //postService 의 updatePost 메서드가 특정 인자들로 호출되었는지 확인
        then(postService).should().updatePost(eq(postId), any(PostWithHashtagsDto.class));
    }

    @WithUserDetails(value = "ellaTest", setupBefore = TestExecutionEvent.TEST_EXECUTION) // TestSecurityConfig에서 지정한 유저정보 사용 가능 | TEST_EXECUTION:테스트 직전에 셋업해라
    @DisplayName("[view][POST] 포스트 삭제 - 정상 호출")
    @Test
    void givenPostIdToDelete_whenRequesting_thenDeletePost() throws Exception {
        // Given
        long postId = 1L;
        String userId = "ellaTest";
        willDoNothing().given(postService).deletePost(postId, userId);

        // When & Then
        mvc.perform(
                post("/posts/" + postId + "/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts"))
                .andExpect(redirectedUrl("/posts"));
        then(postService).should().deletePost(postId, userId);
    }

    @WithMockUser
    @DisplayName("[view][GET] 새 포스트 작성 페이지")
    @Test
    void givenNothing_whenRequesting_thenReturnsNewPostPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/posts/form"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("posts/form"))
                .andExpect(model().attributeExists("post"))
                .andExpect(model().attribute("post", PostWithLikesAndHashtagsResponse.of("")))
                .andExpect(model().attributeExists("formStatus"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE));
    }


    @WithUserDetails(value = "ellaTest", setupBefore = TestExecutionEvent.TEST_EXECUTION) // TestSecurityConfig에서 지정한 유저정보 사용 가능 | TEST_EXECUTION:테스트 직전에 셋업해라
    @DisplayName("[view][POST] 새 포스트 등록 - 정상 호출")
    @Test
    void givenNewPostInfo_whenRequesting_thenSavesNewPost() throws Exception {
        // Given
        PostRequest postRequest = PostRequest.of("new title", "new content", "hashtag");
        given(postService.createPost(any(PostWithHashtagsDto.class))).willReturn(createPostDto());

        // When & Then
        mvc.perform(
                post("/posts/form")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(formDataEncoder.encode(postRequest))
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts/" + createPostDto().id()))
                .andExpect(redirectedUrl("/posts/" + createPostDto().id()));
        then(postService).should().createPost(any(PostWithHashtagsDto.class));
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

    private LikeDto createLikeDto(Long id) {
        return LikeDto.of(
                id,
                createPostDto().id(),
                createUserAccountDto().userId()
        );
    }
    private PostCommentDto createPostCommentDto(Long id, Long parentCommentId, LocalDateTime createdAt) {
        return PostCommentDto.of(
                id,
                1L,
                createUserAccountDto(),
                parentCommentId,
                "test comment :" + id,
                createdAt,
                "ella",
                createdAt,
                "ella"
        );
    }

    private Set<PostCommentDto> createPostCommentDtos() {
        LocalDateTime now = LocalDateTime.now();
        Set<PostCommentDto> postCommentDtos = Set.of(
                createPostCommentDto(1L, null, now),
                createPostCommentDto(2L, null, now.plusDays(1L)),
                createPostCommentDto(3L, null, now.plusDays(3L)),
                createPostCommentDto(4L, null, now),
                createPostCommentDto(5L, null, now.plusDays(5L)),
                createPostCommentDto(6L, null, now.plusDays(4L)),
                createPostCommentDto(7L, null, now.plusDays(2L)),
                createPostCommentDto(8L, null, now.plusDays(7L))
        );
        return postCommentDtos;
    }

    private HashtagDto createHashtagDto(Long id, String hashtagName) {
        return HashtagDto.of(
                id,
                hashtagName
        );
    }

    private Set<HashtagDto> createHashtagDtos() {
        Set<HashtagDto> hashtagDtos = Set.of(
                createHashtagDto(1L, "1"),
                createHashtagDto(2L, "2"),
                createHashtagDto(3L, "3"),
                createHashtagDto(4L, "4"),
                createHashtagDto(5L, "5")
        );
        return hashtagDtos;
    }

    private PostWithLikesAndHashtagsDto createPostWithLikesAndHashtagsDto() {
        return PostWithLikesAndHashtagsDto.of(
                createPostDto(),
                createUserAccountDto(),
                Set.of(createLikeDto(1L)),
                createHashtagDtos()
        );
    }
    private PostWithLikesAndHashtagsAndCommentsDto createPostWithLikesAndHashtagsAndCommentDto() {
        return PostWithLikesAndHashtagsAndCommentsDto.of(
                createPostDto(),
                createUserAccountDto(),
                Set.of(createLikeDto(1L)),
                createPostCommentDtos(),
                createHashtagDtos()
        );
    }
}