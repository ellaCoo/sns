package com.project.sns.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {
    NEW_COMMENT_ON_POST("new comment"),
    NEW_LIKE_ON_POST("new like"),
    ;

    private final String notificationText;
}
