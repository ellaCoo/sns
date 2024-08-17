package com.project.sns.controller;

import com.project.sns.config.TestSecurityConfig;
import com.project.sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("컨트롤러 로직 - Post")
@Import(TestSecurityConfig.class)
@WebMvcTest(PostController.class) //MVC 관련 빈들만 로드함 - 테스트 실행이 빠르고, 메모리 사용량 적음 | 서비스, 리포지토리 등의 빈은 로드되지 않음
class PostControllerTest {

    private final MockMvc mvc; //HTTP 요청을 모의(mock)하여 컨트롤러의 동작을 테스트 | 실제 웹 서버를 띄우지 않고도 컨트롤러의 요청-응답 흐름을 테스트할 수 있음

    // 필드주입
    @MockBean // ->MockBean이 생성자 주입 지원하지 않음
    private PostService postService;

    // 생성자주입
    PostControllerTest(
            @Autowired MockMvc mvc
    ) {
        this.mvc = mvc;
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
}