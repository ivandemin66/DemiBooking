package com.example.bookingsystem.hotelservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Сущность номера отеля
 * Содержит информацию о номере и статистику бронирований
 */
@Entity
@Table(name = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    @Column(name = "room_type", nullable = false, length = 50)
    private String roomType;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "price_per_night", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "amenities", length = 1000)
    private String amenities;

    @Column(name = "available", nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Column(name = "times_booked", nullable = false)
    @Builder.Default
    private Integer timesBooked = 0;

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
     * Увеличивает счетчик бронирований
     */
    public void incrementTimesBooked() {
        this.timesBooked++;
    }

    /**
     * Уменьшает счетчик бронирований
     */
    public void decrementTimesBooked() {
        if (this.timesBooked > 0) {
            this.timesBooked--;
        }
    }
}
