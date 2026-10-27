# Лог прогресса

## Текущее проверенное состояние

- Корень репозитория: d:\apps\test
- Стандартный путь старта: `docker compose up --build -d`; страница http://localhost:8080. Dev без контейнеров приложения: postgres + bootRun + `pnpm dev` (:3000)
- Стандартный путь верификации: `curl http://localhost:8080/actuator/health` → UP + `./gradlew.bat test` + `pnpm test`; e2e — против dev-пути на :3000
- Текущая незавершённая фича с наивысшим приоритетом: нет (ID 2 готова)
- Текущий блокер: нет

## Лог сессий

### Сессия 001

- Дата: 2026-10-27
- Цель: стэк, ТЗ, exec plan
- Сделано: STACK/FEATURE/exec plan
- Запущенная верификация: кода не было
- Коммиты: не создавались

### Сессия 002

- Дата: 2026-10-27
- Цель: реализация MVP
- Сделано: backend+frontend+ЮKassa stub; API smoke; unit backend
- Блокер на конец: Playwright OOM на turbopack dev

### Сессия 003

- Дата: 2026-10-27
- Цель: закрыть DoD
- Сделано:
  - Frontend production build + `pnpm start`
  - Playwright e2e **1 passed (2.3s)**
  - `./gradlew.bat test` EXIT 0; `pnpm test` EXIT 0
  - health UP; FEATURE ID 1 → готово, шаблон очищен; exec plan в completed/
- Запущенная верификация:
  - health UP; backend tests OK; frontend vitest OK; playwright OK
- Зафиксированные доказательства: FEATURE лог завершённых; docs/exec-plans/completed/2026-10-27-ecommerce-phones-mvp/
- Коммиты: не создавались
- Известный риск: на low-RAM e2e гонять против production start, не turbopack; ЮKassa live не проверялась

### Сессия 004

- Дата: 2026-10-27
- Цель: полный Compose на Java 17, страница :8080
- Сделано: backend-образ Temurin 17; Gradle компилирует с `--release 17` без toolchain на JDK 19; `docker compose up --build -d` — postgres, backend, frontend healthy
- Запущенная верификация: health UP; каталог и логин на http://localhost:8080; `.\gradlew.bat test` и `pnpm test` зелёные; в контейнере Java 17.0.20
- Зафиксированные доказательства: FEATURE ID 2 → готово; exec plan в completed/
- Коммиты: полный Compose на Java 17 и страница :8080
- Известный риск: e2e Playwright по-прежнему против dev-пути :3000, не против Compose; шапка после логина обновляется при полной загрузке страницы
