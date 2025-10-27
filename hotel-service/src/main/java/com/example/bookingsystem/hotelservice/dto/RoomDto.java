package com.example.bookingsystem.hotelservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для передачи данных о номере
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

    private Long id;
    private Long hotelId;
    private String roomNumber;
    private String roomType;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private String description;
    private String amenities;
    private Boolean available;
    private Integer timesBooked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
