package com.andruf.sez.validator;

import com.andruf.sez.entity.User;
import com.andruf.sez.exception.BusinessException;
import com.andruf.sez.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void validateRegistration(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email is already used: " + user.getEmail(), "EMAIL_ALREADY_IN_USE");
        }
    }
}