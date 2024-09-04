package com.project.sns.dto.response;

import com.project.sns.dto.PostCommentDto;
import com.project.sns.dto.PostWithLikesAndHashtagsAndCommentsDto;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PostWithLikesAndHashtagAndCommentsResponse(
        PostResponse postResponse,
        String userId,
        boolean isLike,
        Set<String> likeUserId,
        Set<PostCommentResponse> postCommentResponse,
        Set<String> hashtags
) {
    public static PostWithLikesAndHashtagAndCommentsResponse of(PostResponse postResponse, String userId, boolean isLike, Set<String> likeUserId, Set<PostCommentResponse> postCommentResponses, Set<String> hashtags) {
        return new PostWithLikesAndHashtagAndCommentsResponse(postResponse, userId, isLike, likeUserId, postCommentResponses, hashtags);
    }

    public static PostWithLikesAndHashtagAndCommentsResponse fromDto(PostWithLikesAndHashtagsAndCommentsDto dto, String userId) {
        Set<String> likes = dto.likeDtos().stream().map(likeDto -> likeDto.userId()).collect(Collectors.toSet());
        Set<String> hashtags = dto.hashtagDtos().stream().map(hashtagDto -> hashtagDto.hashtagName()).collect(Collectors.toSet());
        return new PostWithLikesAndHashtagAndCommentsResponse(
                PostResponse.fromDto(dto.postDto()),
                dto.userAccountDto().userId(),
                null != userId && likes.contains(userId),
                likes,
                organizeChildComments(dto.postCommentDtos()),
                hashtags
        );
    }

    private static Set<PostCommentResponse> organizeChildComments(Set<PostCommentDto> dtos) {
        Map<Long, PostCommentResponse> map = dtos.stream()
                .map(PostCommentResponse::fromDto)
                .collect(Collectors.toMap(PostCommentResponse::id, Function.identity()));

        map.values().stream()
                .filter(comment -> comment.parentCommentId() != null)
                .forEach(comment -> {
                    PostCommentResponse parentComment = map.get(comment.parentCommentId());
                    parentComment.childComments().add(comment);
                });

        return map.values().stream()
                .filter(comment -> comment.parentCommentId() == null)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(PostCommentResponse::createdAt)
                                .reversed()
                                .thenComparingLong(PostCommentResponse::id)
                        )
                ));
    }
}
