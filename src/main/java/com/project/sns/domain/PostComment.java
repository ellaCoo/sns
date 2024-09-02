package com.project.sns.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
public class PostComment extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "postId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Post post;

    @Setter
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserAccount userAccount;

    @Setter
    @Column(updatable = false) // 부모댓글 한번 셋팅했으면 그 뒤로는 업데이트 대상에서 제외 (자신의 부모댓글이 바뀌는 경우 없음)
    private Long parentCommentId;

    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private Set<PostComment> childComments = new LinkedHashSet<>();

    @Setter
    @Column(nullable = false, length = 500)
    private String content; // 본문

    protected PostComment() {}

    private PostComment(Post post, UserAccount userAccount, Long parentCommentId, String content) {
        this.post = post;
        this.userAccount = userAccount;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    public static PostComment of(Post post, UserAccount userAccount, String content) {
        return new PostComment(post, userAccount, null, content);
    }

    public void addChildComment(PostComment child) {
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostComment that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
