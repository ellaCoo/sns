package com.project.sns.dto;

import com.project.sns.domain.Post;
import com.project.sns.domain.UserAccount;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record PostDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static PostDto of(UserAccountDto userAccountDto, String title, String content) {
        return new PostDto(null, userAccountDto, title, content, null, null, null, null);
    }
    public static PostDto fromEntity(Post entity) {
        return new PostDto(
                entity.getId(),
                UserAccountDto.fromEntity(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Post toEntity(UserAccount userAccount) {
        return Post.of(
                userAccount,
                title,
                content
        );
    }
}
