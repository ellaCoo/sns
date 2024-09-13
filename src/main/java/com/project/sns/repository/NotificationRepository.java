package com.project.sns.repository;

import com.project.sns.domain.Notification;
import com.project.sns.domain.constant.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    void deleteByNotificationTypeAndOccurUserIdAndTargetId(NotificationType notificationType, String occurUserId, Long targetId);
}
