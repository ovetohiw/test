# PulsePhone

MVP интернет-магазина телефонов.

## Стек

См. [STACK.md](STACK.md): Next.js + Spring Boot + PostgreSQL + Gradle + ЮKassa (stub/live).

## Быстрый старт

Полный стек: `docker compose up --build -d`. Магазин: http://localhost:8080. Health: `curl http://localhost:8080/actuator/health`.

Локальная разработка без контейнеров приложения (не вместе с полным Compose — оба занимают :8080):

1. Поднять БД: `docker compose up -d postgres`
2. Backend: `cd backend && ./gradlew.bat bootRun` (API: http://localhost:8080)
3. Frontend: `cd frontend && pnpm install && pnpm dev` (http://localhost:3000)

Seed-пользователи (local): `admin@phoneshop.local` / `Password123!`, `buyer@phoneshop.local` / `Password123!`.

ЮKassa по умолчанию в режиме `APP_YOOKASSA_MODE=stub`. Для live: `APP_YOOKASSA_MODE=live` и ключи из `.env.example`.

## Тесты

- Backend: `cd backend && ./gradlew.bat test`
- Frontend unit: `cd frontend && pnpm test`
- E2E: при поднятых сервисах `cd frontend && pnpm exec playwright install chromium && pnpm test:e2e`

## Документы агента

- [FEATURE.md](FEATURE.md), [PROGRESS.md](PROGRESS.md), [AGENTS.md](AGENTS.md)
- Exec plan: [docs/exec-plans/completed/2026-10-27-docker-full-stack-8080/](docs/exec-plans/completed/2026-10-27-docker-full-stack-8080/)
