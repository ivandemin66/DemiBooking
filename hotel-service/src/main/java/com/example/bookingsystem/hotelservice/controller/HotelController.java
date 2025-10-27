package com.example.bookingsystem.hotelservice.controller;

import com.example.bookingsystem.hotelservice.dto.CreateHotelRequest;
import com.example.bookingsystem.hotelservice.dto.HotelDto;
import com.example.bookingsystem.hotelservice.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления отелями
 */
@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;

    /**
     * Создание нового отеля (только для администраторов)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelDto> createHotel(@Valid @RequestBody CreateHotelRequest request) {
        log.info("Создание нового отеля: {}", request.getName());
        HotelDto hotel = hotelService.createHotel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(hotel);
    }

    /**
     * Получение всех отелей
     */
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        log.debug("Получение списка всех отелей");
        List<HotelDto> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    /**
     * Получение отеля по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        log.debug("Получение отеля по ID: {}", id);
        return hotelService.getHotelById(id)
                .map(hotel -> ResponseEntity.ok(hotel))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Поиск отелей по названию
     */
    @GetMapping("/search")
    public ResponseEntity<List<HotelDto>> searchHotelsByName(@RequestParam String name) {
        log.debug("Поиск отелей по названию: {}", name);
        List<HotelDto> hotels = hotelService.searchHotelsByName(name);
        return ResponseEntity.ok(hotels);
    }

    /**
     * Поиск отелей по адресу
     */
    @GetMapping("/search/address")
    public ResponseEntity<List<HotelDto>> searchHotelsByAddress(@RequestParam String address) {
        log.debug("Поиск отелей по адресу: {}", address);
        List<HotelDto> hotels = hotelService.searchHotelsByAddress(address);
        return ResponseEntity.ok(hotels);
    }

    /**
     * Поиск отелей по рейтингу
     */
    @GetMapping("/search/rating")
    public ResponseEntity<List<HotelDto>> getHotelsByRating(@RequestParam Double minRating) {
        log.debug("Поиск отелей с рейтингом >= {}", minRating);
        List<HotelDto> hotels = hotelService.getHotelsByRating(minRating);
        return ResponseEntity.ok(hotels);
    }

    /**
     * Обновление отеля (только для администраторов)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long id, 
                                               @Valid @RequestBody CreateHotelRequest request) {
        log.info("Обновление отеля с ID: {}", id);
        return hotelService.updateHotel(id, request)
                .map(hotel -> ResponseEntity.ok(hotel))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удаление отеля (только для администраторов)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        log.info("Удаление отеля с ID: {}", id);
        boolean deleted = hotelService.deleteHotel(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
