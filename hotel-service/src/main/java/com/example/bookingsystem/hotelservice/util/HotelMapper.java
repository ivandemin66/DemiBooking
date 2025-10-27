package com.example.bookingsystem.hotelservice.util;

import com.example.bookingsystem.hotelservice.dto.CreateHotelRequest;
import com.example.bookingsystem.hotelservice.dto.HotelDto;
import com.example.bookingsystem.hotelservice.dto.RoomDto;
import com.example.bookingsystem.hotelservice.entity.Hotel;
import com.example.bookingsystem.hotelservice.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между сущностями и DTO отелей
 */
@Mapper(componentModel = "spring")
public interface HotelMapper {

    HotelMapper INSTANCE = Mappers.getMapper(HotelMapper.class);

    /**
     * Преобразование сущности отеля в DTO
     */
    HotelDto toDto(Hotel hotel);

    /**
     * Преобразование списка сущностей отелей в список DTO
     */
    List<HotelDto> toDtoList(List<Hotel> hotels);

    /**
     * Преобразование запроса создания отеля в сущность
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rooms", ignore = true)
    Hotel toEntity(CreateHotelRequest request);

    /**
     * Преобразование сущности номера в DTO
     */
    @Mapping(target = "hotelId", source = "hotel.id")
    RoomDto roomToDto(Room room);

    /**
     * Преобразование списка сущностей номеров в список DTO
     */
    List<RoomDto> roomsToDtoList(List<Room> rooms);
}
