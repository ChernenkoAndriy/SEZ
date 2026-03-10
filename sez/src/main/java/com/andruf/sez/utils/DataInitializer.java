package com.andruf.sez.utils;

import com.andruf.sez.entity.Student;
import com.andruf.sez.entity.Tutor;
import com.andruf.sez.entity.User;
import com.andruf.sez.entity.enums.AuthProvider;
import com.andruf.sez.entity.enums.UserRole;
import com.andruf.sez.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void run() {
        Path root = Paths.get("uploads");
        try {
            FileSystemUtils.deleteRecursively(root);
            Files.createDirectories(root);
            System.out.println("Папка uploads була успішно очищена та створена заново.");
        } catch (IOException e) {
            throw new RuntimeException("Не вдалося ініціалізувати папку uploads: " + e.getMessage());
        }
        if (userRepository.count() == 0) {
            initializeUsers();
            userRepository.flush();
            executeSqlScript();
            System.out.println("--- Дані успішно ініціалізовано: 5 користувачів + SQL скрипт ---");
        }
    }

    private void initializeUsers() {
        String commonPassword = passwordEncoder.encode("password123");

        userRepository.save(Tutor.builder()
                .email("andre.chrn@gmail.com")
                .name("Andrii").surname("Chrn").bio("Experienced tutor in various subjects").rating(4.5)
                .role(UserRole.TUTOR).authProvider(AuthProvider.GOOGLE)
                .registrationComplete(true).enabled(true).bio("Experienced Google tutor").rating(5.0).build());

        userRepository.save(Student.builder()
                .email("andre.chrnko@gmail.com")
                .name("Andrii").surname("Chrnko")
                .role(UserRole.STUDENT).authProvider(AuthProvider.GOOGLE)
                .registrationComplete(true).enabled(true).build());

        userRepository.save(Student.builder()
                .email("polina.chrnko@gmail.com")
                .password(commonPassword)
                .name("Olena").surname("Petrenko")
                .role(UserRole.STUDENT).registrationComplete(true).enabled(true)
                .authProvider(AuthProvider.LOCAL).build());

        userRepository.save(Student.builder()
                .email("student.dmytro@gmail.com")
                .password(commonPassword)
                .name("Dmytro").surname("Sydorenko")
                .role(UserRole.STUDENT).registrationComplete(true).enabled(true)
                .authProvider(AuthProvider.LOCAL).build());
        userRepository.save(Student.builder()
                .email("student.ivan@gmail.com")
                .password(commonPassword)
                .name("Ivan").surname("Ivanov")
                .role(UserRole.STUDENT)
                .registrationComplete(true)
                .enabled(true)
                .authProvider(AuthProvider.LOCAL)
                .build());
    }
    private void executeSqlScript() {
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("data-init.sql"));
            populator.execute(dataSource);
        } catch (Exception e) {
            System.err.println("Помилка виконання SQL скрипта: " + e.getMessage());
        }
    }
}