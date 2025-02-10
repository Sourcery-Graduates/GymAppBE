package com.sourcery.gymapp.backend.userProfile.service;

import com.sourcery.gymapp.backend.globalconfig.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserPhotoService {
    private final CurrentUserService currentUserService;
    private final S3Client s3;

    public void uploadUserPhoto() {
        s3.putObject(builder -> builder.bucket("gymappbucket-dev")
                .key("photo/" + UUID.randomUUID())
                .build(), Path.of("src/main/resources/placeholder.jpg"));
    }

    public void getUserPhoto() {
        ListBucketsResponse response = s3.listBuckets();
        List<Bucket> bucketList = response.buckets();
        bucketList.forEach(bucket -> System.out.println("Bucket Name: " + bucket.name()));
    }
}
