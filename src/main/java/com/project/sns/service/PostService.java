package com.project.sns.service;

import com.project.sns.domain.Post;
import com.project.sns.domain.UserAccount;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.UserAccountDto;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
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
    private final UserAccountRepository userAccountRepository;
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
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

            if (post.getUserAccount().equals(userAccount)) {
                if (dto.title() != null) post.setTitle(dto.title());
                if (dto.content() != null) post.setContent(dto.content());
            }
            // TODO: hashtag 기능 추가 시 함께 업데이트 되도록
        } catch (EntityNotFoundException e) {
            log.warn("포스트 업데이트 실패. 포스트를 수정하는데 필요한 정보를 찾을 수 없습니다.");
        }
    }

    public void deletePost(Long postId, String userId) {
        // TODO: hashtag 기능 추가 시 함께 삭제 되도록
        postRepository.deleteByIdAndUserAccount_UserId(postId, userId);
    }

    public PostDto createPost(PostDto dto) {
        // TODO: hashtag 기능 추가 시 함께 저장 되도록
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());

        Post post = dto.toEntity(userAccount);
        post = postRepository.save(post);
        return PostDto.fromEntity(post);
    }
}
