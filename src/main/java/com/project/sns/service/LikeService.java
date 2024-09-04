package com.project.sns.service;

import com.project.sns.domain.Post;
import com.project.sns.domain.UserAccount;
import com.project.sns.dto.LikeDto;
import com.project.sns.repository.LikeRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    public void deleteLike(LikeDto dto) {
        likeRepository.deleteByUserAccount_userIdAndPost_Id(dto.userId(), dto.postId());
    }

    public void createLike(LikeDto dto) {
        try {
            Post post = postRepository.getReferenceById(dto.postId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userId());
            likeRepository.save(dto.toEntity(post, userAccount));
        } catch (EntityNotFoundException e) {
            log.warn("좋아요 저장 실패. 좋아요 저장에 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
        }
    }
}
