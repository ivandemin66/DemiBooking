package com.example.bookingsystem.hotelservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для передачи данных об отеле
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {

    private Long id;
    private String name;
    private String address;
    private String description;
    private String phoneNumber;
    private String email;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoomDto> rooms;
}
