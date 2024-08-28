package com.project.sns.service;

import com.project.sns.domain.Post;
import com.project.sns.dto.PostDto;
import com.project.sns.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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

    @Transactional(readOnly = true)
    public PostDto getPost(Long postId) {
        return postRepository.findById(postId)
                .map(PostDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("포스트가 없습니다 - postId: " + postId));
    }

    public void updatePost(Long postId, PostDto dto) {
        try {
            Post post = postRepository.getReferenceById(postId);
            if (dto.title() != null) {
                post.setTitle(dto.title());
            }
            if (dto.content() != null) {
                post.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            log.warn("포스트 업데이트 실패. 포스트를 수정하는데 필요한 정보를 찾을 수 없습니다.");
        }
    }

    public void deletePost(long postId, String userId) {
        // TODO: hashtag 기능 추가 시 함께 삭제 되도록
        postRepository.deleteByIdAndUserAccount_UserId(postId, userId);
    }
}
