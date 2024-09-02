package com.project.sns.service;

import com.project.sns.domain.Post;
import com.project.sns.domain.PostComment;
import com.project.sns.domain.UserAccount;
import com.project.sns.dto.PostCommentDto;
import com.project.sns.repository.PostCommentRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostCommentService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Set<PostCommentDto> searchPostComments(Long postId) {
        return postCommentRepository.findByPost_Id(postId)
                .stream()
                .map(PostCommentDto::fromEntity)
                .collect(Collectors.toSet());
    }

    public void deletePostComment(Long commentId, String userId) {
        postCommentRepository.deleteByIdAndUserAccount_userId(commentId, userId);
    }

    public void createPostComment(PostCommentDto dto) {
        try {
            Post post = postRepository.getReferenceById(dto.postId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            PostComment postComment = dto.toEntity(post, userAccount);

            if (dto.parentCommentId() != null) {
                PostComment parentComment = postCommentRepository.getReferenceById(dto.parentCommentId());
                parentComment.addChildComment(postComment);
            } else {
                postCommentRepository.save(postComment);
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패. 댓글 작성에 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
        }

    }
}
