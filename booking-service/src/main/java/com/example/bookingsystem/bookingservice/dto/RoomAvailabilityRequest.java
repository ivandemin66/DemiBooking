package com.example.bookingsystem.bookingservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO для запроса проверки доступности номера
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityRequest {

    @NotNull(message = "Дата начала не может быть пустой")
    private LocalDate startDate;

    @NotNull(message = "Дата окончания не может быть пустой")
    private LocalDate endDate;

    @NotNull(message = "ID номера не может быть пустым")
    private Long roomId;

    private String requestId; // Для идемпотентности
}
