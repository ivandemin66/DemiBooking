package com.example.bookingsystem.bookingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

/**
 * Главный класс Booking Service приложения
 * Обеспечивает управление бронированиями отелей
 */
@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
