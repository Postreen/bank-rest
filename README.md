# Bank REST

Backend сервис управления банковскими картами на Spring Boot 3 / Java 17.

## Стек
- Spring Boot, Spring Security, JWT
- Spring Data JPA
- PostgreSQL
- Liquibase
- OpenAPI/Swagger

## Основные возможности
- Аутентификация: `POST /auth/login`
- ADMIN:
    - Управление пользователями: `/admin/users/**`
    - Управление картами: `/admin/cards/**`
- USER:
    - Просмотр своих карт: `GET /cards` (+пагинация/фильтры)
    - Просмотр карты/баланса: `GET /cards/{id}`, `GET /cards/{id}/balance`
    - Запрос блокировки карты: `POST /cards/{id}/block-request`
    - Перевод между своими картами: `POST /transfers`

## Безопасность
- JWT Bearer токены
- Роли `ADMIN` и `USER`
- Номер карты хранится в БД в зашифрованном виде (AES/GCM)
- В API номер карты всегда возвращается только в маске: `**** **** **** 1234`

## Переменные окружения
Обязательные:
- `JWT_SECRET` - минимум 32 символа
- `CARD_CRYPTO_KEY` - ровно 32 символа

Для профиля `dev`:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Быстрый запуск (Docker Compose)
```bash
  docker compose up --build
```

Сервис:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Локальный запуск
1. Поднимите PostgreSQL.
2. Экспортируйте переменные окружения.
3. Запустите приложение:
```bash
  ./gradlew bootRun --args="--spring.profiles.active=dev"
```

## Тесты

Запустить все тесты

```bash
  ./gradlew test
```

## Миграции
- Master changelog: `src/main/resources/db/changelog/db.changelog-master.yaml`
- Миграции: `src/main/resources/db/migration`

## OpenAPI
Файл спецификации: `docs/openapi.yaml`.