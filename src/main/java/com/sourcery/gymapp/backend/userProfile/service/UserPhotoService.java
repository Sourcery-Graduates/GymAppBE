package com.sourcery.gymapp.backend.userProfile.service;

import com.sourcery.gymapp.backend.globalconfig.CurrentUserService;
import com.sourcery.gymapp.backend.userProfile.model.UserProfile;
import com.sourcery.gymapp.backend.userProfile.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPhotoService {
    private final CurrentUserService currentUserService;
    private final UserProfileRepository userProfileRepository;
    private final S3Client s3Client;

    @Value("${aws.s3.region}")
    private String awsRegion;

    @Value("${aws.s3.bucket}")
    private String awsBucket;

    @Transactional
    public void uploadUserPhoto(MultipartFile image) throws IOException {
        if (image.isEmpty()) {
            throw new RuntimeException("Image is empty");
        }
        if (image.getSize() > 5242880) {
            throw new RuntimeException("Image is too large");
        }
        UUID currentUserId = currentUserService.getCurrentUserId();

        String bucket = awsBucket;
        String region = awsRegion;
        String newImageName = "user-photo";
        String objectKey = currentUserId.toString() + "/" + newImageName;

       PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        RequestBody requestBody = RequestBody.fromInputStream(image.getInputStream(), image.getSize());


        s3Client.putObject(putObjectRequest, requestBody);
        String objectUrl = "https://%s.s3.%s.amazonaws.com/%s/%s".formatted(bucket, region,  currentUserId, newImageName);

        updateAvatarUrl(currentUserId, objectUrl);
    }

    private void updateAvatarUrl(UUID userId, String url) {
        Optional<UserProfile> userProfile = userProfileRepository.findUserProfileByUserId(userId);
        userProfile.ifPresent(user -> {
            user.setAvatarUrl(url);
            userProfileRepository.save(user);
        });
    }
}
