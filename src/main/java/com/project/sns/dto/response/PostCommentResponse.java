package com.project.sns.dto.response;

import java.time.LocalDateTime;

public record PostCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        String userId,
        Long parentCommentId
) {
    // 정적 팩토리 메서드, 객체 생성과 초기화를 동시에 처리하여 새로운 ArticleCommentResponse 객체를 반환
    public static PostCommentResponse of(Long id, String content, LocalDateTime createdAt, String email, String nickname, String userId) {
        return new PostCommentResponse(id, content, createdAt, email, nickname, userId, null);
    }

    public static PostCommentResponse fromDto(PostCommentDto dto) {
        return PostCommentResponse.of(
                dto.id(), // 댓글 ID
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().email(),
                dto.userAccountDto().nickname(),
                dto.userAccountDto().userId()
        );
    }
}
