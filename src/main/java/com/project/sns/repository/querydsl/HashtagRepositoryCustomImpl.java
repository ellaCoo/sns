package com.project.sns.repository.querydsl;

import com.project.sns.domain.Hashtag;
import com.project.sns.domain.QHashtag;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public class HashtagRepositoryCustomImpl extends QuerydslRepositorySupport implements HashtagRepositoryCustom {

    public HashtagRepositoryCustomImpl() {
        super(Hashtag.class);
    }

    @Override
    public List<Hashtag> findUnusedHashtagsByIds(Set<Long> hashtagIds) {
        // @Query("SELECT h FROM Hashtag h WHERE h.postHashtags IS EMPTY AND h.id IN :hashtagIds")
        QHashtag hashtag = QHashtag.hashtag;

        return from(hashtag)
                .select(hashtag)
                .where(hashtag.postHashtags.isEmpty()
                        .and(hashtag.id.in(hashtagIds)))
                .fetch();
    }

    @Override
    public List<Hashtag> findAllHashtagsSortedIgnoreCase() {
        // @Query("SELECT h FROM Hashtag h ORDER BY LOWER(h.hashtagName) ASC")
        QHashtag hashtag = QHashtag.hashtag;

        return from(hashtag)
                .select(hashtag)
                .orderBy(Expressions.stringTemplate("LOWER({0})", hashtag.hashtagName).asc())
                .fetch();
    }
}
