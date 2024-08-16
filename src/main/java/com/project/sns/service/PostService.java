package com.project.sns.service;

import com.project.sns.dto.PostDto;
import com.project.sns.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostService {
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Page<PostDto> getPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostDto::fromEntity);
    }
}
