package com.loginStudy.oauth2andJwt.domain.image.dto;

import com.loginStudy.oauth2andJwt.domain.image.entity.Image;
import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileDto {
    private String uploadFileName; // 업로드한 원본 파일명
    private String storeFileName; // 서버에 저장된 파일명
    private String filePath; // 파일의 전체 경로

    public Image toImage(Member member) {
        return Image.builder()
                .uploadFileName(this.uploadFileName)
                .storeFileName(this.storeFileName)
                .filePath(this.filePath)
                .member(member)
                .build();
    }
}
