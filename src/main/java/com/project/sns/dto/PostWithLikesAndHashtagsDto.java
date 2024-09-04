package com.project.sns.dto;

import com.project.sns.domain.Post;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PostWithLikesAndHashtagsDto(
        PostDto postDto,
        UserAccountDto userAccountDto,
        Set<LikeDto> likeDtos
) {
    public static PostWithLikesAndHashtagsDto of(PostDto postDto, UserAccountDto userAccountDto, Set<LikeDto> likeDtos) {
        return new PostWithLikesAndHashtagsDto(postDto, userAccountDto, likeDtos);
    }

    public static PostWithLikesAndHashtagsDto fromEntity(Post entity) {
        return new PostWithLikesAndHashtagsDto(
                PostDto.fromEntity(entity),
                com.project.sns.dto.UserAccountDto.fromEntity(entity.getUserAccount()),
                entity.getLikes().stream()
                        .map(LikeDto::fromEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
        );
    }
}
