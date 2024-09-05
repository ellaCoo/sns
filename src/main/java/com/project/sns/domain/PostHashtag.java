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
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Post post;

    @Setter
    @JoinColumn(name = "hashtagId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Hashtag hashtag;

    protected PostHashtag() {}

    private PostHashtag(Post post, Hashtag hashtag) {
        this.post = post;
        this.hashtag = hashtag;
    }

    public static PostHashtag of(Post post, Hashtag hashtag) {
        return new PostHashtag(post, hashtag);
    }

}
