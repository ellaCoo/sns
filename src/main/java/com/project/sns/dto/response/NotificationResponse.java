package com.project.sns.dto.response;

import com.project.sns.domain.constant.NotificationType;
import com.project.sns.dto.NotificationDto;
import com.project.sns.dto.UserAccountDto;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String userId,
        String userNickname,
        String occurUserId,
        String occurUserNickname,
        String notificationString,
        Long targetId,
        LocalDateTime createdAt,
        String createdBy
) {
    public static NotificationResponse of(Long id, String userId, String userNickname, String occurUserId, String occurUserNickname, String notificationString, Long targetId, LocalDateTime createdAt, String createdBy) {
        return new NotificationResponse(id, userId, userNickname, occurUserId, occurUserNickname, notificationString, targetId, createdAt, createdBy);
    }

    public static NotificationResponse fromDto(NotificationDto dto) {
        String notificationString = switch (dto.notificationType()) {
            case NEW_LIKE_ON_POST -> "새 좋아요를 눌렀습니다.";
            case NEW_COMMENT_ON_POST -> "새 댓글을 남겼습니다.";
        };
        return NotificationResponse.of(
                dto.id(),
                dto.userAccountDto().userId(),
                dto.userAccountDto().nickname(),
                dto.occurUserAccountDto().userId(),
                dto.occurUserAccountDto().nickname(),
                notificationString,
                dto.targetId(),
                dto.createdAt(),
                dto.createdBy()
        );
    }
}
