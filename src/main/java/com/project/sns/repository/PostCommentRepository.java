package com.project.sns.repository;

import com.project.sns.domain.Like;
import com.project.sns.domain.PostComment;
import com.project.sns.domain.QLike;
import com.project.sns.domain.QPostComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.List;

public interface PostCommentRepository extends
        JpaRepository<PostComment, Long>,
        QuerydslPredicateExecutor<PostComment>, // entity 안에 있는 모든 필드에 대한 기본 검색 기능 추가
        QuerydslBinderCustomizer<QPostComment>  // 부분 검색
{
    List<PostComment> findByPost_Id(Long postId);

    @Override
    default void customize(QuerydslBindings bindings, QPostComment root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content, root.createdAt, root.createdBy);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
