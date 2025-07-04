package com.sourcery.gymapp.backend.authentication.controller;

import com.sourcery.gymapp.backend.authentication.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Profile("!deployment")
@RestController
@RequestMapping("/api/auth-admin")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AuthService authService;

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam("email") String email) {
        return authService.deleteUser(email);
    }
}
