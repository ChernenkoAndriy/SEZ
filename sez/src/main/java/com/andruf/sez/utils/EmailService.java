package com.andruf.sez.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendResetPasswordEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("SEZ Support <no-reply@sez.com>");
        message.setTo(to);
        message.setSubject("Відновлення пароля | SEZ Platform");
        message.setText("Ваш код для відновлення: " + token);

        mailSender.send(message);
    }
}