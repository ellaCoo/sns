package com.project.sns.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class PostComment extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    private Post post;

    @Setter
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false)
    private UserAccount userAccount;

    @Setter
    @Column(updatable = false) // 부모댓글 한번 셋팅했으면 그 뒤로는 업데이트 대상에서 제외 (자신의 부모댓글이 바뀌는 경우 없음)
    private Long parentCommentId;

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문

    protected PostComment() {}
}
