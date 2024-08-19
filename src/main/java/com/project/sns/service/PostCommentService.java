package com.project.sns.service;

import com.project.sns.dto.PostDto;
import com.project.sns.dto.response.PostCommentDto;
import com.project.sns.repository.PostCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;

    @Transactional(readOnly = true)
    public List<PostCommentDto> searchPostComments(Long postId) {
        return postCommentRepository.findByPost_Id(postId)
                .stream()
                .map(PostCommentDto::fromEntity)
                .toList();
    }
}
