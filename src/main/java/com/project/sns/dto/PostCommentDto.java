package com.project.sns.dto;

import com.project.sns.domain.Post;
import com.project.sns.domain.PostComment;
import com.project.sns.domain.UserAccount;

import java.time.LocalDateTime;

public record PostCommentDto(
        Long id,
        Long postId,
        UserAccountDto userAccountDto,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static PostCommentDto of(Long postId, UserAccountDto userAccountDto, String content) {
        return PostCommentDto.of(postId, userAccountDto, null, content);
    }

    public static PostCommentDto of(Long postId, UserAccountDto userAccountDto, Long parentCommentId, String content) {
        return PostCommentDto.of(postId, userAccountDto, parentCommentId, content, null, null, null, null);
    }

    public static PostCommentDto of(Long postId, UserAccountDto userAccountDto, Long parentCommentId, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return PostCommentDto.of(null, postId, userAccountDto, parentCommentId, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static PostCommentDto of(Long id, Long postId, UserAccountDto userAccountDto, Long parentCommentId, String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new PostCommentDto(id, postId, userAccountDto, parentCommentId, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static PostCommentDto fromEntity(PostComment entity) {
        return new PostCommentDto(
                entity.getId(),
                entity.getPost().getId(),
                UserAccountDto.fromEntity(entity.getUserAccount()),
                entity.getParentCommentId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public PostComment toEntity(Post post, UserAccount userAccount) {
        return PostComment.of(
                post,
                userAccount,
                content
        );
    }
}
