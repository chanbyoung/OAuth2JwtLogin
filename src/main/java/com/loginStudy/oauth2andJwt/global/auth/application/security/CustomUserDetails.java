package com.loginStudy.oauth2andJwt.global.auth.application.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Builder
public class CustomUserDetails implements UserDetails, OAuth2User {
    private static final String ROLE_GUEST = "ROLE_GUEST";

    private String account;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes; // OAuth2 관련 사용자 속성

    /**
     * 사용자의 권한 정보 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return account;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public String getName() {
        return account;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    public boolean isGuest() {
        return authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(ROLE_GUEST));
    }
}
