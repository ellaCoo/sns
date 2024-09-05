package com.project.sns.dto;

import com.project.sns.domain.Post;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record PostWithLikesAndHashtagsAndCommentsDto(
        PostDto postDto,
        UserAccountDto userAccountDto,
        Set<LikeDto> likeDtos,
        Set<PostCommentDto> postCommentDtos,
        Set<HashtagDto> hashtagDtos
) {
    public static PostWithLikesAndHashtagsAndCommentsDto of(PostDto postDto, UserAccountDto userAccountDto, Set<LikeDto> likeDtos, Set<PostCommentDto> postCommentDtos, Set<HashtagDto> hashtagDtos) {
        return new PostWithLikesAndHashtagsAndCommentsDto(postDto, userAccountDto, likeDtos, postCommentDtos, hashtagDtos);
    }

    public static PostWithLikesAndHashtagsAndCommentsDto fromEntity(Post entity) {
        return new PostWithLikesAndHashtagsAndCommentsDto(
                PostDto.fromEntity(entity),
                com.project.sns.dto.UserAccountDto.fromEntity(entity.getUserAccount()),
                entity.getLikes().stream()
                        .map(LikeDto::fromEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                ,
                entity.getPostComments().stream()
                        .map(PostCommentDto::fromEntity)
                        .collect(Collectors.toCollection(LinkedHashSet::new))
                ,
                entity.getPostHashtags().stream()
                        .map(postHashtag -> postHashtag.getHashtag())
                        .map(HashtagDto::fromEntity)
                        .collect(Collectors.toSet())
        );
    }
}