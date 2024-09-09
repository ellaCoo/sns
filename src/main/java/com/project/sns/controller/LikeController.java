package com.project.sns.controller;

import com.project.sns.dto.LikeDto;
import com.project.sns.dto.security.SnsPrincipal;
import com.project.sns.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.net.URISyntaxException;

@RequiredArgsConstructor
@RequestMapping("/like")
@Controller
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{postId}/create")
    public String createLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal SnsPrincipal snsPrincipal,
            HttpServletRequest request
    ) throws URISyntaxException {
        String path = new URI(request.getHeader("Referer")).getPath();
        likeService.createLike(LikeDto.of(postId, snsPrincipal.getUsername()));

        return "redirect:" + path;
    }

    @PostMapping("/{postId}/delete")
    public String deleteLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal SnsPrincipal snsPrincipal,
            HttpServletRequest request
    ) throws URISyntaxException {
        String path = new URI(request.getHeader("Referer")).getPath();
        likeService.deleteLike(LikeDto.of(postId, snsPrincipal.getUsername()));
        return "redirect:" + path;
    }
}
