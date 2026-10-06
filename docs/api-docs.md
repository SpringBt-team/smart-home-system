# Документація API (Swagger UI / OpenAPI)

Документація генерується бібліотекою SpringDoc з коду контролерів і DTO (підхід Code-First), тому завжди збігається з реальним API.

## Де дивитися

Запустіть сервер (`./gradlew :server:bootRun`) і відкрийте:

| Адреса | Що це |
|---|---|
| `http://localhost:8080/swagger-ui/index.html` | Інтерактивна документація (Swagger UI) |
| `http://localhost:8080/v3/api-docs` | Специфікація OpenAPI у JSON |

Ендпоінти згруповані за тегами: «Користувачі», «Автентифікація», «Пристрої», «Доступи до пристроїв», «Команди пристрою», «Виконання команд». Внизу сторінки розділ **Schemas** містить усі моделі з прикладами значень.

## Як виконати запит у Swagger UI

1. Оберіть ендпоінт, натисніть **Try it out**.
2. Тіло запиту вже заповнене прикладом із `@Schema(example = ...)`. Змініть за потреби.
3. Для `POST /devices` і `POST /devices/{deviceId}/commands/{commandId}/executions` заповніть заголовок `X-User-Id` (UUID користувача, наприклад з відповіді `POST /users`). Це тимчасова ідентифікація до впровадження Security.
4. Натисніть **Execute**: нижче видно статус, тіло й заголовки відповіді.

Помилки повертаються у форматі `ProblemDetail` (RFC 9457): поля `status`, `title`, `detail`, а при помилках валідації ще й `errors`.

## Спільні приклади даних

Ті самі значення використовує колекція Postman, щоб документація й автотести казали одне й те саме.

| Дані | Значення |
|---|---|
| email | `owner@example.com` |
| password | `Passw0rd123` |
| ім'я користувача | `Власник` |
| пристрій | `Лампа у вітальні`, тип `LAMP` |
| команда | `turn_on`, `requiredRole` = `GUEST` |
| argsSchema | `{"type":"object","additionalProperties":false}` |
| args виконання | `{}` |

## Як документувати новий ендпоінт

Усе робиться анотаціями, поведінка коду не змінюється.

1. **Контролер**: `@Tag(name, description)` на класі.
2. **Метод**: `@Operation(summary, description)` українською, `@ApiResponse` для успішного коду (`201`, `202`, `204` тощо; `200` визначається автоматично, але краще вказувати явно).
3. **Помилки**: готові анотації з пакета `server.openapi`. Кожна додає відповідь у форматі `ProblemDetail`:

   | Анотація | Код |
   |---|---|
   | `@BadRequestResponse` | 400 |
   | `@UnauthorizedResponse` | 401 |
   | `@NotFoundResponse` | 404 |
   | `@ConflictResponse` | 409 |
   | `@UnprocessableResponse` | 422 |

   Документуйте лише ті коди, які метод **реально** повертає (дивіться `GlobalExceptionHandler` і тести контролера).
4. **Параметри**: query-параметри й заголовки описуються методною `@Parameter(name = "...", description = "...", example = "...")`.
5. **DTO**: `@Schema(description, example)` на record і на кожному полі. Обмеження Jakarta Validation (`@NotBlank`, `@Size`, ...) потрапляють у схему автоматично. Для enum перелічіть значення в `description`.
6. Не повертайте `@Entity` з контролера, не використовуйте `Map<String, Object>` у відповіді без опису і не повертайте `200` при помилці.

Тест `OpenApiDocsTest` перевіряє, що кожен ендпоінт має `summary`, `description` і тег, а помилки описані схемою `ProblemDetail`. Додаючи ендпоінт, оновіть там `DOCUMENTED_OPERATIONS`.
