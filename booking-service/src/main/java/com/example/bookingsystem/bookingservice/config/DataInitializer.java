package com.example.bookingsystem.bookingservice.config;

import com.example.bookingsystem.bookingservice.entity.User;
import com.example.bookingsystem.bookingservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Инициализация тестовых данных для Booking Service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Инициализация тестовых данных для Booking Service");
        
        if (userRepository.count() == 0) {
            initializeUsers();
            log.info("Тестовые данные успешно инициализированы");
        } else {
            log.info("Тестовые данные уже существуют, пропускаем инициализацию");
        }
    }

    /**
     * Создание тестовых пользователей
     */
    private void initializeUsers() {
        log.info("Создание тестовых пользователей");
        
        // Создаем администратора
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .email("admin@hotelbooking.com")
                .firstName("Администратор")
                .lastName("Системы")
                .role(User.UserRole.ADMIN)
                .build();
        
        // Создаем обычного пользователя
        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .email("user@example.com")
                .firstName("Иван")
                .lastName("Петров")
                .role(User.UserRole.USER)
                .build();
        
        // Создаем еще одного пользователя
        User user2 = User.builder()
                .username("testuser")
                .password(passwordEncoder.encode("test123"))
                .email("test@example.com")
                .firstName("Мария")
                .lastName("Сидорова")
                .role(User.UserRole.USER)
                .build();
        
        userRepository.save(admin);
        userRepository.save(user);
        userRepository.save(user2);
        
        log.info("Создано {} пользователей", userRepository.count());
        log.info("Администратор: admin / admin123");
        log.info("Пользователь 1: user / user123");
        log.info("Пользователь 2: testuser / test123");
    }
}
