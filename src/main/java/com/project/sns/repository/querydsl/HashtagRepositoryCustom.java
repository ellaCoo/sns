package com.project.sns.repository.querydsl;

import com.project.sns.domain.Hashtag;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface HashtagRepositoryCustom {
    List<Hashtag> findUnusedHashtagsByIds(@Param("hashtagIds") Set<Long> hashtagIds);

    List<Hashtag> findAllHashtagsSortedIgnoreCase();
}
