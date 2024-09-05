package com.project.sns.repository;

import com.project.sns.domain.Hashtag;
import com.project.sns.domain.QHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.List;
import java.util.Optional;

public interface HashtagRepository extends
        JpaRepository<Hashtag, Long>
{
    // 해시태그 중에서 PostHashtag에 속하지 않는 해시태그들 찾기
    @Query("SELECT h FROM Hashtag h WHERE h.postHashtags IS EMPTY")
    List<Hashtag> findUnusedHashtags();


    Optional<Hashtag> findByHashtagName(String hashtagName);
}
