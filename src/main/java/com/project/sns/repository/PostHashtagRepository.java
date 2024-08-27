package com.project.sns.repository;

import com.project.sns.domain.PostComment;
import com.project.sns.domain.PostHashtag;
import com.project.sns.domain.QPostComment;
import com.project.sns.domain.QPostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public interface PostHashtagRepository extends
        JpaRepository<PostHashtag, Long>,
        QuerydslPredicateExecutor<PostHashtag>, // entity 안에 있는 모든 필드에 대한 기본 검색 기능 추가
        QuerydslBinderCustomizer<QPostHashtag>  // 부분 검색
{
}
