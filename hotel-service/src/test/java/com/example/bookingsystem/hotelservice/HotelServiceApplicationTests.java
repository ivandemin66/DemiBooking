package com.example.bookingsystem.hotelservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Базовые тесты для Hotel Service
 */
@SpringBootTest
@ActiveProfiles("test")
class HotelServiceApplicationTests {

    @Test
    void contextLoads() {
        // Тест проверяет, что контекст Spring Boot загружается корректно
    }
}
