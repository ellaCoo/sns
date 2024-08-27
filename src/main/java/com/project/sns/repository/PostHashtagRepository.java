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
        JpaRepository<PostHashtag, Long>
{
}
