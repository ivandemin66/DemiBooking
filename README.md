# Hotel Booking System

Микросервисная система бронирования отелей, построенная на Spring Boot и Spring Cloud.

## Архитектура

Проект использует микросервисную архитектуру с следующими компонентами:

- **Eureka Server** - сервер регистрации и обнаружения сервисов
- **API Gateway** - единая точка входа для всех запросов
- **Hotel Service** - управление отелями и их информацией
- **Booking Service** - управление бронированиями

## Технологический стек

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Data JPA**
- **Spring Security**
- **Eureka Server**
- **Spring Cloud Gateway**
- **OpenFeign**
- **H2 Database** (для разработки)
- **PostgreSQL** (для продакшена)
- **JWT** для аутентификации

## Структура проекта

```
hotel-booking-system/
├── .gitignore
├── pom.xml                 <-- Родительский POM-файл
├── README.md
├── api-gateway/            <-- Модуль API Gateway
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java/com/example/bookingsystem/gateway
│           │   └── ApiGatewayApplication.java
│           └── resources
│               └── application.yml
├── booking-service/        <-- Модуль сервиса бронирования
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java/com/example/bookingsystem/bookingservice
│           │   ├── BookingServiceApplication.java
│           │   ├── client/
│           │   ├── config/
│           │   ├── controller/
│           │   ├── dto/
│           │   ├── entity/
│           │   ├── exception/
│           │   ├── repository/
│           │   ├── security/
│           │   └── service/
│           └── resources
│               └── application.yml
├── eureka-server/          <-- Модуль Eureka Server
│   ├── pom.xml
│   └── src
│       └── main
│           ├── java/com/example/bookingsystem/eureka
│           │   └── EurekaServerApplication.java
│           └── resources
│               └── application.yml
└── hotel-service/          <-- Модуль управления отелями
    ├── pom.xml
    └── src
        └── main
            ├── java/com/example/bookingsystem/hotelservice
            │   ├── HotelServiceApplication.java
            │   ├── config/
            │   ├── controller/
            │   ├── dto/
            │   ├── entity/
            │   ├── repository/
            │   ├── service/
            │   └── util/
            └── resources
                └── application.yml
```

## Запуск проекта

### Предварительные требования

- Java 17 или выше
- Maven 3.6 или выше

### Порядок запуска сервисов

1. **Eureka Server** (порт 8761)
```bash
cd eureka-server
mvn spring-boot:run
```

2. **Hotel Service** (порт 8081)
```bash
cd hotel-service
mvn spring-boot:run
```

3. **Booking Service** (порт 8082)
```bash
cd booking-service
mvn spring-boot:run
```

4. **API Gateway** (порт 8080)
```bash
cd api-gateway
mvn spring-boot:run
```

### Доступ к сервисам

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Hotel Service H2 Console**: http://localhost:8081/h2-console
- **Booking Service H2 Console**: http://localhost:8082/h2-console

## API Endpoints

### Через API Gateway (порт 8080)

#### Аутентификация и пользователи
- `POST /user/register` - Регистрация пользователя
- `POST /user/auth` - Аутентификация пользователя
- `GET /user` - Получение всех пользователей (ADMIN)
- `POST /user` - Создание пользователя (ADMIN)
- `PATCH /user/{id}` - Обновление пользователя (ADMIN)
- `DELETE /user/{id}` - Удаление пользователя (ADMIN)

#### Отели
- `GET /api/hotels` - Получение всех отелей
- `GET /api/hotels/{id}` - Получение отеля по ID
- `GET /api/hotels/search?name={name}` - Поиск отелей по названию
- `GET /api/hotels/search/address?address={address}` - Поиск отелей по адресу
- `GET /api/hotels/search/rating?minRating={rating}` - Поиск отелей по рейтингу
- `POST /api/hotels` - Создание отеля (ADMIN)
- `PUT /api/hotels/{id}` - Обновление отеля (ADMIN)
- `DELETE /api/hotels/{id}` - Удаление отеля (ADMIN)

#### Номера
- `GET /api/rooms` - Получение всех доступных номеров
- `GET /api/rooms/{id}` - Получение номера по ID
- `GET /api/rooms/hotel/{hotelId}` - Получение номеров отеля
- `GET /api/rooms/hotel/{hotelId}/available` - Получение доступных номеров отеля
- `GET /api/rooms/recommend?hotelId={id}` - Получение рекомендованных номеров
- `GET /api/rooms/search/type?roomType={type}` - Поиск номеров по типу
- `GET /api/rooms/search/capacity?capacity={capacity}` - Поиск номеров по вместимости
- `GET /api/rooms/search/price?minPrice={min}&maxPrice={max}` - Поиск номеров по цене
- `GET /api/rooms/statistics/{hotelId}` - Статистика загруженности (ADMIN)
- `POST /api/rooms` - Создание номера (ADMIN)

#### Бронирования
- `POST /booking` - Создание бронирования (USER)
- `GET /booking/{id}` - Получение бронирования по ID (USER)
- `GET /booking` - История бронирований пользователя (USER)
- `GET /booking/page` - История бронирований с пагинацией (USER)
- `DELETE /booking/{id}` - Отмена бронирования (USER)
- `GET /booking/admin/all` - Все бронирования (ADMIN)
- `GET /booking/admin/status/{status}` - Бронирования по статусу (ADMIN)
- `POST /booking/admin/cleanup` - Очистка старых бронирований (ADMIN)

## Тестовые данные

### Пользователи
- **Администратор**: `admin` / `admin123`
- **Пользователь 1**: `user` / `user123`
- **Пользователь 2**: `testuser` / `test123`

### Отели
- **Гранд Отель Москва** - роскошный отель в центре Москвы
- **Отель Санкт-Петербург** - элегантный отель на Невском проспекте
- **Бизнес Отель Екатеринбург** - современный бизнес-отель

### Номера
Каждый отель содержит различные типы номеров:
- Люкс (2-местные, 15000₽/ночь)
- Стандарт (1-2-местные, 3000-8000₽/ночь)
- Семейные (4-местные, 12000₽/ночь)
- Бизнес (2-местные, 10000₽/ночь)
- Эконом (1-местные, 3000₽/ночь)

## Принципы разработки

Проект следует принципам:

- **SOLID** - принципы объектно-ориентированного программирования
- **Clean Architecture** - чистая архитектура с разделением слоев
- **DRY** - Don't Repeat Yourself
- **YAGNI** - You Aren't Gonna Need It
- **Паттерны проектирования** - использование проверенных решений

## Безопасность

- JWT токены для аутентификации
- Spring Security для авторизации
- Валидация входных данных
- Обработка исключений

## Мониторинг

- Spring Boot Actuator для health checks
- Eureka для регистрации сервисов
- Логирование на уровне DEBUG для разработки

## Примеры использования API

### 1. Регистрация и аутентификация

```bash
# Регистрация нового пользователя
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@example.com",
    "firstName": "Новый",
    "lastName": "Пользователь"
  }'

# Аутентификация
curl -X POST http://localhost:8080/user/auth \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user",
    "password": "user123"
  }'
```

### 2. Работа с отелями

```bash
# Получение всех отелей
curl -X GET http://localhost:8080/api/hotels

# Поиск отелей по названию
curl -X GET "http://localhost:8080/api/hotels/search?name=Москва"

# Получение отеля по ID
curl -X GET http://localhost:8080/api/hotels/1
```

### 3. Работа с номерами

```bash
# Получение всех доступных номеров
curl -X GET http://localhost:8080/api/rooms

# Получение рекомендованных номеров для отеля
curl -X GET "http://localhost:8080/api/rooms/recommend?hotelId=1"

# Поиск номеров по типу
curl -X GET "http://localhost:8080/api/rooms/search/type?roomType=Люкс"
```

### 4. Создание бронирования

```bash
# Создание бронирования (требует JWT токен)
curl -X POST http://localhost:8080/booking \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "roomId": 1,
    "startDate": "2024-12-01",
    "endDate": "2024-12-03",
    "guestCount": 2,
    "specialRequests": "Номер с видом на город",
    "autoSelect": false
  }'
```

### 5. Получение истории бронирований

```bash
# История бронирований пользователя
curl -X GET http://localhost:8080/booking \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Архитектурные решения

### Саги с компенсацией
Система использует паттерн "Саги" для обеспечения согласованности данных между сервисами:

1. **Booking Service** создает бронирование в статусе `PENDING`
2. Вызывается **Hotel Service** для подтверждения доступности номера
3. При успехе: бронирование переводится в статус `CONFIRMED`
4. При ошибке: выполняется компенсация - бронирование отменяется

### Алгоритм планирования занятости
Hotel Service ведет статистику бронирований (`times_booked`) для каждого номера:
- Рекомендуемые номера сортируются по возрастанию `times_booked`
- Это обеспечивает равномерную загрузку номеров
- Предотвращает "простой" популярных номеров

### Безопасность
- JWT токены с ролевой моделью (USER/ADMIN)
- Каждый сервис проверяет токены независимо
- API Gateway передает токены в backend сервисы
- Валидация входных данных на всех уровнях

### Тестирование
- Unit тесты для контроллеров и сервисов
- Интеграционные тесты с H2 in-memory базой
- Тестовые профили с отключенным Eureka
- Моки для межсервисного взаимодействия
