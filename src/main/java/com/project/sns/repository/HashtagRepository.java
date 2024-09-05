package com.project.sns.repository;

import com.project.sns.domain.Hashtag;
import com.project.sns.domain.Post;
import com.project.sns.domain.QHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HashtagRepository extends
        JpaRepository<Hashtag, Long>
{
    // 해시태그 중에서 PostHashtag에 속하지 않는 해시태그들 찾기
    @Query("SELECT h FROM Hashtag h WHERE h.postHashtags IS EMPTY AND h.id IN :hashtagIds")
    List<Hashtag> findUnusedHashtagsByIds(@Param("hashtagIds") Set<Long> hashtagIds);

    Optional<Hashtag> findByHashtagName(String hashtagName);

    @Query("SELECT h FROM Hashtag h ORDER BY LOWER(h.hashtagName) ASC")
    List<Hashtag> findAllHashtagsSortedIgnoreCase();
}
