package com.example.bookingsystem.bookingservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//DTO для создания бронирования

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "ID номера не может быть пустым")
    private Long roomId;

    @NotNull(message = "Дата начала не может быть пустой")
    @Future(message = "Дата начала должна быть в будущем")
    private LocalDate startDate;

    @NotNull(message = "Дата окончания не может быть пустой")
    @Future(message = "Дата окончания должна быть в будущем")
    private LocalDate endDate;

    @NotNull(message = "Количество гостей не может быть пустым")
    @Min(value = 1, message = "Количество гостей должно быть не менее 1")
    @Max(value = 10, message = "Количество гостей не может превышать 10")
    private Integer guestCount;

    @Size(max = 1000, message = "Особые пожелания не могут превышать 1000 символов")
    private String specialRequests;

    @NotNull(message = "Поле autoSelect обязательно")
    private Boolean autoSelect; // true - автоподбор номера, false - конкретный номер

    private String requestId; // Для идемпотентности
}
