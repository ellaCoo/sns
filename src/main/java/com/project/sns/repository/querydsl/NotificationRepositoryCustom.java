package com.project.sns.repository.querydsl;

import com.project.sns.domain.Notification;
import com.project.sns.domain.Post;

import java.util.List;
import java.util.Optional;

public interface NotificationRepositoryCustom {

    Optional<Post> findPostByNotificationId(Long id);

    List<Notification> findByUserIdSortingCreatedAtDesc(String userId);
}
