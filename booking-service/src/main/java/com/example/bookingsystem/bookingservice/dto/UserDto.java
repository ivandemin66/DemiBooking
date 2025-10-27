package com.example.bookingsystem.bookingservice.dto;

import com.example.bookingsystem.bookingservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для передачи данных о пользователе
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private User.UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
