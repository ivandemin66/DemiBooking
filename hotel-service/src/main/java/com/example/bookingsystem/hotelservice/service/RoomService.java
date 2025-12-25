package com.example.bookingsystem.hotelservice.service;

import com.example.bookingsystem.hotelservice.dto.CreateRoomRequest;
import com.example.bookingsystem.hotelservice.dto.RoomAvailabilityRequest;
import com.example.bookingsystem.hotelservice.dto.RoomAvailabilityResponse;
import com.example.bookingsystem.hotelservice.dto.RoomDto;
import com.example.bookingsystem.hotelservice.entity.Hotel;
import com.example.bookingsystem.hotelservice.entity.Room;
import com.example.bookingsystem.hotelservice.repository.HotelRepository;
import com.example.bookingsystem.hotelservice.repository.RoomRepository;
import com.example.bookingsystem.hotelservice.util.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Сервис для работы с номерами
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;
    
    // Кэш для временных блокировок номеров
    private final ConcurrentMap<String, RoomAvailabilityRequest> temporaryBlocks = new ConcurrentHashMap<>();

    /**
     * Создание нового номера
     */
    @Transactional
    public RoomDto createRoom(CreateRoomRequest request) {
        log.info("Создание нового номера: {} в отеле {}", request.getRoomNumber(), request.getHotelId());
        
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Отель с ID " + request.getHotelId() + " не найден"));
        
        // Проверяем, что номер с таким номером не существует в отеле
        if (roomRepository.findByHotelIdAndRoomNumber(request.getHotelId(), request.getRoomNumber()).isPresent()) {
            throw new IllegalArgumentException("Номер " + request.getRoomNumber() + " уже существует в отеле");
        }
        
        Room room = roomMapper.toEntity(request);
        room.setHotel(hotel);
        
        Room savedRoom = roomRepository.save(room);
        log.info("Номер успешно создан с ID: {}", savedRoom.getId());
        
        return roomMapper.toDto(savedRoom);
    }

    /**
     * Получение номера по ID
     */
    public Optional<RoomDto> getRoomById(Long id) {
        log.debug("Поиск номера по ID: {}", id);
        return roomRepository.findById(id)
                .map(roomMapper::toDto);
    }

    /**
     * Получение всех номеров отеля
     */
    public List<RoomDto> getRoomsByHotelId(Long hotelId) {
        log.debug("Получение номеров отеля с ID: {}", hotelId);
        List<Room> rooms = roomRepository.findByHotelId(hotelId);
        return roomMapper.toDtoList(rooms);
    }

    /**
     * Получение доступных номеров отеля
     */
    public List<RoomDto> getAvailableRoomsByHotelId(Long hotelId) {
        log.debug("Получение доступных номеров отеля с ID: {}", hotelId);
        List<Room> rooms = roomRepository.findByHotelIdAndAvailableTrue(hotelId);
        return roomMapper.toDtoList(rooms);
    }

    /**
     * Получение рекомендованных номеров (алгоритм планирования занятости)
     */
    public List<RoomDto> getRecommendedRooms(Long hotelId) {
        log.debug("Получение рекомендованных номеров для отеля с ID: {}", hotelId);
        List<Room> rooms = roomRepository.findRecommendedRoomsByHotelId(hotelId);
        return roomMapper.toDtoList(rooms);
    }

    /**
     * Получение всех доступных номеров
     */
    public List<RoomDto> getAllAvailableRooms() {
        log.debug("Получение всех доступных номеров");
        List<Room> rooms = roomRepository.findAllAvailableRooms();
        return roomMapper.toDtoList(rooms);
    }

    /**
     * Поиск номеров по типу
     */
    public List<RoomDto> getRoomsByType(String roomType) {
        log.debug("Поиск номеров по типу: {}", roomType);
        List<Room> rooms = roomRepository.findByRoomTypeAndAvailableTrue(roomType);
        return roomMapper.toDtoList(rooms);
    }

    /**
     * Поиск номеров по вместимости
     */
    public List<RoomDto> getRoomsByCapacity(Integer capacity) {
        log.debug("Поиск номеров по вместимости: {}", capacity);
        List<Room> rooms = roomRepository.findByCapacityGreaterThanEqualAndAvailableTrue(capacity);
        return roomMapper.toDtoList(rooms);
    }

    /**
     * Поиск номеров по ценовому диапазону
     */
    public List<RoomDto> getRoomsByPriceRange(Double minPrice, Double maxPrice) {
        log.debug("Поиск номеров по ценовому диапазону: {} - {}", minPrice, maxPrice);
        List<Room> rooms = roomRepository.findByPriceRangeAndAvailableTrue(minPrice, maxPrice);
        return roomMapper.toDtoList(rooms);
    }

    /**
     * Получение статистики загруженности номеров
     */
    public List<RoomDto> getRoomStatistics(Long hotelId) {
        log.debug("Получение статистики загруженности номеров отеля с ID: {}", hotelId);
        List<Room> rooms = roomRepository.findRoomsByHotelIdOrderByTimesBookedDesc(hotelId);
        return roomMapper.toDtoList(rooms);
    }

    /**
     * Подтверждение доступности номера (для двухшагового подтверждения)
     */
    @Transactional
    public RoomAvailabilityResponse confirmRoomAvailability(RoomAvailabilityRequest request) {
        log.info("Подтверждение доступности номера {} на период {} - {}", 
                request.getRoomId(), request.getStartDate(), request.getEndDate());
        
        // Проверяем идемпотентность
        if (request.getRequestId() != null && temporaryBlocks.containsKey(request.getRequestId())) {
            log.info("Запрос {} уже обработан (идемпотентность)", request.getRequestId());
            Room existingRoom = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new IllegalArgumentException("Номер не найден"));
            return RoomAvailabilityResponse.builder()
                    .available(true)
                    .message("Номер уже заблокирован")
                    .requestId(request.getRequestId())
                    .roomId(request.getRoomId())
                    .hotelId(existingRoom.getHotel().getId())
                    .build();
        }
        
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Номер с ID " + request.getRoomId() + " не найден"));
        
        Long hotelId = room.getHotel().getId();
        
        if (!room.getAvailable()) {
            log.warn("Номер {} недоступен", request.getRoomId());
            return RoomAvailabilityResponse.builder()
                    .available(false)
                    .message("Номер недоступен")
                    .requestId(request.getRequestId())
                    .roomId(request.getRoomId())
                    .hotelId(hotelId)
                    .build();
        }
        
        // Проверяем пересечение дат (упрощенная логика)
        if (hasDateConflict(request.getRoomId(), request.getStartDate(), request.getEndDate())) {
            log.warn("Номер {} занят на указанные даты", request.getRoomId());
            return RoomAvailabilityResponse.builder()
                    .available(false)
                    .message("Номер занят на указанные даты")
                    .requestId(request.getRequestId())
                    .roomId(request.getRoomId())
                    .hotelId(hotelId)
                    .build();
        }
        
        // Временно блокируем номер
        if (request.getRequestId() != null) {
            temporaryBlocks.put(request.getRequestId(), request);
        }
        
        log.info("Номер {} успешно заблокирован на период {} - {}", 
                request.getRoomId(), request.getStartDate(), request.getEndDate());
        
        return RoomAvailabilityResponse.builder()
                .available(true)
                .message("Номер доступен и заблокирован")
                .requestId(request.getRequestId())
                .roomId(request.getRoomId())
                .hotelId(hotelId)
                .build();
    }

    /**
     * Освобождение временной блокировки номера (компенсация)
     */
    @Transactional
    public void releaseRoomBlock(String requestId) {
        log.info("Освобождение блокировки номера для запроса: {}", requestId);
        
        RoomAvailabilityRequest request = temporaryBlocks.remove(requestId);
        if (request != null) {
            log.info("Блокировка номера {} успешно снята", request.getRoomId());
        } else {
            log.warn("Блокировка для запроса {} не найдена", requestId);
        }
    }

    /**
     * Подтверждение бронирования (увеличиваем счетчик)
     */
    @Transactional
    public void confirmBooking(Long roomId) {
        log.info("Подтверждение бронирования номера: {}", roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Номер с ID " + roomId + " не найден"));
        
        room.incrementTimesBooked();
        roomRepository.save(room);
        
        log.info("Счетчик бронирований номера {} увеличен до {}", roomId, room.getTimesBooked());
    }

    /**
     * Отмена бронирования (уменьшаем счетчик)
     */
    @Transactional
    public void cancelBooking(Long roomId) {
        log.info("Отмена бронирования номера: {}", roomId);
        
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Номер с ID " + roomId + " не найден"));
        
        room.decrementTimesBooked();
        roomRepository.save(room);
        
        log.info("Счетчик бронирований номера {} уменьшен до {}", roomId, room.getTimesBooked());
    }

    /**
     * Проверка конфликта дат (упрощенная реализация)
     */
    private boolean hasDateConflict(Long roomId, LocalDate startDate, LocalDate endDate) {
        // В реальной системе здесь была бы проверка с таблицей бронирований
        // Для демонстрации возвращаем false
        return false;
    }
}
