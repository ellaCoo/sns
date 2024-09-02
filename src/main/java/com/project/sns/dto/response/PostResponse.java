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
        String userId,
        String nickname,
        LocalDateTime modifiedAt
) {

    public static PostResponse of(String s) {
        return PostResponse.of(null, s, s, null, s, s, null);
    }
    public static PostResponse of(Long id, String title, String content, LocalDateTime createdAt, String userId, String nickname, LocalDateTime modifiedAt) {
        return new PostResponse(id, title, content, createdAt, userId, nickname, modifiedAt);
    }

    public static PostResponse fromDto(PostDto dto) {
        return PostResponse.of(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().userId(),
                dto.userAccountDto().nickname(),
                dto.modifiedAt()
        );
    }
}
