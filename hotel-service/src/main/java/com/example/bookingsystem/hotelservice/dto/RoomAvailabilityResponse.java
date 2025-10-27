package com.example.bookingsystem.hotelservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа о доступности номера
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAvailabilityResponse {

    private boolean available;
    private String message;
    private String requestId;
    private Long roomId;
}
