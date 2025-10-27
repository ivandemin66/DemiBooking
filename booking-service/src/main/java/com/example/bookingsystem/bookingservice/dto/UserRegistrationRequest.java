package com.example.bookingsystem.bookingservice.dto;

import com.example.bookingsystem.bookingservice.entity.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для регистрации пользователя
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6, max = 100, message = "Пароль должен содержать от 6 до 100 символов")
    private String password;

    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не может превышать 100 символов")
    private String email;

    @Size(max = 50, message = "Имя не может превышать 50 символов")
    private String firstName;

    @Size(max = 50, message = "Фамилия не может превышать 50 символов")
    private String lastName;

    @Size(max = 20, message = "Номер телефона не может превышать 20 символов")
    private String phoneNumber;

    @Builder.Default
    private User.UserRole role = User.UserRole.USER;
}
