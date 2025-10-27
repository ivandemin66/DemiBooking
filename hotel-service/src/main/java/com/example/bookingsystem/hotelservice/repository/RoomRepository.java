package com.example.bookingsystem.hotelservice.repository;

import com.example.bookingsystem.hotelservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с номерами
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Поиск номеров по отелю
     */
    List<Room> findByHotelId(Long hotelId);

    /**
     * Поиск доступных номеров в отеле
     */
    List<Room> findByHotelIdAndAvailableTrue(Long hotelId);

    /**
     * Поиск номера по номеру комнаты и отелю
     */
    Optional<Room> findByHotelIdAndRoomNumber(Long hotelId, String roomNumber);

    /**
     * Получение рекомендованных номеров (отсортированных по times_booked)
     */
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.available = true " +
           "ORDER BY r.timesBooked ASC, r.id ASC")
    List<Room> findRecommendedRoomsByHotelId(@Param("hotelId") Long hotelId);

    /**
     * Получение всех доступных номеров (для общего списка)
     */
    @Query("SELECT r FROM Room r WHERE r.available = true ORDER BY r.hotel.name ASC, r.roomNumber ASC")
    List<Room> findAllAvailableRooms();

    /**
     * Поиск номеров по типу
     */
    List<Room> findByRoomTypeAndAvailableTrue(String roomType);

    /**
     * Поиск номеров по вместимости
     */
    @Query("SELECT r FROM Room r WHERE r.capacity >= :capacity AND r.available = true")
    List<Room> findByCapacityGreaterThanEqualAndAvailableTrue(@Param("capacity") Integer capacity);

    /**
     * Поиск номеров по ценовому диапазону
     */
    @Query("SELECT r FROM Room r WHERE r.pricePerNight BETWEEN :minPrice AND :maxPrice AND r.available = true")
    List<Room> findByPriceRangeAndAvailableTrue(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    /**
     * Получение статистики загруженности номеров
     */
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId ORDER BY r.timesBooked DESC")
    List<Room> findRoomsByHotelIdOrderByTimesBookedDesc(@Param("hotelId") Long hotelId);
}
