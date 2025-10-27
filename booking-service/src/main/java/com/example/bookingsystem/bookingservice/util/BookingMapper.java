package com.example.bookingsystem.bookingservice.util;

import com.example.bookingsystem.bookingservice.dto.BookingDto;
import com.example.bookingsystem.bookingservice.dto.CreateBookingRequest;
import com.example.bookingsystem.bookingservice.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между сущностями и DTO бронирований
 */
@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    /**
     * Преобразование сущности бронирования в DTO
     */
    @Mapping(target = "userId", source = "user.id")
    BookingDto toDto(Booking booking);

    /**
     * Преобразование списка сущностей бронирований в список DTO
     */
    List<BookingDto> toDtoList(List<Booking> bookings);

    /**
     * Преобразование запроса создания бронирования в сущность
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hotelId", ignore = true) // Будет установлен из номера
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "totalPrice", ignore = true) // Будет рассчитан
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Booking toEntity(CreateBookingRequest request);
}
