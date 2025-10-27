package com.example.bookingsystem.hotelservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Hotel Service приложения
 * Обеспечивает управление отелями и их информацией
 */
@SpringBootApplication
public class HotelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelServiceApplication.class, args);
    }
}
