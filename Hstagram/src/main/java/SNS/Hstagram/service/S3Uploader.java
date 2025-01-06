package SNS.Hstagram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    /**
     * Presigned URL 생성:
     *  - originalFilename(또는 key), contentType 등을 받아서
     *  - PUT 메서드 presigned URL을 발급
     */
    public String createPresignedUrl(String originalFilename, String contentType) {
        // PutObjectRequest 구성
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(originalFilename)
                .acl("public-read")  // public 읽기 권한 부여
                .contentType(contentType)
                .build();

        // Presign 요청 (유효기간 예: 5분)
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .putObjectRequest(objectRequest)
                .signatureDuration(Duration.ofMinutes(5))
                .build();

        // 실제 URL 생성
        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedPutObjectRequest.url().toString();
    }

    /**
     * 게시글 등록 시, 최종 이미지 경로를 만들기 위한 S3 Object URL
     *  - 가장 단순하게 "https://버킷명.s3.amazonaws.com/{key}" 형태로 조합
     *  - 또는 presigner.utilities().getUrl(...) 사용 가능
     */
    public String getS3ObjectUrl(String key) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
}

