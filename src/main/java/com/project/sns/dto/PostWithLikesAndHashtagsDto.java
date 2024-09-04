package com.project.sns.dto;

import com.project.sns.domain.Hashtag;
import com.project.sns.domain.Post;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PostWithLikesAndHashtagsDto(
        PostDto postDto,
        UserAccountDto userAccountDto,
        Set<LikeDto> likeDtos,
        Set<HashtagDto> hashtagDtos
) {
    public static PostWithLikesAndHashtagsDto of(PostDto postDto, UserAccountDto userAccountDto, Set<LikeDto> likeDtos, Set<HashtagDto> hashtagDtos) {
        return new PostWithLikesAndHashtagsDto(postDto, userAccountDto, likeDtos, hashtagDtos);
    }

    public static PostWithLikesAndHashtagsDto fromEntity(Post entity) {
        return new PostWithLikesAndHashtagsDto(
                PostDto.fromEntity(entity),
                com.project.sns.dto.UserAccountDto.fromEntity(entity.getUserAccount()),
                entity.getLikes().stream()
                        .map(LikeDto::fromEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                ,
                entity.getPostHashtags().stream()
                        .map(postHashtag -> postHashtag.getHashtag())
                        .map(HashtagDto::fromEntity)
                        .collect(Collectors.toSet())
        );
    }
}
