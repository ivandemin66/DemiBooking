package com.example.bookingsystem.bookingservice.controller;

import com.example.bookingsystem.bookingservice.dto.AuthResponse;
import com.example.bookingsystem.bookingservice.dto.UserAuthRequest;
import com.example.bookingsystem.bookingservice.dto.UserDto;
import com.example.bookingsystem.bookingservice.dto.UserRegistrationRequest;
import com.example.bookingsystem.bookingservice.entity.User;
import com.example.bookingsystem.bookingservice.security.JwtUtil;
import com.example.bookingsystem.bookingservice.service.UserService;
import com.example.bookingsystem.bookingservice.util.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для аутентификации и регистрации
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    /**
     * Регистрация пользователя
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Регистрация нового пользователя: {}", request.getUsername());
        
        UserDto userDto = userService.registerUser(request);
        User user = userService.getUserByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Ошибка при получении пользователя"));
        
        String token = jwtUtil.generateToken(user);
        
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1 час
                .user(userDto)
                .build();
        
        log.info("Пользователь {} успешно зарегистрирован", request.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Аутентификация пользователя
     */
    @PostMapping("/auth")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody UserAuthRequest request) {
        log.info("Аутентификация пользователя: {}", request.getUsername());
        
        User user = userService.authenticateUser(request)
                .orElseThrow(() -> new RuntimeException("Неверные учетные данные"));
        
        String token = jwtUtil.generateToken(user);
        UserDto userDto = userMapper.toDto(user);
        
        AuthResponse response = AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(3600L) // 1 час
                .user(userDto)
                .build();
        
        log.info("Пользователь {} успешно аутентифицирован", request.getUsername());
        return ResponseEntity.ok(response);
    }
}
