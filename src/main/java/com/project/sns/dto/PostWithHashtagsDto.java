package com.project.sns.dto;

import com.project.sns.domain.Post;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PostWithHashtagsDto(
        PostDto postDto,
        UserAccountDto userAccountDto,
        Set<HashtagDto> hashtagDtos
) {
    public static PostWithHashtagsDto of(PostDto postDto, UserAccountDto userAccountDto, Set<HashtagDto> hashtagDtos) {
        return new PostWithHashtagsDto(postDto, userAccountDto, hashtagDtos);
    }
}
