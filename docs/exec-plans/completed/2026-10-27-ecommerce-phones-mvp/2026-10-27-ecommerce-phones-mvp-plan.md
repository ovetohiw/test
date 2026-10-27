# Exec plan: MVP интернет-магазина телефонов

Связь: FEATURE.md ID 1. Стэк: STACK.md.

## Цель

Собрать монолитный MVP: покупатель проходит каталог → корзина → заказ → оплата ЮKassa; админ ведёт товары и видит заказы; health-check и сквозной Playwright зелёные.

## Скоуп

В скоупе — как в FEATURE.md (каталог, корзина, auth, заказ, ЮKassa тест + webhook, статус заказа, админ CRUD товаров и просмотр заказов, health-check).

Вне скоупа — как в FEATURE.md (мобильные приложения, другие платёжки, трекинг курьеров, i18n/мультивалюта, рекомендации, очереди, микросервисы).

## Топология

Монолит как согласованный набор процессов:

- `frontend/` — Next.js (TypeScript, Tailwind, shadcn/ui), pnpm
- `backend/` — Spring Boot (Java), Gradle, Hibernate, REST, OpenAPI
- PostgreSQL + загрузка фото на локальную ФС
- Docker Compose для локального подъёма связки

## Путь верификации

Пока скриптов нет в AGENTS.md — после шага каркаса заполнить команды там и в FEATURE.md.

Ожидаемый контур:

1. `docker compose up` (или эквивалент из AGENTS.md) — Postgres + backend + frontend
2. Backend unit/integration: Gradle + JUnit
3. Frontend component: Testing Library
4. Health-check HTTP успешен
5. Playwright: каталог → заказ → успешная оплата ЮKassa (тест)

Критерии приёмки — чеклист в FEATURE.md. DoD только с доказательствами там же.

## Риски и блокеры

- ЮKassa: ключи только в env; для CI/сквозных тестов — тест-магазин или контролируемый double за границей адаптера (боевые ключи не в git)
- Локальные фото не масштабируются на несколько инстансов — ок для MVP
- Объём большой — идём строго по декомпозиции, один текущий шаг

## Открытые решения

| Решение | Статус | Выбор |
|--------|--------|--------|
| Хранение JWT на клиенте | **закрыто** | httpOnly Secure cookie (SameSite=Lax); не localStorage |
| Структура репозитория | **закрыто** | `frontend/` + `backend/` + `docker-compose.yml` в корне |
| Миграции БД | **закрыто** | Flyway в Spring Boot (рядом с Hibernate) |
| ЮKassa в e2e без реального мерчанта | **закрыто** | `APP_YOOKASSA_MODE=stub` + StubYooKassaClient; live — `APP_YOOKASSA_MODE=live` + ключи env |

## Текущий шаг

Завершён. План в `docs/exec-plans/completed/`.

## Лог прогресса

- 2026-10-27: создан exec plan и декомпозиция; FEATURE и PROGRESS связаны.
- 2026-10-27: шаги 1–8 реализованы; stub ЮKassa; health UP; `./gradlew test` OK.
- 2026-10-27: Playwright зелёный на production frontend; FEATURE → готово; план архивирован.
