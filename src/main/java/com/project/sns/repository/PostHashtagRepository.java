package com.project.sns.repository;

import com.project.sns.domain.PostComment;
import com.project.sns.domain.PostHashtag;
import com.project.sns.domain.QPostComment;
import com.project.sns.domain.QPostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;

public interface PostHashtagRepository extends
        JpaRepository<PostHashtag, Long>
{
    // 특정 Post의 PostHashtag 삭제
    @Modifying
    @Query("DELETE FROM PostHashtag ph WHERE ph.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
