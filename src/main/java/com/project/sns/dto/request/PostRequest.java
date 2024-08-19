package com.project.sns.dto.request;

import com.project.sns.domain.Post;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.UserAccountDto;

public record PostRequest(
        String title,
        String content
) {
    public static PostRequest of(String title, String content) {
        return new PostRequest(title,content);
    }

    public PostDto toDto(UserAccountDto userAccountDto) {
        return PostDto.of(
                userAccountDto,
                title,
                content
        );
    }
}
