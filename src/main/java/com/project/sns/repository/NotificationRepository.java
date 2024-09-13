package com.project.sns.repository;

import com.project.sns.domain.Notification;
import com.project.sns.domain.constant.NotificationType;
import com.project.sns.repository.querydsl.NotificationRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends
        JpaRepository<Notification, Long>,
        NotificationRepositoryCustom {
    void deleteByNotificationTypeAndOccurUserIdAndTargetId(NotificationType notificationType, String occurUserId, Long targetId);
    List<Notification> findByUserAccount_UserId(String userId);
}
