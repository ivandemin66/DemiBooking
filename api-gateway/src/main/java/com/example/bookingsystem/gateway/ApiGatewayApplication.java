package com.example.bookingsystem.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

/**
 * Главный класс API Gateway приложения
 * Отключаем Servlet/WebMVC автоконфигурации, чтобы гарантировать реактивный режим.
 */
@SpringBootApplication(exclude = {
        WebMvcAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class
})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
