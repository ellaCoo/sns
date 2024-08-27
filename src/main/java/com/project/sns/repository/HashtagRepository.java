package com.project.sns.repository;

import com.project.sns.domain.Hashtag;
import com.project.sns.domain.QHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface HashtagRepository extends
        JpaRepository<Hashtag, Long>,
        QuerydslPredicateExecutor<Hashtag>, // entity 안에 있는 모든 필드에 대한 기본 검색 기능 추가
        QuerydslBinderCustomizer<QHashtag>  // 부분 검색
{
    @Override
    default void customize(QuerydslBindings bindings, QHashtag root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.hashtagName);
    }
}
