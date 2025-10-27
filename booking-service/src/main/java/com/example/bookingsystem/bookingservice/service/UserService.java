package com.example.bookingsystem.bookingservice.service;

import com.example.bookingsystem.bookingservice.dto.UserAuthRequest;
import com.example.bookingsystem.bookingservice.dto.UserDto;
import com.example.bookingsystem.bookingservice.dto.UserRegistrationRequest;
import com.example.bookingsystem.bookingservice.entity.User;
import com.example.bookingsystem.bookingservice.repository.UserRepository;
import com.example.bookingsystem.bookingservice.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с пользователями
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Регистрация нового пользователя
     */
    @Transactional
    public UserDto registerUser(UserRegistrationRequest request) {
        log.info("Регистрация нового пользователя: {}", request.getUsername());
        
        // Проверяем, что пользователь с таким именем не существует
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Пользователь с именем " + request.getUsername() + " уже существует");
        }
        
        // Проверяем, что email не занят (если указан)
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Пользователь с email " + request.getEmail() + " уже существует");
        }
        
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован с ID: {}", savedUser.getId());
        
        return userMapper.toDto(savedUser);
    }

    /**
     * Аутентификация пользователя
     */
    public Optional<User> authenticateUser(UserAuthRequest request) {
        log.debug("Аутентификация пользователя: {}", request.getUsername());
        
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()));
    }

    /**
     * Получение пользователя по ID
     */
    public Optional<UserDto> getUserById(Long id) {
        log.debug("Получение пользователя по ID: {}", id);
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    /**
     * Получение пользователя по имени пользователя
     */
    public Optional<User> getUserByUsername(String username) {
        log.debug("Получение пользователя по имени: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Получение всех пользователей (только для администраторов)
     */
    public List<UserDto> getAllUsers() {
        log.debug("Получение списка всех пользователей");
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    /**
     * Обновление пользователя
     */
    @Transactional
    public Optional<UserDto> updateUser(Long id, UserRegistrationRequest request) {
        log.info("Обновление пользователя с ID: {}", id);
        
        return userRepository.findById(id)
                .map(user -> {
                    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                        if (userRepository.existsByEmail(request.getEmail())) {
                            throw new IllegalArgumentException("Email " + request.getEmail() + " уже используется");
                        }
                        user.setEmail(request.getEmail());
                    }
                    
                    if (request.getFirstName() != null) {
                        user.setFirstName(request.getFirstName());
                    }
                    if (request.getLastName() != null) {
                        user.setLastName(request.getLastName());
                    }
                    if (request.getPhoneNumber() != null) {
                        user.setPhoneNumber(request.getPhoneNumber());
                    }
                    if (request.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    
                    User updatedUser = userRepository.save(user);
                    log.info("Пользователь с ID {} успешно обновлен", id);
                    return userMapper.toDto(updatedUser);
                });
    }

    /**
     * Удаление пользователя
     */
    @Transactional
    public boolean deleteUser(Long id) {
        log.info("Удаление пользователя с ID: {}", id);
        
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("Пользователь с ID {} успешно удален", id);
            return true;
        }
        
        log.warn("Пользователь с ID {} не найден для удаления", id);
        return false;
    }

    /**
     * Проверка существования пользователя
     */
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }
}
