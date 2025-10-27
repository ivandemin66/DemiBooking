package com.example.bookingsystem.hotelservice.util;

import com.example.bookingsystem.hotelservice.dto.CreateRoomRequest;
import com.example.bookingsystem.hotelservice.dto.RoomDto;
import com.example.bookingsystem.hotelservice.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между сущностями и DTO номеров
 */
@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    /**
     * Преобразование сущности номера в DTO
     */
    @Mapping(target = "hotelId", source = "hotel.id")
    RoomDto toDto(Room room);

    /**
     * Преобразование списка сущностей номеров в список DTO
     */
    List<RoomDto> toDtoList(List<Room> rooms);

    /**
     * Преобразование запроса создания номера в сущность
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hotel", ignore = true)
    @Mapping(target = "available", constant = "true")
    @Mapping(target = "timesBooked", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Room toEntity(CreateRoomRequest request);
}
