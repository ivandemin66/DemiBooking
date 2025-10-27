package com.example.bookingsystem.bookingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Сущность бронирования
 */
@Entity
@Table(name = "bookings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "hotel_id", nullable = false)
    private Long hotelId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "total_price", precision = 10, scale = 2)
    private Double totalPrice;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "special_requests", length = 1000)
    private String specialRequests;

    @Column(name = "request_id", length = 100)
    private String requestId; // Для идемпотентности

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Статусы бронирования
     */
    public enum BookingStatus {
        PENDING,    // Ожидает подтверждения
        CONFIRMED,  // Подтверждено
        CANCELLED   // Отменено
    }
}
