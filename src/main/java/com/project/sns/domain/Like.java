package com.project.sns.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "\"like\"")
public class Like extends AuditingFields {
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

    protected Like() {}

    private Like(Post post, UserAccount userAccount) {
        this.post = post;
        this.userAccount = userAccount;
    }

    public static Like of(Post post, UserAccount userAccount) {
        return new Like(post, userAccount);
    }

}
