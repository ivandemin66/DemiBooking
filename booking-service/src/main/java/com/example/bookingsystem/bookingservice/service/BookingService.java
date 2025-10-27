package com.example.bookingsystem.bookingservice.service;

import com.example.bookingsystem.bookingservice.client.HotelServiceClient;
import com.example.bookingsystem.bookingservice.dto.*;
import com.example.bookingsystem.bookingservice.entity.Booking;
import com.example.bookingsystem.bookingservice.entity.User;
import com.example.bookingsystem.bookingservice.repository.BookingRepository;
import com.example.bookingsystem.bookingservice.util.BookingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с бронированиями
 * Реализует саги с компенсацией для распределенных транзакций
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final HotelServiceClient hotelServiceClient;
    private final BookingMapper bookingMapper;

    /**
     * Создание бронирования с сагой
     */
    @Transactional
    public BookingDto createBooking(CreateBookingRequest request, User user) {
        log.info("Создание бронирования для пользователя {} на номер {} с {} по {}", 
                user.getUsername(), request.getRoomId(), request.getStartDate(), request.getEndDate());
        
        // Генерируем requestId для идемпотентности
        String requestId = request.getRequestId() != null ? request.getRequestId() : UUID.randomUUID().toString();
        
        // Проверяем идемпотентность
        Optional<Booking> existingBooking = bookingRepository.findByRequestId(requestId);
        if (existingBooking.isPresent()) {
            log.info("Бронирование с requestId {} уже существует (идемпотентность)", requestId);
            return bookingMapper.toDto(existingBooking.get());
        }
        
        // Создаем бронирование в статусе PENDING
        Booking booking = bookingMapper.toEntity(request);
        booking.setUser(user);
        booking.setRequestId(requestId);
        booking.setStatus(Booking.BookingStatus.PENDING);
        
        // Сохраняем в локальной транзакции
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Бронирование создано в статусе PENDING с ID: {}", savedBooking.getId());
        
        try {
            // Шаг 1: Подтверждение доступности номера в Hotel Service
            RoomAvailabilityRequest availabilityRequest = RoomAvailabilityRequest.builder()
                    .roomId(request.getRoomId())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .requestId(requestId)
                    .build();
            
            RoomAvailabilityResponse response = confirmRoomAvailabilityWithRetry(availabilityRequest);
            
            if (response.isAvailable()) {
                // Шаг 2: Подтверждение бронирования
                savedBooking.setStatus(Booking.BookingStatus.CONFIRMED);
                savedBooking = bookingRepository.save(savedBooking);
                log.info("Бронирование {} успешно подтверждено", savedBooking.getId());
                
                return bookingMapper.toDto(savedBooking);
            } else {
                // Компенсация: отменяем бронирование
                savedBooking.setStatus(Booking.BookingStatus.CANCELLED);
                bookingRepository.save(savedBooking);
                log.warn("Бронирование {} отменено: {}", savedBooking.getId(), response.getMessage());
                
                throw new RuntimeException("Номер недоступен: " + response.getMessage());
            }
            
        } catch (Exception e) {
            // Компенсация при ошибке
            log.error("Ошибка при создании бронирования {}: {}", savedBooking.getId(), e.getMessage());
            performCompensation(savedBooking, requestId);
            throw new RuntimeException("Ошибка при создании бронирования: " + e.getMessage(), e);
        }
    }

    /**
     * Подтверждение доступности номера с повторными попытками
     */
    private RoomAvailabilityResponse confirmRoomAvailabilityWithRetry(RoomAvailabilityRequest request) {
        log.debug("Попытка подтверждения доступности номера {} с requestId {}",
                request.getRoomId(), request.getRequestId());

        final int maxAttempts = 3;
        final long delayMs = 1000L;
        int attempt = 0;

        while (true) {
            attempt++;
            try {
                return hotelServiceClient.confirmRoomAvailability(request.getRoomId(), request);
            } catch (Exception e) {
                log.warn("Попытка {}: ошибка при подтверждении доступности номера {}: {}", attempt, request.getRoomId(), e.getMessage());
                if (attempt >= maxAttempts) {
                    throw e;
                }
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting to retry", ie);
                }
            }
        }
    }

    /**
     * Выполнение компенсации
     */
    private void performCompensation(Booking booking, String requestId) {
        log.info("Выполнение компенсации для бронирования {} с requestId {}", booking.getId(), requestId);
        
        try {
            // Освобождаем блокировку в Hotel Service
            hotelServiceClient.releaseRoomBlock(booking.getRoomId(), requestId);
            log.info("Блокировка номера {} успешно снята", booking.getRoomId());
        } catch (Exception e) {
            log.error("Ошибка при снятии блокировки номера {}: {}", booking.getRoomId(), e.getMessage());
        }
    }

    /**
     * Получение бронирования по ID
     */
    public Optional<BookingDto> getBookingById(Long id, Long userId) {
        log.debug("Получение бронирования {} для пользователя {}", id, userId);
        return bookingRepository.findByIdAndUserId(id, userId)
                .map(bookingMapper::toDto);
    }

    /**
     * Получение бронирований пользователя
     */
    public List<BookingDto> getUserBookings(Long userId) {
        log.debug("Получение бронирований пользователя {}", userId);
        List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return bookingMapper.toDtoList(bookings);
    }

    /**
     * Получение бронирований пользователя с пагинацией
     */
    public Page<BookingDto> getUserBookings(Long userId, Pageable pageable) {
        log.debug("Получение бронирований пользователя {} с пагинацией", userId);
        Page<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return bookings.map(bookingMapper::toDto);
    }

    /**
     * Отмена бронирования
     */
    @Transactional
    public boolean cancelBooking(Long bookingId, Long userId) {
        log.info("Отмена бронирования {} пользователем {}", bookingId, userId);
        
        return bookingRepository.findByIdAndUserId(bookingId, userId)
                .map(booking -> {
                    if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
                        booking.setStatus(Booking.BookingStatus.CANCELLED);
                        bookingRepository.save(booking);
                        
                        // Уведомляем Hotel Service об отмене (уменьшаем счетчик)
                        try {
                            // В реальной системе здесь был бы вызов для уменьшения счетчика
                            log.info("Бронирование {} отменено", bookingId);
                        } catch (Exception e) {
                            log.error("Ошибка при уведомлении Hotel Service об отмене: {}", e.getMessage());
                        }
                        
                        return true;
                    } else {
                        log.warn("Невозможно отменить бронирование {} в статусе {}", 
                                bookingId, booking.getStatus());
                        return false;
                    }
                })
                .orElse(false);
    }

    /**
     * Получение всех бронирований (для администраторов)
     */
    public List<BookingDto> getAllBookings() {
        log.debug("Получение всех бронирований");
        List<Booking> bookings = bookingRepository.findAll();
        return bookingMapper.toDtoList(bookings);
    }

    /**
     * Получение бронирований по статусу
     */
    public List<BookingDto> getBookingsByStatus(Booking.BookingStatus status) {
        log.debug("Получение бронирований со статусом {}", status);
        List<Booking> bookings = bookingRepository.findByStatusOrderByCreatedAtDesc(status);
        return bookingMapper.toDtoList(bookings);
    }

    /**
     * Очистка старых бронирований в статусе PENDING
     */
    @Transactional
    public void cleanupOldPendingBookings() {
        log.info("Очистка старых бронирований в статусе PENDING");
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusHours(1); // Старше 1 часа
        List<Booking> oldBookings = bookingRepository.findOldPendingBookings(cutoffDate);
        
        for (Booking booking : oldBookings) {
            booking.setStatus(Booking.BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            
            // Выполняем компенсацию
            performCompensation(booking, booking.getRequestId());
        }
        
        log.info("Очищено {} старых бронирований", oldBookings.size());
    }

    /**
     * Проверка конфликта дат для номера
     */
    public boolean hasDateConflict(Long roomId, LocalDate startDate, LocalDate endDate) {
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(
                roomId, startDate, endDate, 
                List.of(Booking.BookingStatus.CONFIRMED, Booking.BookingStatus.PENDING)
        );
        
        return !conflictingBookings.isEmpty();
    }

    /**
     * Получение статистики бронирований пользователя
     */
    public Long getUserBookingCount(Long userId, Booking.BookingStatus status) {
        return bookingRepository.countByUserIdAndStatus(userId, status);
    }
}
