package com.example.bookingsystem.hotelservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для создания нового отеля
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateHotelRequest {

    @NotBlank(message = "Название отеля не может быть пустым")
    @Size(max = 255, message = "Название отеля не может превышать 255 символов")
    private String name;

    @NotBlank(message = "Адрес отеля не может быть пустым")
    @Size(max = 500, message = "Адрес отеля не может превышать 500 символов")
    private String address;

    @Size(max = 1000, message = "Описание отеля не может превышать 1000 символов")
    private String description;

    @Size(max = 20, message = "Номер телефона не может превышать 20 символов")
    private String phoneNumber;

    @Email(message = "Некорректный формат email")
    @Size(max = 100, message = "Email не может превышать 100 символов")
    private String email;

    private Double rating;
}
