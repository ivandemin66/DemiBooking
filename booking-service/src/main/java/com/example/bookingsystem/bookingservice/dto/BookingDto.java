package com.example.bookingsystem.bookingservice.dto;

import com.example.bookingsystem.bookingservice.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO для передачи данных о бронировании
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;
    private Long userId;
    private Long roomId;
    private Long hotelId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Booking.BookingStatus status;
    private Double totalPrice;
    private Integer guestCount;
    private String specialRequests;
    private String requestId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
