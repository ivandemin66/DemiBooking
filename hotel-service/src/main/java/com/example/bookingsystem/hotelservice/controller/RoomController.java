package com.example.bookingsystem.hotelservice.controller;

import com.example.bookingsystem.hotelservice.dto.CreateRoomRequest;
import com.example.bookingsystem.hotelservice.dto.RoomAvailabilityRequest;
import com.example.bookingsystem.hotelservice.dto.RoomAvailabilityResponse;
import com.example.bookingsystem.hotelservice.dto.RoomDto;
import com.example.bookingsystem.hotelservice.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления номерами
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    /**
     * Создание нового номера (только для администраторов)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        log.info("Создание нового номера: {} в отеле {}", request.getRoomNumber(), request.getHotelId());
        RoomDto room = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    /**
     * Получение номера по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        log.debug("Получение номера по ID: {}", id);
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получение всех доступных номеров
     */
    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllAvailableRooms() {
        log.debug("Получение всех доступных номеров");
        List<RoomDto> rooms = roomService.getAllAvailableRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * Получение номеров отеля
     */
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomDto>> getRoomsByHotelId(@PathVariable Long hotelId) {
        log.debug("Получение номеров отеля с ID: {}", hotelId);
        List<RoomDto> rooms = roomService.getRoomsByHotelId(hotelId);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Получение доступных номеров отеля
     */
    @GetMapping("/hotel/{hotelId}/available")
    public ResponseEntity<List<RoomDto>> getAvailableRoomsByHotelId(@PathVariable Long hotelId) {
        log.debug("Получение доступных номеров отеля с ID: {}", hotelId);
        List<RoomDto> rooms = roomService.getAvailableRoomsByHotelId(hotelId);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Получение рекомендованных номеров (алгоритм планирования занятости)
     */
    @GetMapping("/recommend")
    public ResponseEntity<List<RoomDto>> getRecommendedRooms(@RequestParam Long hotelId) {
        log.debug("Получение рекомендованных номеров для отеля с ID: {}", hotelId);
        List<RoomDto> rooms = roomService.getRecommendedRooms(hotelId);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Поиск номеров по типу
     */
    @GetMapping("/search/type")
    public ResponseEntity<List<RoomDto>> getRoomsByType(@RequestParam String roomType) {
        log.debug("Поиск номеров по типу: {}", roomType);
        List<RoomDto> rooms = roomService.getRoomsByType(roomType);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Поиск номеров по вместимости
     */
    @GetMapping("/search/capacity")
    public ResponseEntity<List<RoomDto>> getRoomsByCapacity(@RequestParam Integer capacity) {
        log.debug("Поиск номеров по вместимости: {}", capacity);
        List<RoomDto> rooms = roomService.getRoomsByCapacity(capacity);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Поиск номеров по ценовому диапазону
     */
    @GetMapping("/search/price")
    public ResponseEntity<List<RoomDto>> getRoomsByPriceRange(@RequestParam Double minPrice, 
                                                             @RequestParam Double maxPrice) {
        log.debug("Поиск номеров по ценовому диапазону: {} - {}", minPrice, maxPrice);
        List<RoomDto> rooms = roomService.getRoomsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Получение статистики загруженности номеров (только для администраторов)
     */
    @GetMapping("/statistics/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoomDto>> getRoomStatistics(@PathVariable Long hotelId) {
        log.debug("Получение статистики загруженности номеров отеля с ID: {}", hotelId);
        List<RoomDto> rooms = roomService.getRoomStatistics(hotelId);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Подтверждение доступности номера (внутренний API для Booking Service)
     */
    @PostMapping("/{id}/confirm-availability")
    public ResponseEntity<RoomAvailabilityResponse> confirmRoomAvailability(
            @PathVariable Long id,
            @Valid @RequestBody RoomAvailabilityRequest request) {
        log.info("Подтверждение доступности номера {} на период {} - {}", 
                id, request.getStartDate(), request.getEndDate());
        
        request.setRoomId(id);
        RoomAvailabilityResponse response = roomService.confirmRoomAvailability(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Освобождение блокировки номера (внутренний API для Booking Service)
     */
    @PostMapping("/{id}/release")
    public ResponseEntity<Void> releaseRoomBlock(@PathVariable Long id, 
                                                @RequestParam String requestId) {
        log.info("Освобождение блокировки номера {} для запроса {}", id, requestId);
        roomService.releaseRoomBlock(requestId);
        return ResponseEntity.ok().build();
    }
}
