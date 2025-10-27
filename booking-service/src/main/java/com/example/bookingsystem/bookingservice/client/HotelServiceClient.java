package com.example.bookingsystem.bookingservice.client;

import com.example.bookingsystem.bookingservice.dto.RoomAvailabilityRequest;
import com.example.bookingsystem.bookingservice.dto.RoomAvailabilityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign клиент для взаимодействия с Hotel Service
 */
@FeignClient(name = "hotel-service", path = "/api/rooms")
public interface HotelServiceClient {

    /**
     * Подтверждение доступности номера
     */
    @PostMapping("/{roomId}/confirm-availability")
    RoomAvailabilityResponse confirmRoomAvailability(@PathVariable("roomId") Long roomId,
                                                    @RequestBody RoomAvailabilityRequest request);

    /**
     * Освобождение блокировки номера
     */
    @PostMapping("/{roomId}/release")
    void releaseRoomBlock(@PathVariable("roomId") Long roomId,
                          @RequestParam("requestId") String requestId);
}
