# Декомпозиция: MVP интернет-магазина телефонов

Один текущий шаг за раз. Отмечать `[x]` только после проверки поведения шага.

## Шаги

### 1. Каркас репозитория

- [x] `backend/`: Spring Boot + Gradle, actuator/health
- [x] `frontend/`: Next.js + TypeScript + Tailwind + pnpm
- [x] `docker-compose.yml`: PostgreSQL (+ сервисы app)
- [x] `.env.example` без секретов
- [x] Команды подъёма/тестов/верификации в AGENTS.md
- Проверка: backend health UP; frontend 200; Postgres через Compose

### 2. Домен и схема БД

- [x] Сущности: User, Product, CartItem, Order/OrderItem, Payment
- [x] Миграции Flyway; роли USER / ADMIN
- [x] Сид админа/buyer через DataInitializer; 2 телефона в V2
- Проверка: миграции на чистой БД при старте

### 3. Auth (JWT в httpOnly cookie)

- [x] Регистрация, вход, выход
- [x] Spring Security: защита маршрутов; роль ADMIN на сервере
- [x] Frontend: формы входа/регистрации, cookie-сессия через proxy rewrite
- Проверка: anon → 401 на /api/cart; login → cookie; /api/auth/me 200

### 4. Каталог

- [x] API: список с фильтром бренд/цена, карточка товара
- [x] UI: список, фильтры, страница товара, фото с локальной ФС
- Проверка: GET /api/products отдаёт seed-товары

### 5. Корзина

- [x] API: add / update qty / remove для авторизованного пользователя
- [x] UI корзины
- Проверка: POST /api/cart/items после login

### 6. Заказы

- [x] API: создать заказ из корзины (пересчёт на сервере), контакты, способ получения
- [x] UI оформления и страница статуса заказа
- Проверка: заказ PENDING_PAYMENT с серверной суммой

### 7. Оплата ЮKassa

- [x] Создание платежа (live клиент + stub при `APP_YOOKASSA_MODE=stub`)
- [x] Webhook handler + stub checkout; идемпотентность succeeded
- [x] Отказ/отмена → CANCELLED, не PAID
- [x] E2e решение: **stub-адаптер** (закрыто)
- Проверка: stub success → order PAID; unit PaymentServiceTest

### 8. Админка

- [x] CRUD товаров (active toggle), загрузка фото
- [x] Список и детали заказов
- [x] UI /admin; API только ADMIN
- Проверка: admin создаёт товар; buyer на admin получает отказ

### 9. Сквозная приёмка и DoD

- [x] Playwright: `1 passed (2.3s)` против production `pnpm start` + stub ЮKassa
- [x] Health-check зелёный
- [x] API smoke: catalog → login → cart → order → stub pay → PAID
- [x] Backend `./gradlew test` зелёный
- [x] FEATURE статус `готово`, доказательства заполнены
