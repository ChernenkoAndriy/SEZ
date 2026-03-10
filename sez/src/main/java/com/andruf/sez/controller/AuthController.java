package com.andruf.sez.controller;

import com.andruf.sez.genapi.AuthApi;
import com.andruf.sez.gendto.*;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Override
    public ResponseEntity<AuthResponse> authenticateUser(LoginDto loginDto) {
        return ResponseEntity.ok(authService.authenticate(loginDto.getEmail(), loginDto.getPassword()));
    }

    @Override
    public ResponseEntity<AuthResponse> authenticateGoogle(GoogleLoginDto googleLoginDto) {
        AuthResponse authResponse = authService.authenticateGoogle(googleLoginDto, googleClientId);
        log.info("Google authentication completed for {}", authResponse);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    public ResponseEntity<AuthResponse> completeRegistration(CompleteRegistrationDto completeRegistrationDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthResponse authResponse =authService.completeRegistration(userDetails.getUsername(), completeRegistrationDto);
        log.info("Registration through google completed for {}", authResponse);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    public ResponseEntity<MessageResponse> registerTutor(TutorRegistrationDto tutorRegistrationDto) {
        authService.registerTutor(tutorRegistrationDto);
        log.info("Tutor local registration completed for {}", tutorRegistrationDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse().message("Tutor registered successfully"));
    }

    @Override
    public ResponseEntity<MessageResponse> registerStudent(StudentRegistrationDto studentRegistrationDto) {
        authService.registerStudent(studentRegistrationDto);
        log.info("Student local registration completed for {}", studentRegistrationDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse().message("Student registered successfully"));
    }

    @Override
    public ResponseEntity<MessageResponse> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        authService.processForgotPassword(forgotPasswordDto.getEmail());
        log.info("Forgot password request for {}", forgotPasswordDto.getEmail());
        return ResponseEntity.ok(new MessageResponse().message("Reset email sent"));
    }

    @Override
    public ResponseEntity<MessageResponse> resetPassword(ResetPasswordDto resetPasswordDto) {
        authService.updatePassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
        log.info("Reset password request for {}", resetPasswordDto.getToken());
        return ResponseEntity.ok(new MessageResponse().message("Password changed successfully"));
    }
}