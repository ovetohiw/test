## Описание проекта

- Стэк: STACK.md.
- MVP магазин телефонов: `frontend/` (Next.js) + `backend/` (Spring Boot) + PostgreSQL.

## Стартовый воркфлоу

- Прочитать файл PROGRESS.md;
- Прочитать файл PLANS.md;
- Прочитать файл FEATURE.md;
- Посмотреть последние коммиты: git log --oneline -5;

Если текущая базовая верификация падает то нужно чинить ее. Не выполнять работу при неисправном стартовом состоянии.

## Тест работоспособности приложения

- Поднять приложение: `docker compose up -d postgres`; backend `.\gradlew.bat bootRun` или `java -Xmx384m -jar build\libs\backend-0.0.1-SNAPSHOT.jar`; frontend для разработки `pnpm dev`, для e2e на weak RAM — `pnpm build && pnpm start`
- Прогнать тесты: backend `.\gradlew.bat test`; frontend `pnpm test`; e2e `pnpm test:e2e` (при поднятых app)
- Верификация: `curl http://localhost:8080/actuator/health` → UP; `pnpm test`, `.\gradlew.bat test`, `pnpm test:e2e` зелёные

## Defenition of Done

- Фича действительно реализована;
- Проверка фичи сквозными тестами;
- Отсутствие ошибок;
- helth-check;
- Доказательство зафиксированно в FEATURE.md.

## Рабочие правила

- Работать над одной фичей за раз;
- Не помечать фичу сделанной если написан только код;
- Папка с референсами для дизайна - refs. Допускается, что папка может быть пустая.
- Соблюдать docs/convesion/SECURITY.md.
- Соблюдать docs/convesion/TEST.md.
- Соблюдать docs/convesion/DEVELOPMENT.md.
- Соблюдать docs/convesion/LOGGING.md.
- Соблюдать docs/convesion/NETWORK.md.
- Соблюдать docs/convesion/DB.md.
- Соблюдать docs/convesion/ARCHITECTURE.md.
- Соблюдать docs/convesion/UIDESIGN.md.

## Учётки seed (только local)

- admin@phoneshop.local / Password123! (ADMIN)
- buyer@phoneshop.local / Password123! (USER)

## Окончание сессии

- Обнови PROGRESS.md;
- Обнови FEATURE.md;
- Обнови файлы в docs/exec-plans;
- Обнови файлы в ranking;
- Опиши все риски и нерешенные вопросы;
- Закоммить все измененения;
- Сохрани чистоту репозитория чтобы следующая сессия могла работать;
- Если изменился стэк обнови STACK.md;
- Обнови README.md.
