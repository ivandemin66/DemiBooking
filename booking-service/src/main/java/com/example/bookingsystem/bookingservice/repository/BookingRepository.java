package com.example.bookingsystem.bookingservice.repository;

import com.example.bookingsystem.bookingservice.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с бронированиями
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Поиск бронирований пользователя
     */
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Поиск бронирований пользователя с пагинацией
     */
    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Поиск бронирования по ID и пользователю
     */
    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    /**
     * Поиск бронирований по статусу
     */
    List<Booking> findByStatusOrderByCreatedAtDesc(Booking.BookingStatus status);

    /**
     * Поиск бронирований по номеру
     */
    List<Booking> findByRoomIdAndStatusIn(Long roomId, List<Booking.BookingStatus> statuses);

    /**
     * Поиск бронирований по отелю
     */
    List<Booking> findByHotelIdAndStatusIn(Long hotelId, List<Booking.BookingStatus> statuses);

    /**
     * Проверка пересечения дат для номера
     */
    @Query("SELECT b FROM Booking b WHERE b.roomId = :roomId AND b.status IN :statuses " +
           "AND ((b.startDate <= :endDate AND b.endDate >= :startDate))")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate,
                                         @Param("statuses") List<Booking.BookingStatus> statuses);

    /**
     * Поиск бронирований по requestId (для идемпотентности)
     */
    Optional<Booking> findByRequestId(String requestId);

    /**
     * Статистика бронирований пользователя
     */
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.user.id = :userId AND b.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Booking.BookingStatus status);

    /**
     * Поиск старых бронирований в статусе PENDING
     */
    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.createdAt < :cutoffDate")
    List<Booking> findOldPendingBookings(@Param("cutoffDate") java.time.LocalDateTime cutoffDate);
}
