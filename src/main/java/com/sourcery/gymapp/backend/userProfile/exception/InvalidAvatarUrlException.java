package com.sourcery.gymapp.backend.userProfile.exception;

import org.springframework.http.HttpStatus;

public class InvalidAvatarUrlException extends UserProfileRuntimeException {

    public InvalidAvatarUrlException() {
        super("Invalid avatar url", ErrorCode.INVALID_AVATAR_URL, HttpStatus.BAD_REQUEST);
    }
}
