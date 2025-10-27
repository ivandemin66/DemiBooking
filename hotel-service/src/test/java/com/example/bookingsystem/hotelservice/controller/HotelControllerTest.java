package com.example.bookingsystem.hotelservice.controller;

import com.example.bookingsystem.hotelservice.dto.CreateHotelRequest;
import com.example.bookingsystem.hotelservice.entity.Hotel;
import com.example.bookingsystem.hotelservice.repository.HotelRepository;
import com.example.bookingsystem.hotelservice.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для HotelController
 */
@WebMvcTest(HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @MockBean
    private HotelRepository hotelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Hotel testHotel;
    private CreateHotelRequest createRequest;

    @BeforeEach
    void setUp() {
        testHotel = Hotel.builder()
                .id(1L)
                .name("Тестовый Отель")
                .address("Тестовая улица, 1")
                .description("Описание тестового отеля")
                .phoneNumber("+7 (495) 123-45-67")
                .email("test@hotel.com")
                .rating(4.5)
                .createdAt(LocalDateTime.now())
                .build();

        createRequest = CreateHotelRequest.builder()
                .name("Тестовый Отель")
                .address("Тестовая улица, 1")
                .description("Описание тестового отеля")
                .phoneNumber("+7 (495) 123-45-67")
                .email("test@hotel.com")
                .rating(4.5)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createHotel_ShouldReturnCreatedHotel() throws Exception {
        // Given
        when(hotelService.createHotel(any(CreateHotelRequest.class))).thenReturn(
                com.example.bookingsystem.hotelservice.dto.HotelDto.builder()
                        .id(1L)
                        .name("Тестовый Отель")
                        .address("Тестовая улица, 1")
                        .description("Описание тестового отеля")
                        .phoneNumber("+7 (495) 123-45-67")
                        .email("test@hotel.com")
                        .rating(4.5)
                        .build()
        );

        // When & Then
        mockMvc.perform(post("/api/hotels")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Тестовый Отель"))
                .andExpect(jsonPath("$.address").value("Тестовая улица, 1"))
                .andExpect(jsonPath("$.rating").value(4.5));
    }

    @Test
    void getAllHotels_ShouldReturnListOfHotels() throws Exception {
        // Given
        when(hotelService.getAllHotels()).thenReturn(List.of(
                com.example.bookingsystem.hotelservice.dto.HotelDto.builder()
                        .id(1L)
                        .name("Отель 1")
                        .address("Адрес 1")
                        .rating(4.0)
                        .build(),
                com.example.bookingsystem.hotelservice.dto.HotelDto.builder()
                        .id(2L)
                        .name("Отель 2")
                        .address("Адрес 2")
                        .rating(4.5)
                        .build()
        ));

        // When & Then
        mockMvc.perform(get("/api/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Отель 1"))
                .andExpect(jsonPath("$[1].name").value("Отель 2"));
    }

    @Test
    void getHotelById_WhenHotelExists_ShouldReturnHotel() throws Exception {
        // Given
        when(hotelService.getHotelById(1L)).thenReturn(Optional.of(
                com.example.bookingsystem.hotelservice.dto.HotelDto.builder()
                        .id(1L)
                        .name("Тестовый Отель")
                        .address("Тестовая улица, 1")
                        .rating(4.5)
                        .build()
        ));

        // When & Then
        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тестовый Отель"));
    }

    @Test
    void getHotelById_WhenHotelNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(hotelService.getHotelById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/hotels/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchHotelsByName_ShouldReturnFilteredHotels() throws Exception {
        // Given
        when(hotelService.searchHotelsByName("Тест")).thenReturn(List.of(
                com.example.bookingsystem.hotelservice.dto.HotelDto.builder()
                        .id(1L)
                        .name("Тестовый Отель")
                        .address("Тестовая улица, 1")
                        .rating(4.5)
                        .build()
        ));

        // When & Then
        mockMvc.perform(get("/api/hotels/search?name=Тест"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Тестовый Отель"));
    }
}
