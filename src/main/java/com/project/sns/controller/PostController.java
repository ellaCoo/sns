package com.project.sns.controller;

import com.project.sns.domain.constant.FormStatus;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.request.PostRequest;
import com.project.sns.dto.response.PostResponse;
import com.project.sns.dto.response.PostWithLikesAndHashtagAndCommentsResponse;
import com.project.sns.dto.response.PostWithLikesAndHashtagsResponse;
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

@RequiredArgsConstructor
@RequestMapping("/posts")
@Controller
public class PostController {

    private final PostService postService;
    private final PostCommentService postCommentService;
    private final UserAccountService userAccountService;

    private Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
    @GetMapping
    public String postsPage(
            ModelMap map,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        String user = null == boardPrincipal ? null : boardPrincipal.getUsername();
        Page<PostWithLikesAndHashtagsResponse> response = postService.getPosts(pageable)
                .map(res -> PostWithLikesAndHashtagsResponse.fromDto(res, user));

        map.addAttribute("posts", response);
        return "posts/index";
    }

    @PostMapping
    @ResponseBody
    public Page<PostWithLikesAndHashtagsResponse> posts(
            @RequestParam("page") int page,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        String user = null == boardPrincipal ? null : boardPrincipal.getUsername();
        Page<PostWithLikesAndHashtagsResponse> response = postService.getPosts(pageable.withPage(page))
                .map(res -> PostWithLikesAndHashtagsResponse.fromDto(res, user));
        return response;
    }

    @GetMapping("/{postId}")
    public String postPage(
            @PathVariable Long postId,
            ModelMap map,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        String user = null == boardPrincipal ? null : boardPrincipal.getUsername();
        PostWithLikesAndHashtagAndCommentsResponse response = PostWithLikesAndHashtagAndCommentsResponse
                .fromDto(postService.getPostWithLikesAndHashtagsAndComments(postId), user);

        map.addAttribute("post", response);
        return "posts/detail";
    }

    @GetMapping("/{postId}/edit")
    public String updatePostPage(
            @PathVariable Long postId,
            ModelMap map
    ) {
        PostResponse response = PostResponse.fromDto(postService.getPost(postId));

        map.addAttribute("post", response);
        map.addAttribute("formStatus", FormStatus.UPDATE);
        return "posts/form";
    }

    @PostMapping("/{postId}/edit")
    public String updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            PostRequest postRequest
    ) {
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
        String user = null == boardPrincipal ? null : boardPrincipal.getUsername();
        Page<PostWithLikesAndHashtagsResponse> response = postService.getPosts(boardPrincipal.toDto(), pageable)
                .map(res -> PostWithLikesAndHashtagsResponse.fromDto(res, user));
        map.addAttribute("posts", response);
        return "posts/index";
    }

    @PostMapping("/myfeed")
    @ResponseBody
    public Page<PostWithLikesAndHashtagsResponse> myPosts(
            @RequestParam("page") int page,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        String user = null == boardPrincipal ? null : boardPrincipal.getUsername();
        Page<PostWithLikesAndHashtagsResponse> response = postService.getPosts(boardPrincipal.toDto(), pageable.withPage(page))
                .map(res -> PostWithLikesAndHashtagsResponse.fromDto(res, user));
        return response;
    }
}
