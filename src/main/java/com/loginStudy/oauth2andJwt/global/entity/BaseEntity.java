package com.loginStudy.oauth2andJwt.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * MappedSuperclass
 * DB 데이터가 생성/수정 시 자동으로 업데이트 됨
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    // 생성 시간
    @CreatedDate
    @Column(name = "createdTime")
    private LocalDateTime createdTime;

    // 수정 시간
    @LastModifiedDate
    @Column(name = "updatedTime")
    private LocalDateTime updatedTime;

}
