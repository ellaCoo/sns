package com.project.sns.dto.response;

import com.project.sns.dto.PostWithLikesAndHashtagsDto;

import java.util.Set;
import java.util.stream.Collectors;

public record PostWithLikesAndHashtagsResponse(
        PostResponse postResponse,
        String email,
        String nickname,
        String userId,
        boolean isLike,
        Set<String> likeUserId,
        Set<String> hashtags
) {
    public static PostWithLikesAndHashtagsResponse of(PostResponse postResponse, String email, String nickname, String userId, boolean isLike, Set<String> likeUserId, Set<String> hashtags) {
        return new PostWithLikesAndHashtagsResponse(postResponse, email, nickname, userId, isLike, likeUserId, hashtags);
    }

    public static PostWithLikesAndHashtagsResponse fromDto(PostWithLikesAndHashtagsDto dto, String userId) {
        Set<String> likes = dto.likeDtos().stream().map(likeDto -> likeDto.userId()).collect(Collectors.toSet());
        Set<String> hashtags = dto.hashtagDtos().stream().map(hashtagDto -> hashtagDto.hashtagName()).collect(Collectors.toSet());
        return new PostWithLikesAndHashtagsResponse(
                PostResponse.fromDto(dto.postDto()),
                dto.userAccountDto().email(),
                dto.userAccountDto().nickname(),
                dto.userAccountDto().userId(),
                null != userId && likes.contains(userId),
                likes,
                hashtags
        );
    }
}
