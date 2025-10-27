package com.example.bookingsystem.bookingservice.repository;

import com.example.bookingsystem.bookingservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по имени пользователя
     */
    Optional<User> findByUsername(String username);

    /**
     * Поиск пользователя по email
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверка существования пользователя по имени пользователя
     */
    boolean existsByUsername(String username);

    /**
     * Проверка существования пользователя по email
     */
    boolean existsByEmail(String email);
}
