package com.loginStudy.oauth2andJwt.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA와 관련된 설정
 * EnableJpaAuditing 을 통해, CreatedDate 등 사용할 수 있게 설정
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
