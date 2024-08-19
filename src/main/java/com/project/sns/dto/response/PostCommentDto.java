package com.project.sns.dto.response;

import com.project.sns.domain.PostComment;
import com.project.sns.dto.UserAccountDto;

import java.time.LocalDateTime;

public record PostCommentDto(
        Long id,
        Long articleId,
        UserAccountDto userAccountDto,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static PostCommentDto of(Long articleId, UserAccountDto userAccountDto, Long parentCommentId, String content) {
        return new PostCommentDto(null, articleId, userAccountDto, parentCommentId, content, null, null, null, null);
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
}
