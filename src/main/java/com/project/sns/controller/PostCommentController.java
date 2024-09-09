package com.project.sns.controller;

import com.project.sns.domain.PostComment;
import com.project.sns.dto.request.PostCommentRequest;
import com.project.sns.dto.security.SnsPrincipal;
import com.project.sns.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PostMapping("/{commentId}/delete")
    public String deletePostComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal SnsPrincipal snsPrincipal,
            Long postId
    ) {
        postCommentService.deletePostComment(commentId, snsPrincipal.getUsername());

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/new")
    public String createNewPostComment(
            @AuthenticationPrincipal SnsPrincipal snsPrincipal,
            PostCommentRequest postCommentRequest
    ) {
        postCommentService.createPostComment(postCommentRequest.toDto(snsPrincipal.toDto()));

        return "redirect:/posts/" + postCommentRequest.postId();
    }
}
