package com.loginStudy.oauth2andJwt.domain.image.dao;

import com.loginStudy.oauth2andJwt.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
