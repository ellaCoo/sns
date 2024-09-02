package com.project.sns.controller;

import com.project.sns.domain.constant.FormStatus;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.request.PostRequest;
import com.project.sns.dto.PostCommentDto;
import com.project.sns.dto.response.PostCommentResponse;
import com.project.sns.dto.response.PostResponse;
import com.project.sns.dto.response.PostWithCommentsResponse;
import com.project.sns.dto.security.BoardPrincipal;
import com.project.sns.service.PostCommentService;
import com.project.sns.service.PostService;
import com.project.sns.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/posts")
@Controller
public class PostController {

    private final PostService postService;
    private final PostCommentService postCommentService;
    private final UserAccountService userAccountService;

    @GetMapping
    public String postsPage(ModelMap map) {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        Page<PostResponse> posts = postService.getPosts(pageable).map(PostResponse::fromDto);
        map.addAttribute("posts", posts);
        return "posts/index";
    }

    @PostMapping
    @ResponseBody
    public Page<PostResponse> posts(@RequestParam("page") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "createdAt");
        return postService.getPosts(pageable).map(PostResponse::fromDto);
    }

    @GetMapping("/{postId}")
    public String postPage(
            @PathVariable Long postId,
            ModelMap map) {
        PostWithCommentsResponse postWithComments = PostWithCommentsResponse
                .fromDto(postService.getPost(postId), postCommentService.searchPostComments(postId));
        map.addAttribute("postWithComments", postWithComments);

        return "posts/detail";
    }

    @GetMapping("/{postId}/edit")
    public String updatePostPage(
            @PathVariable Long postId,
            ModelMap map) {
        PostResponse post = PostResponse.fromDto(postService.getPost(postId));
        map.addAttribute("post", post);
        map.addAttribute("formStatus", FormStatus.UPDATE);
        return "posts/form";
    }

    @PostMapping("/{postId}/edit")
    public String updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            PostRequest postRequest,
            ModelMap map) {
        postService.updatePost(postId, postRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        postService.deletePost(postId, boardPrincipal.getUsername());

        return "redirect:/posts";
    }

    @GetMapping("/form")
    public String createPostPage(ModelMap map) {
        PostResponse post = PostResponse.of("");
        map.addAttribute("post", post);
        map.addAttribute("formStatus", FormStatus.CREATE);
        return "posts/form";
    }

    @PostMapping("/form")
    public String createPost(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            PostRequest postRequest,
            ModelMap map
    ) {
        PostDto postDto = postService.createPost(postRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/posts/" + postDto.id();
    }

    @GetMapping("/myfeed")
    public String myPostsPage(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            ModelMap map) {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        Page<PostResponse> posts = postService.getPosts(boardPrincipal.toDto(), pageable).map(PostResponse::fromDto);
        map.addAttribute("posts", posts);
        return "posts/index";
    }

    @PostMapping("/myfeed")
    @ResponseBody
    public Page<PostResponse> myPosts(
            @RequestParam("page") int page,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "createdAt");
        return postService.getPosts(pageable).map(PostResponse::fromDto);
    }
}
