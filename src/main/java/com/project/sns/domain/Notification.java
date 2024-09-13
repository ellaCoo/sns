package com.project.sns.domain;

import com.project.sns.domain.constant.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Entity
public class Notification extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserAccount userAccount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(nullable = false)
    private Long targetId; // notificationType 의 id

    @Column(nullable = false)
    private String occurUserId; // who occurs notification

    protected Notification() {}

    private Notification(UserAccount userAccount, NotificationType notificationType, Long targetId, String occurUserId) {
        this.userAccount = userAccount;
        this.notificationType = notificationType;
        this.targetId = targetId;
        this.occurUserId = occurUserId;
    }

    public static Notification of(UserAccount userAccount, NotificationType notificationType, Long targetId, String occurUserId) {
        return new Notification(userAccount, notificationType, targetId, occurUserId);
    }

}
