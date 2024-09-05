package com.project.sns.controller;

import com.project.sns.domain.constant.FormStatus;
import com.project.sns.dto.HashtagDto;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.request.PostRequest;
import com.project.sns.dto.response.PostResponse;
import com.project.sns.dto.response.PostWithLikesAndHashtagAndCommentsResponse;
import com.project.sns.dto.response.PostWithLikesAndHashtagsResponse;
import com.project.sns.dto.security.BoardPrincipal;
import com.project.sns.service.HashtagService;
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

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RequestMapping("/posts")
@Controller
public class PostController {

    private final PostService postService;
    private final HashtagService hashtagService;

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
            ModelMap map,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        String user = null == boardPrincipal ? null : boardPrincipal.getUsername();
        PostWithLikesAndHashtagsResponse response = PostWithLikesAndHashtagsResponse.fromDto(postService.getPost(postId), user);

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
        PostWithLikesAndHashtagsResponse response = PostWithLikesAndHashtagsResponse.of("");

        map.addAttribute("post", response);
        map.addAttribute("formStatus", FormStatus.CREATE);
        return "posts/form";
    }

    @PostMapping("/form")
    public String createPost(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            PostRequest postRequest
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

    @GetMapping("/hashtag")
    public String hashtagsPage(
            ModelMap map
    ) {
        List<String> hashtags = hashtagService.getAllHashtags().stream()
                .map(HashtagDto::hashtagName).collect(Collectors.toList());
        map.addAttribute("hashtags", hashtags);
        return "posts/hashtag";
    }

    @GetMapping("/hashtag/{hashtagName}")
    public String postsByHashtagPage(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            @PathVariable String hashtagName,
            ModelMap map
    ) {
        String user = null == boardPrincipal ? null : boardPrincipal.getUsername();
        Page<PostWithLikesAndHashtagsResponse> response = postService.getPostsByHashtagName(hashtagName, pageable)
                .map(res -> PostWithLikesAndHashtagsResponse.fromDto(res, user));

        map.addAttribute("posts", response);
        return "posts/index";
    }

    @PostMapping("/hashtag/{hashtagName}")
    @ResponseBody
    public Page<PostWithLikesAndHashtagsResponse> postsByHashtag(
            @RequestParam("page") int page,
            @PathVariable String hashtagName,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        String user = null == boardPrincipal ? null : boardPrincipal.getUsername();
        Page<PostWithLikesAndHashtagsResponse> response = postService.getPostsByHashtagName(hashtagName, pageable.withPage(page))
                .map(res -> PostWithLikesAndHashtagsResponse.fromDto(res, user));
        return response;
    }
}
