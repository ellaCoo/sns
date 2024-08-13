package com.project.sns.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class PostHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JoinColumn(name = "postId")
    @ManyToOne(optional = false) // false: 연관된 엔티티가 항상 있어야 한다.
    private Post post;

    @Setter
    @JoinColumn(name = "hashtagId")
    @ManyToOne(optional = false) // false: 연관된 엔티티가 항상 있어야 한다.
    private Hashtag hashtag;

    protected PostHashtag() {}
}
