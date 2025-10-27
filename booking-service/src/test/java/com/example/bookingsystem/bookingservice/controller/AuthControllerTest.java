package com.example.bookingsystem.bookingservice.controller;


import com.example.bookingsystem.bookingservice.dto.UserAuthRequest;
import com.example.bookingsystem.bookingservice.dto.UserDto;
import com.example.bookingsystem.bookingservice.dto.UserRegistrationRequest;
import com.example.bookingsystem.bookingservice.entity.User;
import com.example.bookingsystem.bookingservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Тесты для AuthController
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserDto userDto;
    private UserRegistrationRequest registrationRequest;
    private UserAuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("test_user")
                .email("test@example.com")
                .firstName("Тест")
                .lastName("Пользователь")
                .role(User.UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("test_user")
                .email("test@example.com")
                .firstName("Тест")
                .lastName("Пользователь")
                .role(User.UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        registrationRequest = UserRegistrationRequest.builder()
                .username("test_user")
                .password("password123")
                .email("test@example.com")
                .firstName("Тест")
                .lastName("Пользователь")
                .role(User.UserRole.USER)
                .build();

        authRequest = UserAuthRequest.builder()
                .username("test_user")
                .password("password123")
                .build();
    }

    @Test
    void registerUser_ShouldReturnAuthResponse() throws Exception {
        // Given
        when(userService.registerUser(any(UserRegistrationRequest.class))).thenReturn(userDto);
        when(userService.getUserByUsername("test_user")).thenReturn(java.util.Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value("test_user"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnAuthResponse() throws Exception {
        // Given
        when(userService.authenticateUser(any(UserAuthRequest.class))).thenReturn(java.util.Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("test_user"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void authenticateUser_WithInvalidCredentials_ShouldReturnError() throws Exception {
        // Given
        when(userService.authenticateUser(any(UserAuthRequest.class))).thenReturn(java.util.Optional.empty());

        // When & Then
        mockMvc.perform(post("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registerUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        UserRegistrationRequest invalidRequest = UserRegistrationRequest.builder()
                .username("") // Пустое имя пользователя
                .password("123") // Слишком короткий пароль
                .build();

        // When & Then
        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
