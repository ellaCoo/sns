package com.project.sns.dto;

import com.project.sns.domain.Like;

import java.time.LocalDateTime;

public record LikeDto(
        Long id,
        UserAccountDto userAccountDto,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static LikeDto of(Long id, UserAccountDto userAccountDto) {
        return new LikeDto(id, userAccountDto, null, null, null, null);
    }

    public static LikeDto fromEntity(Like entity) {
        return new LikeDto(
                entity.getId(),
                UserAccountDto.fromEntity(entity.getUserAccount()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }
}
