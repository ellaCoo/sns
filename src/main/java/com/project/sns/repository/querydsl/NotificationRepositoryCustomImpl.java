package com.project.sns.repository.querydsl;

import com.project.sns.domain.*;
import com.project.sns.domain.constant.NotificationType;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.*;

public class NotificationRepositoryCustomImpl extends QuerydslRepositorySupport implements NotificationRepositoryCustom {

    public NotificationRepositoryCustomImpl() {
        super(Notification.class);
    }

    @Override
    public Optional<Post> findPostByNotificationId(Long id) {
        QNotification notification = QNotification.notification;
        QPost post = QPost.post;
        QLike like = QLike.like;
        QPostComment postComment = QPostComment.postComment;

        Notification fetchedNotification = from(notification)
                .select(notification)
                .where(notification.id.eq(id))
                .fetchOne();

        // NotificationType에 따라 동적 조인
        switch (fetchedNotification.getNotificationType()) {
            case NEW_LIKE_ON_POST:
                return Optional.ofNullable(from(like)
                        .select(post)
                        .join(like.post, post)
                        .where(like.id.eq(fetchedNotification.getTargetId()))
                        .fetchOne());
            case NEW_COMMENT_ON_POST:
                return Optional.ofNullable(from(postComment)
                        .select(post)
                        .join(postComment.post, post)
                        .where(postComment.id.eq(fetchedNotification.getTargetId()))
                        .fetchOne());
        }
        return Optional.empty();
    }

    @Override
    public List<Notification> findByUserIdSortingCreatedAtDesc(String userId) {
        QNotification notification = QNotification.notification;

        return from(notification)
                .select(notification)
                .where(notification.userAccount.userId.eq(userId))
                .orderBy(notification.createdAt.desc())  // createdAt 내림차순 정렬
                .fetch();
    }

    @Override
    public void deleteNotificationByPostId(Long postId) {
        QNotification notification = QNotification.notification;
        QPost post = QPost.post;
        QLike like = QLike.like;
        QPostComment postComment = QPostComment.postComment;

        Arrays.stream(NotificationType.values()).forEach(notiType -> {
            switch (notiType) {
                case NEW_LIKE_ON_POST:
                    List<Long> likeIds = from(like)
                            .select(like.id)
                            .join(like.post, post)
                            .where(post.id.eq(postId))
                            .fetch();
                    long deletedNotificationsLike = new JPADeleteClause(getEntityManager(), notification)
                            .where(notification.notificationType.eq(NotificationType.NEW_LIKE_ON_POST)
                                    .and(notification.targetId.in(likeIds)))
                            .execute();
                    break;
                case NEW_COMMENT_ON_POST:
                    List<Long> postCommentId = from(postComment)
                            .select(postComment.id)
                            .join(postComment.post, post)
                            .where(post.id.eq(postId))
                            .fetch();
                    long deletedNotificationsComment = new JPADeleteClause(getEntityManager(), notification)
                            .where(notification.notificationType.eq(NotificationType.NEW_COMMENT_ON_POST)
                                    .and(notification.targetId.in(postCommentId)))
                            .execute();
                  break;
            }
        });
    }
}
