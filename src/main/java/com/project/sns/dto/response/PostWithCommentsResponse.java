package com.project.sns.dto.response;

import com.project.sns.dto.PostCommentDto;
import com.project.sns.dto.PostDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PostWithCommentsResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Set<PostCommentResponse> postCommentsResponse
) {
    public static PostWithCommentsResponse of(Long id, String title, String content, LocalDateTime createdAt, String email, String nickname, String userId, Set<PostCommentResponse> postCommentsResponses) {
        return new PostWithCommentsResponse(id, title, content, createdAt, email, nickname, userId, postCommentsResponses);
    }

    public static PostWithCommentsResponse fromDto(PostDto postDto, Set<PostCommentDto> postCommentDtos) {
        return new PostWithCommentsResponse(
                postDto.id(),
                postDto.title(),
                postDto.content(),
                postDto.createdAt(),
                postDto.userAccountDto().email(),
                postDto.userAccountDto().nickname(),
                postDto.userAccountDto().userId(),
                organizeChildComments(postCommentDtos)
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
