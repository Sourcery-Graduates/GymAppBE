package com.sourcery.gymapp.backend.sharedLinks.service;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class LinksCurrentUserService {

    //TODO: add to module-local exception handler when handler is created
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return UUID.fromString(jwt.getClaimAsString("userId"));
        }
        throw new IllegalStateException("User is not authenticated");
    }
}
