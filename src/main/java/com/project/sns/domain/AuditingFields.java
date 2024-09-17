package com.project.sns.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
/*
BaseTimeEntity는 모든 Entity의 상위클래스가 되어 Entity들의 createdDate, modidiedDate를 자동으로 관리하는 역할
Jpa Entity들이 @MappedSuperClass가 선언된 클래스를 상속할 경우, 클래스의 필드를 컬럼으로 인식하도록 함
 */
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class) //BaseTimeEntity에 Auditing 기능을 포함
@MappedSuperclass
public abstract class AuditingFields { // 직접 생성해서 사용할 일 없으므로 추상클래스로 생성

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate // entity 생성시마다 DB에 현재 날짜 시간 자동 저장
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 100)
    protected String createdBy;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate // entity 수정시마다 DB에 현재 날짜 시간 자동 저장
    @Column(nullable = false)
    protected LocalDateTime modifiedAt;

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    protected String modifiedBy;
}
