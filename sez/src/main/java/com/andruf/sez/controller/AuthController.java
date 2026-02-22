package com.andruf.sez.controller;

import com.andruf.sez.genapi.AuthApi;
import com.andruf.sez.gendto.*;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(authService.authenticateGoogle(googleLoginDto, googleClientId));
    }

    @Override
    public ResponseEntity<AuthResponse> completeRegistration(CompleteRegistrationDto completeRegistrationDto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(authService.completeRegistration(userDetails.getUsername(), completeRegistrationDto));
    }

    @Override
    public ResponseEntity<MessageResponse> registerTutor(TutorRegistrationDto tutorRegistrationDto) {
        authService.registerTutor(tutorRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse().message("Tutor registered successfully"));
    }

    @Override
    public ResponseEntity<MessageResponse> registerStudent(StudentRegistrationDto studentRegistrationDto) {
        authService.registerStudent(studentRegistrationDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse().message("Student registered successfully"));
    }

    @Override
    public ResponseEntity<MessageResponse> forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        authService.processForgotPassword(forgotPasswordDto.getEmail());
        return ResponseEntity.ok(new MessageResponse().message("Reset email sent"));
    }

    @Override
    public ResponseEntity<MessageResponse> resetPassword(ResetPasswordDto resetPasswordDto) {
        authService.updatePassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
        return ResponseEntity.ok(new MessageResponse().message("Password changed successfully"));
    }
}