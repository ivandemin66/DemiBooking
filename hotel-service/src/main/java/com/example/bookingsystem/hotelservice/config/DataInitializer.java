package com.example.bookingsystem.hotelservice.config;

import com.example.bookingsystem.hotelservice.entity.Hotel;
import com.example.bookingsystem.hotelservice.entity.Room;
import com.example.bookingsystem.hotelservice.repository.HotelRepository;
import com.example.bookingsystem.hotelservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Инициализация тестовых данных для Hotel Service
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Инициализация тестовых данных для Hotel Service");
        
        if (hotelRepository.count() == 0) {
            initializeHotels();
            initializeRooms();
            log.info("Тестовые данные успешно инициализированы");
        } else {
            log.info("Тестовые данные уже существуют, пропускаем инициализацию");
        }
    }

    /**
     * Создание тестовых отелей
     */
    private void initializeHotels() {
        log.info("Создание тестовых отелей");
        
        Hotel hotel1 = Hotel.builder()
                .name("Гранд Отель Москва")
                .address("Тверская улица, 1, Москва")
                .description("Роскошный отель в центре Москвы с видом на Кремль")
                .phoneNumber("+7 (495) 123-45-67")
                .email("info@grandhotel-moscow.ru")
                .rating(4.8)
                .createdAt(LocalDateTime.now())
                .build();
        
        Hotel hotel2 = Hotel.builder()
                .name("Отель Санкт-Петербург")
                .address("Невский проспект, 25, Санкт-Петербург")
                .description("Элегантный отель на главной улице Санкт-Петербурга")
                .phoneNumber("+7 (812) 234-56-78")
                .email("info@hotel-spb.ru")
                .rating(4.6)
                .createdAt(LocalDateTime.now())
                .build();
        
        Hotel hotel3 = Hotel.builder()
                .name("Бизнес Отель Екатеринбург")
                .address("Ленина проспект, 5, Екатеринбург")
                .description("Современный бизнес-отель в центре Екатеринбурга")
                .phoneNumber("+7 (343) 345-67-89")
                .email("info@business-hotel-ekb.ru")
                .rating(4.4)
                .createdAt(LocalDateTime.now())
                .build();
        
        hotelRepository.save(hotel1);
        hotelRepository.save(hotel2);
        hotelRepository.save(hotel3);
        
        log.info("Создано {} отелей", hotelRepository.count());
    }

    /**
     * Создание тестовых номеров
     */
    private void initializeRooms() {
        log.info("Создание тестовых номеров");
        
        // Получаем созданные отели
        Hotel hotel1 = hotelRepository.findByName("Гранд Отель Москва").orElseThrow();
        Hotel hotel2 = hotelRepository.findByName("Отель Санкт-Петербург").orElseThrow();
        Hotel hotel3 = hotelRepository.findByName("Бизнес Отель Екатеринбург").orElseThrow();
        
        // Номера для первого отеля
        createRoom(hotel1, "101", "Люкс", 2, new BigDecimal("15000.00"), "Роскошный люкс с видом на Кремль", "WiFi, мини-бар, кондиционер, сейф");
        createRoom(hotel1, "102", "Стандарт", 1, new BigDecimal("8000.00"), "Стандартный номер с удобствами", "WiFi, кондиционер, сейф");
        createRoom(hotel1, "103", "Семейный", 4, new BigDecimal("12000.00"), "Семейный номер для 4 человек", "WiFi, кондиционер, сейф, диван");
        createRoom(hotel1, "201", "Бизнес", 2, new BigDecimal("10000.00"), "Бизнес-номер с рабочим местом", "WiFi, кондиционер, сейф, рабочий стол");
        
        // Номера для второго отеля
        createRoom(hotel2, "301", "Стандарт", 2, new BigDecimal("6000.00"), "Стандартный номер с видом на Невский", "WiFi, кондиционер, сейф");
        createRoom(hotel2, "302", "Улучшенный", 2, new BigDecimal("8000.00"), "Улучшенный номер с балконом", "WiFi, кондиционер, сейф, балкон");
        createRoom(hotel2, "401", "Люкс", 2, new BigDecimal("12000.00"), "Люкс с панорамным видом", "WiFi, мини-бар, кондиционер, сейф, балкон");
        
        // Номера для третьего отеля
        createRoom(hotel3, "501", "Эконом", 1, new BigDecimal("3000.00"), "Экономный номер для бизнес-поездок", "WiFi, кондиционер");
        createRoom(hotel3, "502", "Стандарт", 2, new BigDecimal("4500.00"), "Стандартный бизнес-номер", "WiFi, кондиционер, сейф");
        createRoom(hotel3, "601", "Конференц-зал", 20, new BigDecimal("25000.00"), "Большой конференц-зал", "WiFi, проектор, флипчарт, кофе-брейк");
        
        log.info("Создано {} номеров", roomRepository.count());
    }

    /**
     * Создание номера
     */
    private void createRoom(Hotel hotel, String roomNumber, String roomType, Integer capacity, 
                           BigDecimal pricePerNight, String description, String amenities) {
        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(roomNumber)
                .roomType(roomType)
                .capacity(capacity)
                .pricePerNight(pricePerNight)
                .description(description)
                .amenities(amenities)
                .available(true)
                .timesBooked(0)
                .createdAt(LocalDateTime.now())
                .build();
        
        roomRepository.save(room);
    }
}
