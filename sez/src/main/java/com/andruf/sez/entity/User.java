package com.andruf.sez.entity;

import com.andruf.sez.entity.enums.AuthProvider;
import com.andruf.sez.entity.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.OffsetDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor
@SuperBuilder
@Data
public class User extends BaseEntity<UUID> {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 3, max = 255, message = "Password must be between 3 and 255 characters")
    private String password;
    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    @NotBlank(message = "name is required")
    private String name;

    @Size(min = 2, max = 50, message = "surname must be between 2 and 50 characters")
    @NotBlank(message = "surname is required")
    private String surname;

    @Size( max = 50, message = "surname must be between 2 and 50 characters")
    private String patronymic;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder.Default
    @Column(name = "registration_complete")
    private boolean registrationComplete = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    @Builder.Default
    @NotNull(message = "Auth provider is required")
    private AuthProvider authProvider = AuthProvider.LOCAL;

    private String providerId;

    private String resetPasswordToken;

    private OffsetDateTime resetPasswordTokenExpiry;

    @Builder.Default
    private boolean enabled = true;
}