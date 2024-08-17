package com.project.sns.controller;

import com.project.sns.dto.response.PostResponse;
import com.project.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/posts")
@Controller
public class PostController {

    private final PostService postService;

    @GetMapping
    public String posts(ModelMap map) {
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        Page<PostResponse> posts = postService.getPosts(pageable).map(PostResponse::fromDto);
        map.addAttribute("posts", posts);
        return "posts/index";
    }
    @PostMapping
    @ResponseBody
    public Page<PostResponse> getPosts(@RequestParam("page") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.Direction.DESC, "createdAt");
        return postService.getPosts(pageable).map(PostResponse::fromDto);
    }
}
