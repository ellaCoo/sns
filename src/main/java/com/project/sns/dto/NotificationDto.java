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
        UserAccountDto occurUserAccountDto,
        LocalDateTime createdAt,
        String createdBy
) {
    public static NotificationDto of(UserAccountDto userAccountDto, NotificationType notificationType, Long targetId, UserAccountDto occurUserAccountDto) {
        return NotificationDto.of(null, userAccountDto, notificationType, targetId, occurUserAccountDto, null, null);
    }

    public static NotificationDto of(Long id, UserAccountDto userAccountDto, NotificationType notificationType, Long targetId, UserAccountDto occurUserAccountDto, LocalDateTime createdAt, String createdBy) {
        return new NotificationDto(id, userAccountDto, notificationType, targetId, occurUserAccountDto, createdAt, createdBy);
    }

    public static NotificationDto fromEntity(Notification entity, UserAccount occurUserAccount) {
        return new NotificationDto(
                entity.getId(),
                UserAccountDto.fromEntity(entity.getUserAccount()),
                entity.getNotificationType(),
                entity.getTargetId(),
                UserAccountDto.fromEntity(occurUserAccount),
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
