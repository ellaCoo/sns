package com.project.sns.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Entity
public class Hashtag extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String hashtagName;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PostHashtag> postHashtags = new LinkedHashSet<>();

    protected Hashtag() {}

    private Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }

    public static Hashtag of(String hashtagName) {
        return new Hashtag(hashtagName);
    }
}
