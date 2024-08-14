package com.project.sns.dto.response;

import com.project.sns.dto.PostDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record PostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        String userId
) {
    public static PostResponse fromDto(PostDto dto) {
        return new PostResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().userId()
        );
    }
}
