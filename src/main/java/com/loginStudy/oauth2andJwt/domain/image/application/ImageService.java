package com.loginStudy.oauth2andJwt.domain.image.application;

import com.loginStudy.oauth2andJwt.domain.image.dao.ImageRepository;
import com.loginStudy.oauth2andJwt.domain.image.dto.FileDto;
import com.loginStudy.oauth2andJwt.domain.image.entity.Image;
import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final FileService fileService;
    private final ImageRepository imageRepository;

    @Transactional
    public Image saveImage(MultipartFile file, Member member) throws IOException {
        // 파일 저장 및 파일 경로, 파일명 설정
        FileDto fileDto = fileService.uploadFile(file);

        // 이미지 엔티티 생성 및 저장
        Image image = fileDto.toImage(member);

        return imageRepository.save(image);
    }
}
