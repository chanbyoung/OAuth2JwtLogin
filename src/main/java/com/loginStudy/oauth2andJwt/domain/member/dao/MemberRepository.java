package com.loginStudy.oauth2andJwt.domain.member.dao;

import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
