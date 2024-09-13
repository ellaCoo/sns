package com.project.sns.repository;

import com.project.sns.domain.Post;
import com.project.sns.domain.QPost;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.List;

public interface PostRepository extends
        JpaRepository<Post, Long>
{
    void deleteByIdAndUserAccount_UserId(Long postId, String userId);

    Page<Post> findByUserAccount_UserId(String userId, Pageable pageable);

    Page<Post> findByPostHashtags_hashtagId(Long hashtagId, Pageable pageable);
}
