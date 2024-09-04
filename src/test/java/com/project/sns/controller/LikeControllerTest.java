package com.project.sns.controller;

import com.project.sns.config.TestSecurityConfig;
import com.project.sns.dto.LikeDto;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.UserAccountDto;
import com.project.sns.service.LikeService;
import com.project.sns.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("컨트롤러 로직 - Like")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(LikeController.class) //MVC 관련 빈들만 로드함 - 테스트 실행이 빠르고, 메모리 사용량 적음 | 서비스, 리포지토리 등의 빈은 로드되지 않음
class LikeControllerTest {
    private final MockMvc mvc; //HTTP 요청을 모의(mock)하여 컨트롤러의 동작을 테스트 | 실제 웹 서버를 띄우지 않고도 컨트롤러의 요청-응답 흐름을 테스트할 수 있음
    private final FormDataEncoder formDataEncoder;

    @MockBean
    private LikeService likeService;

    LikeControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @WithUserDetails(value = "ellaTest", setupBefore = TestExecutionEvent.TEST_EXECUTION) // TestSecurityConfig에서 지정한 유저정보 사용 가능 | TEST_EXECUTION:테스트 직전에 셋업해라
    @DisplayName("[view][POST] 좋아요 생성 후 기존 호출 페이지 반환")
    @Test
    void givenPostId_whenRequestingCreateLike_thenCreateLikeAndReturnsBeforePage() throws Exception {
        // Given
        Long postId = 1L;
        String referer = "/referer/path";
        willDoNothing().given(likeService).createLike(any(LikeDto.class));

        // When & Then
        mvc.perform(
                        post("/like/" + postId + "/create")
                                .header("Referer", referer)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));
        then(likeService).should().createLike(any(LikeDto.class));
    }

    @WithUserDetails(value = "ellaTest", setupBefore = TestExecutionEvent.TEST_EXECUTION) // TestSecurityConfig에서 지정한 유저정보 사용 가능 | TEST_EXECUTION:테스트 직전에 셋업해라
    @DisplayName("[view][POST] 좋아요 삭제 후 기존 호출 페이지 반환")
    @Test
    void givenPostId_whenRequestingDeleteLike_thenDeleteLikeAndReturnsBeforePage() throws Exception {
        // Given
        Long postId = 1L;
        String referer = "/referer/path";
        willDoNothing().given(likeService).deleteLike(any(LikeDto.class));

        // When & Then
        mvc.perform(
                        post("/like/" + postId + "/delete")
                                .header("Referer", referer)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(referer));
        then(likeService).should().deleteLike(any(LikeDto.class));
    }
}