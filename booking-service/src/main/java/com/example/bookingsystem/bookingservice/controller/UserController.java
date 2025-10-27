package com.example.bookingsystem.bookingservice.controller;

import com.example.bookingsystem.bookingservice.dto.UserDto;
import com.example.bookingsystem.bookingservice.dto.UserRegistrationRequest;
import com.example.bookingsystem.bookingservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для управления пользователями
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Создание пользователя (только для администраторов)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Создание пользователя администратором: {}", request.getUsername());
        UserDto user = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    /**
     * Получение пользователя по ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        log.debug("Получение пользователя по ID: {}", id);
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Получение всех пользователей (только для администраторов)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Обновление пользователя (только для администраторов)
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, 
                                             @Valid @RequestBody UserRegistrationRequest request) {
        log.info("Обновление пользователя с ID: {}", id);
        return userService.updateUser(id, request)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Удаление пользователя (только для администраторов)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Удаление пользователя с ID: {}", id);
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
