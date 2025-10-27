package com.example.bookingsystem.bookingservice.util;

import com.example.bookingsystem.bookingservice.dto.UserDto;
import com.example.bookingsystem.bookingservice.dto.UserRegistrationRequest;
import com.example.bookingsystem.bookingservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Маппер для преобразования между сущностями и DTO пользователей
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Преобразование сущности пользователя в DTO
     */
    @Mapping(target = "password", ignore = true) // Не включаем пароль в DTO
    UserDto toDto(User user);

    /**
     * Преобразование списка сущностей пользователей в список DTO
     */
    List<UserDto> toDtoList(List<User> users);

    /**
     * Преобразование запроса регистрации в сущность
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Пароль обрабатывается отдельно
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    User toEntity(UserRegistrationRequest request);
}
