package com.project.sns.dto.request;

import com.project.sns.domain.Post;
import com.project.sns.dto.HashtagDto;
import com.project.sns.dto.PostDto;
import com.project.sns.dto.PostWithHashtagsDto;
import com.project.sns.dto.UserAccountDto;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record PostRequest(
        String title,
        String content,
        String hashtag
) {
    public static PostRequest of(String title, String content, String hashtag) {
        return new PostRequest(title, content, hashtag);
    }

    public PostWithHashtagsDto toDto(UserAccountDto userAccountDto) {
        PostDto postDto = PostDto.of(userAccountDto, title, content);
        Set<HashtagDto> hashtagDtos = Arrays.stream(hashtag.split(","))
                .map(tag -> HashtagDto.of(tag.trim())).collect(Collectors.toSet());
        return PostWithHashtagsDto.of(
                postDto,
                userAccountDto,
                hashtagDtos
        );
    }
}
