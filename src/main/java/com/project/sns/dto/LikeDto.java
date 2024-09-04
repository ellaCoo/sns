package com.project.sns.dto;

import com.project.sns.domain.Like;
import com.project.sns.domain.Post;
import com.project.sns.domain.PostComment;
import com.project.sns.domain.UserAccount;

import java.time.LocalDateTime;

public record LikeDto(
        Long id,
        Long postId,
        String userId,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static LikeDto of(Long postId, String userId) {
        return LikeDto.of(null, postId, userId);
    }
    public static LikeDto of(Long id, Long postId, String userId) {
        return new LikeDto(id, postId, userId, null, null, null, null);
    }

    public static LikeDto fromEntity(Like entity) {
        return new LikeDto(
                entity.getId(),
                entity.getPost().getId(),
                entity.getUserAccount().getUserId(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Like toEntity(Post post, UserAccount userAccount) {
        return Like.of(
                post,
                userAccount
        );
    }
}
