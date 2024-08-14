package com.project.sns.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "\"like\"")
public class Like {
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
}
