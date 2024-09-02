package com.project.sns.controller;

import com.project.sns.domain.PostComment;
import com.project.sns.dto.request.PostCommentRequest;
import com.project.sns.dto.security.BoardPrincipal;
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
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Long postId
    ) {
        postCommentService.deletePostComment(commentId, boardPrincipal.getUsername());

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/new")
    public String createNewPostComment(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            PostCommentRequest postCommentRequest
    ) {
        postCommentService.createPostComment(postCommentRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/posts/" + postCommentRequest.postId();
    }
}
