package com.example.bookingsystem.hotelservice.repository;

import com.example.bookingsystem.hotelservice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с отелями
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    /**
     * Поиск отеля по названию
     */
    Optional<Hotel> findByName(String name);

    /**
     * Поиск отелей по адресу
     */
    List<Hotel> findByAddressContainingIgnoreCase(String address);

    /**
     * Поиск отелей с рейтингом выше указанного
     */
    @Query("SELECT h FROM Hotel h WHERE h.rating >= :minRating ORDER BY h.rating DESC")
    List<Hotel> findByRatingGreaterThanEqualOrderByRatingDesc(@Param("minRating") Double minRating);

    /**
     * Поиск отелей по названию (частичное совпадение)
     */
    @Query("SELECT h FROM Hotel h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Hotel> findByNameContainingIgnoreCase(@Param("name") String name);
}
