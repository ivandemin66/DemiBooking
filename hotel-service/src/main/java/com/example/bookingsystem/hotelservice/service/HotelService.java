package com.example.bookingsystem.hotelservice.service;

import com.example.bookingsystem.hotelservice.dto.CreateHotelRequest;
import com.example.bookingsystem.hotelservice.dto.HotelDto;
import com.example.bookingsystem.hotelservice.entity.Hotel;
import com.example.bookingsystem.hotelservice.repository.HotelRepository;
import com.example.bookingsystem.hotelservice.util.HotelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с отелями
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    /**
     * Создание нового отеля
     */
    @Transactional
    public HotelDto createHotel(CreateHotelRequest request) {
        log.info("Создание нового отеля: {}", request.getName());
        
        Hotel hotel = hotelMapper.toEntity(request);
        Hotel savedHotel = hotelRepository.save(hotel);
        
        log.info("Отель успешно создан с ID: {}", savedHotel.getId());
        return hotelMapper.toDto(savedHotel);
    }

    /**
     * Получение отеля по ID
     */
    public Optional<HotelDto> getHotelById(Long id) {
        log.debug("Поиск отеля по ID: {}", id);
        return hotelRepository.findById(id)
                .map(hotelMapper::toDto);
    }

    /**
     * Получение всех отелей
     */
    public List<HotelDto> getAllHotels() {
        log.debug("Получение списка всех отелей");
        List<Hotel> hotels = hotelRepository.findAll();
        return hotelMapper.toDtoList(hotels);
    }

    /**
     * Поиск отелей по названию
     */
    public List<HotelDto> searchHotelsByName(String name) {
        log.debug("Поиск отелей по названию: {}", name);
        List<Hotel> hotels = hotelRepository.findByNameContainingIgnoreCase(name);
        return hotelMapper.toDtoList(hotels);
    }

    /**
     * Поиск отелей по адресу
     */
    public List<HotelDto> searchHotelsByAddress(String address) {
        log.debug("Поиск отелей по адресу: {}", address);
        List<Hotel> hotels = hotelRepository.findByAddressContainingIgnoreCase(address);
        return hotelMapper.toDtoList(hotels);
    }

    /**
     * Поиск отелей с рейтингом выше указанного
     */
    public List<HotelDto> getHotelsByRating(Double minRating) {
        log.debug("Поиск отелей с рейтингом >= {}", minRating);
        List<Hotel> hotels = hotelRepository.findByRatingGreaterThanEqualOrderByRatingDesc(minRating);
        return hotelMapper.toDtoList(hotels);
    }

    /**
     * Обновление отеля
     */
    @Transactional
    public Optional<HotelDto> updateHotel(Long id, CreateHotelRequest request) {
        log.info("Обновление отеля с ID: {}", id);
        
        return hotelRepository.findById(id)
                .map(hotel -> {
                    hotel.setName(request.getName());
                    hotel.setAddress(request.getAddress());
                    hotel.setDescription(request.getDescription());
                    hotel.setPhoneNumber(request.getPhoneNumber());
                    hotel.setEmail(request.getEmail());
                    hotel.setRating(request.getRating());
                    
                    Hotel updatedHotel = hotelRepository.save(hotel);
                    log.info("Отель с ID {} успешно обновлен", id);
                    return hotelMapper.toDto(updatedHotel);
                });
    }

    /**
     * Удаление отеля
     */
    @Transactional
    public boolean deleteHotel(Long id) {
        log.info("Удаление отеля с ID: {}", id);
        
        if (hotelRepository.existsById(id)) {
            hotelRepository.deleteById(id);
            log.info("Отель с ID {} успешно удален", id);
            return true;
        }
        
        log.warn("Отель с ID {} не найден для удаления", id);
        return false;
    }

    /**
     * Проверка существования отеля
     */
    public boolean hotelExists(Long id) {
        return hotelRepository.existsById(id);
    }
}
