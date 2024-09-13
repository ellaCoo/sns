package com.project.sns.repository;

import com.project.sns.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.Optional;

public interface LikeRepository extends
        JpaRepository<Like, Long>
{
    void deleteByUserAccount_userIdAndPost_Id(String Userid, Long postId);
    Optional<Like> findByUserAccount_userIdAndPost_Id(String Userid, Long postId);
}
