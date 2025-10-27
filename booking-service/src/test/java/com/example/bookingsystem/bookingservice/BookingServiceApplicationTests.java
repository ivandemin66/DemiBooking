package com.example.bookingsystem.bookingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовые тесты для Booking Service
 */
@SpringBootTest
@ActiveProfiles("test")
class BookingServiceApplicationTests {

    @Test
    void contextLoads() {
        // Тест проверяет, что контекст Spring Boot загружается корректно
    }
}
