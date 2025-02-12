package com.sourcery.gymapp.backend.userProfile.controller;

import com.sourcery.gymapp.backend.userProfile.dto.UserProfileDto;
import com.sourcery.gymapp.backend.userProfile.service.UserPhotoService;
import com.sourcery.gymapp.backend.userProfile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user-profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final UserPhotoService userPhotoService;

    @GetMapping
    public UserProfileDto getUserProfile() {
        return userProfileService.getUserProfile();
    }

    @PutMapping()
    public UserProfileDto updateUserProfile(@Validated @RequestBody UserProfileDto dto) {
        return userProfileService.updateUserProfile(dto);
    }
    @DeleteMapping()
    public UserProfileDto deleteUserProfile() {
        return userProfileService.deleteUserProfile();
    }

    @PutMapping("/photo")
    public ResponseEntity<String> uploadUserPhoto(@RequestParam("file") MultipartFile image) throws IOException {
        userPhotoService.uploadUserPhoto(image);
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        return ResponseEntity.ok().headers(headers).body("Photo uploaded successfully");
    }
}
