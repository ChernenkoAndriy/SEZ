package com.andruf.sez.service;

import com.andruf.sez.entity.Student;
import com.andruf.sez.entity.Tutor;
import com.andruf.sez.entity.User;
import com.andruf.sez.entity.enums.AuthProvider;
import com.andruf.sez.entity.enums.UserRole;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.exception.EntityNotFoundException;
import com.andruf.sez.gendto.*;
import com.andruf.sez.repository.StudentRepository;
import com.andruf.sez.repository.TutorRepository;
import com.andruf.sez.repository.UserRepository;
import com.andruf.sez.security.JwtUtils;
import com.andruf.sez.security.services.UserDetailsImpl;
import com.andruf.sez.utils.EmailService;
import com.andruf.sez.validator.UserValidator;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserValidator userValidator;
    @PersistenceContext
    private EntityManager entityManager;

    public AuthResponse authenticate(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return generateAuthResponse(authentication);
    }

    @Transactional
    public void registerTutor(TutorRegistrationDto request) {
        emailService.sendGreetingEmail(request.getEmail());
        Tutor tutor = Tutor.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .surname(request.getSurname())
                .role(UserRole.TUTOR)
                .registrationComplete(true)
                .authProvider(AuthProvider.LOCAL)
                .enabled(true)
                .rating(5.0)
                .bio(request.getBio())
                .build();
        userValidator.validateRegistration(tutor);
        userRepository.save(tutor);
    }

    @Transactional
    public void registerStudent(StudentRegistrationDto request) {
        emailService.sendGreetingEmail(request.getEmail());
        Student student = Student.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .surname(request.getSurname())
                .role(UserRole.STUDENT)
                .registrationComplete(true)
                .authProvider(AuthProvider.LOCAL)
                .enabled(true)
                .build();
        userValidator.validateRegistration(student);
        userRepository.save(student);
    }

    @Transactional
    public AuthResponse completeRegistration(String currentEmail, CompleteRegistrationDto dto) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with email: " + currentEmail));

        if (user.getRole() != UserRole.PRE_REGISTERED) {
            throw new BusinessException("User has already completed registration", "REGISTRATION_ALREADY_COMPLETE");
        }
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setRole(UserRole.valueOf(dto.getRole().getValue()));
        user.setRegistrationComplete(true);
        userRepository.saveAndFlush(user);

        if (dto.getRole() == CompleteRegistrationDto.RoleEnum.TUTOR) {
            entityManager.createNativeQuery("INSERT INTO tutors (id, bio, rating) VALUES (?, ?, ?)")
                    .setParameter(1, user.getId())
                    .setParameter(2, dto.getBio())
                    .setParameter(3, 5.0)
                    .executeUpdate();
        } else {
            entityManager.createNativeQuery("INSERT INTO students (id) VALUES (?)")
                    .setParameter(1, user.getId())
                    .executeUpdate();
        }

        UserDetails userDetails = UserDetailsImpl.build(user);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        AuthResponse response = generateAuthResponse(newAuth);
        response.setRegistrationComplete(true);
        return response;
    }

    public AuthResponse generateAuthResponse(Authentication authentication) {
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String authority = userDetails.getAuthorities().iterator().next().getAuthority();

        String roleName = authority.startsWith("ROLE_") ? authority.substring(5) : authority;

        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setEmail(userDetails.getUsername());

        response.setRole(roleName);

        response.setRegistrationComplete(true);
        return response;
    }

    @Transactional
    public void processForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with email: " + email));
        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new BusinessException("This user does not use local authentication", "INVALID_AUTH_PROVIDER");
        }
        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(OffsetDateTime.now().plusMinutes(15));
        userRepository.save(user);
        emailService.sendResetPasswordEmail(user.getEmail(), token);
    }

    @Transactional
    public void updatePassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user with token: " + token));
        if (user.getResetPasswordTokenExpiry().isBefore(OffsetDateTime.now())) {
            throw new BusinessException("Reset Password Token has expired", "INVALID_TOKEN_EXPIRY");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse authenticateGoogle(GoogleLoginDto loginRequest, String googleClientId) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(loginRequest.getIdToken());
            if (idToken == null) throw new BusinessException("Invalid Google Token", "INVALID_GOOGLE_TOKEN");

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .email(email)
                        .name((String) payload.get("given_name"))
                        .surname((String) payload.get("family_name"))
                        .patronymic("")
                        .authProvider(AuthProvider.GOOGLE)
                        .role(UserRole.PRE_REGISTERED)
                        .registrationComplete(false)
                        .enabled(true)
                        .build();
                return userRepository.save(newUser);
            });

            UserDetails userDetails = UserDetailsImpl.build(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            AuthResponse response = generateAuthResponse(authentication);
            response.setRegistrationComplete(user.isRegistrationComplete());
            return response;
        } catch (Exception e) {
            throw new BusinessException("Failed authentication with google", "GOOGLE_AUTH_FAILURE");
        }
    }
}