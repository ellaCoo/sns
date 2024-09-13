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
import java.util.Optional;

public interface PostCommentRepository extends
        JpaRepository<PostComment, Long>
{
    List<PostComment> findByPost_Id(Long postId);

    Optional<PostComment> findByIdAndUserAccount_userId(Long commentId, String userId);
}
