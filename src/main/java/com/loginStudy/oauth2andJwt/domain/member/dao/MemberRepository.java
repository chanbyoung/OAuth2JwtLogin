package com.loginStudy.oauth2andJwt.domain.member.dao;

import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount(String account);
}
