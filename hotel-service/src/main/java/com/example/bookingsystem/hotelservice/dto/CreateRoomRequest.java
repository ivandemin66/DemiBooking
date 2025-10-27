package com.example.bookingsystem.hotelservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO для создания нового номера
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {

    @NotNull(message = "ID отеля не может быть пустым")
    private Long hotelId;

    @NotBlank(message = "Номер комнаты не может быть пустым")
    @Size(max = 20, message = "Номер комнаты не может превышать 20 символов")
    private String roomNumber;

    @NotBlank(message = "Тип комнаты не может быть пустым")
    @Size(max = 50, message = "Тип комнаты не может превышать 50 символов")
    private String roomType;

    @NotNull(message = "Вместимость не может быть пустой")
    @Min(value = 1, message = "Вместимость должна быть не менее 1")
    private Integer capacity;

    @NotNull(message = "Цена за ночь не может быть пустой")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена за ночь должна быть больше 0")
    private BigDecimal pricePerNight;

    @Size(max = 1000, message = "Описание комнаты не может превышать 1000 символов")
    private String description;

    @Size(max = 1000, message = "Удобства не могут превышать 1000 символов")
    private String amenities;
}
