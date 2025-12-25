package com.example.bookingsystem.bookingservice.client;

import com.example.bookingsystem.bookingservice.dto.RoomAvailabilityRequest;
import com.example.bookingsystem.bookingservice.dto.RoomAvailabilityResponse;
import com.example.bookingsystem.bookingservice.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Feign клиент для взаимодействия с Hotel Service
 */
@FeignClient(name = "hotel-service", path = "/api/rooms")
public interface HotelServiceClient {

    /**
     * Получение рекомендованных номеров (отсортированных по times_booked)
     */
    @GetMapping("/recommend")
    List<RoomDto> getRecommendedRooms(@RequestParam("hotelId") Long hotelId);

    /**
     * Получение всех доступных рекомендованных номеров
     */
    @GetMapping
    List<RoomDto> getAllAvailableRooms();

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
