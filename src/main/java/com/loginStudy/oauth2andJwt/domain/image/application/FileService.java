package com.loginStudy.oauth2andJwt.domain.image.application;

import com.loginStudy.oauth2andJwt.domain.image.dto.FileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileService {
    @Value("${file.dir}")
    private String UPLOAD_DIR;

    // 파일 업로드 메서드
    public FileDto uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);

        // 실제 파일 저장
        String filePath = UPLOAD_DIR + storeFileName;
        log.info("UPLOAD_DIR = {} " , UPLOAD_DIR);
        File destinationFile = new File(filePath);
        file.transferTo(destinationFile);

        // FileDto로 반환
        return new FileDto(originalFilename, storeFileName, filePath);
    }

    private String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }
}
