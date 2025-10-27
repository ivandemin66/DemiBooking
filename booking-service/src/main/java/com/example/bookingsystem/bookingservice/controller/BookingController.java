package com.example.bookingsystem.bookingservice.controller;

import com.example.bookingsystem.bookingservice.dto.BookingDto;
import com.example.bookingsystem.bookingservice.dto.CreateBookingRequest;
import com.example.bookingsystem.bookingservice.entity.Booking;
import com.example.bookingsystem.bookingservice.entity.User;
import com.example.bookingsystem.bookingservice.security.JwtUtil;
import com.example.bookingsystem.bookingservice.service.BookingService;
import com.example.bookingsystem.bookingservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления бронированиями
 */
@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * Создание бронирования
     */
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                    HttpServletRequest httpRequest) {
        log.info("Создание бронирования для номера {} с {} по {}", 
                request.getRoomId(), request.getStartDate(), request.getEndDate());
        
        // Извлекаем пользователя из JWT токена
        String token = extractTokenFromRequest(httpRequest);
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getUserByUsername(jwtUtil.getUsernameFromToken(token))
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        BookingDto booking = bookingService.createBooking(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    /**
     * Получение бронирования по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(@PathVariable Long id,
                                                     HttpServletRequest httpRequest) {
        log.debug("Получение бронирования по ID: {}", id);
        
        String token = extractTokenFromRequest(httpRequest);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        return bookingService.getBookingById(id, userId)
                .map(booking -> ResponseEntity.ok(booking))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получение истории бронирований пользователя
     */
    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(HttpServletRequest httpRequest) {
        log.debug("Получение истории бронирований пользователя");
        
        String token = extractTokenFromRequest(httpRequest);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        List<BookingDto> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Получение истории бронирований пользователя с пагинацией
     */
    @GetMapping("/page")
    public ResponseEntity<Page<BookingDto>> getUserBookingsWithPagination(HttpServletRequest httpRequest,
                                                                          Pageable pageable) {
        log.debug("Получение истории бронирований пользователя с пагинацией");
        
        String token = extractTokenFromRequest(httpRequest);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        Page<BookingDto> bookings = bookingService.getUserBookings(userId, pageable);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Отмена бронирования
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id,
                                              HttpServletRequest httpRequest) {
        log.info("Отмена бронирования с ID: {}", id);
        
        String token = extractTokenFromRequest(httpRequest);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        boolean cancelled = bookingService.cancelBooking(id, userId);
        return cancelled ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Получение всех бронирований (только для администраторов)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        log.debug("Получение всех бронирований администратором");
        List<BookingDto> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    /**
     * Получение бронирований по статусу (только для администраторов)
     */
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingDto>> getBookingsByStatus(@PathVariable Booking.BookingStatus status) {
        log.debug("Получение бронирований со статусом {}", status);
        List<BookingDto> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Очистка старых бронирований (только для администраторов)
     */
    @PostMapping("/admin/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cleanupOldBookings() {
        log.info("Очистка старых бронирований администратором");
        bookingService.cleanupOldPendingBookings();
        return ResponseEntity.ok().build();
    }

    /**
     * Извлечение JWT токена из запроса
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        throw new RuntimeException("JWT токен не найден");
    }
}
