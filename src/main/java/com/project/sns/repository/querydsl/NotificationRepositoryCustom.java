package com.project.sns.repository.querydsl;

import com.project.sns.domain.Notification;
import com.project.sns.domain.Post;
import com.project.sns.domain.constant.NotificationType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface NotificationRepositoryCustom {

    Optional<Post> findPostByNotificationId(Long id);

    List<Notification> findByUserIdSortingCreatedAtDesc(String userId);

    void deleteNotificationByPostId(Long postId);
}
