package com.project.sns.dto.request;

import com.project.sns.dto.PostCommentDto;
import com.project.sns.dto.UserAccountDto;
import com.project.sns.dto.response.PostCommentResponse;

public record PostCommentRequest(
        Long postId,
        Long parentCommentId,
        String content
) {
    public static PostCommentRequest of(Long postId, String content) {
        return PostCommentRequest.of(postId, null, content);
    }

    public static PostCommentRequest of(Long postId, Long parentCommentId, String content) {
        return new PostCommentRequest(postId, parentCommentId, content);
    }

    public PostCommentDto toDto(UserAccountDto userAccountDto) {
        return PostCommentDto.of(
                postId,
                userAccountDto,
                parentCommentId,
                content
        );
    }

}
