package com.project.sns.controller;

import com.project.sns.config.TestSecurityConfig;
import com.project.sns.dto.PostCommentDto;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.request.PostCommentRequest;
import com.project.sns.dto.request.PostRequest;
import com.project.sns.service.PostCommentService;
import com.project.sns.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("컨트롤러 로직 - PostComment")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(PostCommentController.class)
class PostCommentControllerTest {
    private final MockMvc mvc;
    private final FormDataEncoder formDataEncoder;

    @MockBean private PostCommentService postCommentService;

    PostCommentControllerTest(
            @Autowired MockMvc mvc,
            @Autowired FormDataEncoder formDataEncoder
    ) {
        this.mvc = mvc;
        this.formDataEncoder = formDataEncoder;
    }

    @WithUserDetails(value = "ellaTest", setupBefore = TestExecutionEvent.TEST_EXECUTION) // TestSecurityConfig에서 지정한 유저정보 사용 가능 | TEST_EXECUTION:테스트 직전에 셋업해라
    @DisplayName("[view][POST] 댓글 삭제 - 정상 호출")
    @Test
    void givenPostCommentIdToDelete_whenRequesting_thenDeletesPostComment() throws Exception {
        // Given
        Long postId = 1L; //테스트할 id
        Long postCommentId = 1L;
        String userId = "ellaTest";
        willDoNothing().given(postCommentService).deletePostComment(postCommentId, userId);

        // When & Then
        mvc.perform(
                        post("/comments/" + postId + "/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(Map.of("postId", postId)))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts/" + postId))
                .andExpect(redirectedUrl("/posts/" + postId));
        then(postCommentService).should().deletePostComment(postCommentId, userId);
    }

    @WithUserDetails(value = "ellaTest", setupBefore = TestExecutionEvent.TEST_EXECUTION) // TestSecurityConfig에서 지정한 유저정보 사용 가능 | TEST_EXECUTION:테스트 직전에 셋업해라
    @DisplayName("[view][POST] 댓글 등록 - 정상 호출")
    @Test
    void givenPostCommentInfo_whenRequesting_thenCreateNewPostComment() throws Exception {
        // Given
        Long postId = 1L; //테스트할 id
        PostCommentRequest request = PostCommentRequest.of(postId, "test comment");
        willDoNothing().given(postCommentService).createPostComment(any(PostCommentDto.class));

        // When & Then
        mvc.perform(
                        post("/comments/new")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))// requestObject를 form data 형식으로 인코딩 ( key value에 %, &로 연결)
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/posts/" + postId))
                .andExpect(redirectedUrl("/posts/" + postId));
        then(postCommentService).should().createPostComment(any(PostCommentDto.class));
    }


}