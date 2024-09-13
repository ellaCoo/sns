package com.project.sns.dto;

import com.project.sns.domain.Notification;
import com.project.sns.domain.UserAccount;
import com.project.sns.domain.constant.NotificationType;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        UserAccountDto userAccountDto,
        NotificationType notificationType,
        Long targetId,
        String occurUserId,
        LocalDateTime createdAt,
        String createdBy
) {
    public static NotificationDto of(UserAccountDto userAccountDto, NotificationType notificationType, Long targetId, String occurUserId) {
        return NotificationDto.of(null, userAccountDto, notificationType, targetId, occurUserId, null, null);
    }

    public static NotificationDto of(Long id, UserAccountDto userAccountDto, NotificationType notificationType, Long targetId, String occurUserId, LocalDateTime createdAt, String createdBy) {
        return new NotificationDto(id, userAccountDto, notificationType, targetId, occurUserId, createdAt, createdBy);
    }

    public static NotificationDto fromEntity(Notification entity) {
        return new NotificationDto(
                entity.getId(),
                UserAccountDto.fromEntity(entity.getUserAccount()),
                entity.getNotificationType(),
                entity.getTargetId(),
                entity.getOccurUserId(),
                entity.getCreatedAt(),
                entity.getCreatedBy()
        );
    }

    public Notification toEntity(UserAccount userAccount, NotificationType notificationType, Long targetId, String occurUserId) {
        return Notification.of(
                userAccount,
                notificationType,
                targetId,
                occurUserId
        );
    }
}
