package com.project.sns.dto.response;

import com.project.sns.dto.PostCommentDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PostCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Long parentCommentId,
        Set<PostCommentResponse> childComments
) {
    // of: 정적 팩토리 메서드, 객체 생성과 초기화를 동시에 처리하여 새로운 PostCommentResponse 객체를 반환
    public static PostCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
        return PostCommentResponse.of(id, content, createdAt, email, nickname, userId, null);
    }

    public static PostCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId, Long parentCommentId) {
        Comparator<PostCommentResponse> childCommentComparator = Comparator
                .comparing(PostCommentResponse::createdAt) // 생성시간 오름차순
                .thenComparingLong(PostCommentResponse::id); // 생성시간 동일할때 정렬 기준
        return new PostCommentResponse(id, content, createdAt, email, nickname, userId, parentCommentId, new TreeSet<>(childCommentComparator));
    }

    public static PostCommentResponse fromDto(PostCommentDto dto) {
        return PostCommentResponse.of(
                dto.id(), // 댓글 ID
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                dto.userAccountDto().nickname(),
                dto.userAccountDto().userId(),
                dto.parentCommentId()
        );
    }
}
