package com.project.sns.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
public class Post extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "userId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false, length = 10000)
    private String content;

    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // 연관된 엔티티도 모두 영속성 전이
    private Set<PostComment> postComments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Like> likes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostHashtag> hashtags = new LinkedHashSet<>();

    protected Post() {}

    private Post(UserAccount userAccount, String title, String content) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static Post of(UserAccount userAccount, String title, String content) {
        return new Post(userAccount, title, content);
    }
}
