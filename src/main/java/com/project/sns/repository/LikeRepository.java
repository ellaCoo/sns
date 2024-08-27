package com.project.sns.repository;

import com.project.sns.domain.Hashtag;
import com.project.sns.domain.Like;
import com.project.sns.domain.QHashtag;
import com.project.sns.domain.QLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface LikeRepository extends
        JpaRepository<Like, Long>,
        QuerydslPredicateExecutor<Like>, // entity 안에 있는 모든 필드에 대한 기본 검색 기능 추가
        QuerydslBinderCustomizer<QLike>  // 부분 검색
{
}
