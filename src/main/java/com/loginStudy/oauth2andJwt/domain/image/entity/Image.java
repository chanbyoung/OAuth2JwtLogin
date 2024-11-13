package com.loginStudy.oauth2andJwt.domain.image.entity;

import com.loginStudy.oauth2andJwt.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;

    // 원본 파일명 (사용자가 업로드한 파일명)
    @Column(name = "upload_file_name", nullable = false)
    private String uploadFileName;

    // 서버에 저장된 파일명 (UUID 등으로 관리)
    @Column(name = "store_file_name", nullable = false)
    private String storeFileName;

    // 파일 경로
    @Column(name = "file_path", nullable = false)
    private String filePath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Builder
    public Image(Long id, String uploadFileName, String storeFileName, String filePath, Member member) {
        this.id = id;
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.filePath = filePath;
        this.member = member;
    }

    // 파일 정보 업데이트
    public void updateImage(String uploadFileName, String storeFileName, String filePath) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.filePath = filePath;
    }
}
