package com.loginStudy.oauth2andJwt.domain.member.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

import com.loginStudy.oauth2andJwt.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Enumerated;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long id;

    // 계정의 아이디
    @Column(name = "account", nullable = false)
    private String account;

    // 비밀 번호
    @Column(name = "password", nullable = false)
    private String password;

    //권한
    @Enumerated(STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // 소셜 로그인 제공자 정보 (예: kakao, naver 등)
    @Column(name = "provider")
    private String provider;


    @Builder
    public Member(Long id, String account, String password, Role role, String provider) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.role = role;
        this.provider = provider;
    }

    // 소셜 계정으로 회원가입할 경우의 편의 생성자
    public static Member socialMember(String account, Role role, String provider) {
        String randomPassword = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString());
        return Member.builder()
                .account(account)
                .password(randomPassword)
                .role(role)
                .provider(provider)
                .build();
    }
}
