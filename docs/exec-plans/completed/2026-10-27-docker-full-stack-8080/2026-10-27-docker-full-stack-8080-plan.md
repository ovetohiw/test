# План: полный Docker Compose, страница на :8080

## Цель

Поднять весь MVP одной командой Compose так, чтобы пользователь открывал магазин на http://localhost:8080.

## Скоуп и out-of-scope

В скоупе: compose, Dockerfiles, env CORS/base URL, документация старта.

Вне скоупа: TLS, K8s, live ЮKassa.

## Путь верификации

1. Остановить локальные процессы на :8080/:3000 при конфликте.
2. `docker compose up --build -d`
3. `curl http://localhost:8080/actuator/health` → UP
4. Smoke: UI главная/каталог; `curl` `/api/products` через :8080
5. `.\gradlew.bat test`, `pnpm test`

## Риски и блокеры

- Конфликт портов с уже запущенным jar/dev.
- Next должен слушать 0.0.0.0 внутри контейнера.
- Rewrite destination — внутренний `http://backend:8080`.

## Лог прогресса

- 2026-10-27: образ backend на eclipse-temurin:17; сборка `bootJar` внутри JDK 17; Gradle `--release 17` без toolchain (на хосте JDK 19, в контейнере JDK 17).
- `docker compose up --build -d`: postgres, backend, frontend healthy. Публичный порт только `8080:3000`.
- `java -version` в backend: OpenJDK Temurin 17.0.20.1.
- `curl http://localhost:8080/actuator/health` → UP. `GET /api/products` — каталог. Логин buyer через UI и API.
- `.\gradlew.bat test` BUILD SUCCESSFUL; `pnpm test` 1 passed.
- План закрыт и перенесён в completed/.

## Открытые решения

- Backend на хост не публикуем (только внутренняя сеть); отладка API — через proxy :8080 или временный publish.
