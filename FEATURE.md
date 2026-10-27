# FEATURE.md

Этот файл — форма ТЗ на одну активную фичу и место, куда фиксируют доказательство готовности.
Правила стек-агностичны: они действуют до выбора стэка и после него.
Команды запуска и проверки берите из AGENTS.md, когда они заполнены. Крупную работу ведите по PLANS.md, здесь только ссылка на план.

Нарушение — вторая активная фича в этом файле, статус «готово» без доказательств или ТЗ, которое описывает внутреннюю реализацию вместо наблюдаемого поведения.

## Как заполнять

- Вверху всегда одна активная фича. Новую не начинайте, пока текущая не в статусе «готово» или явно не снята.
- Сначала заполните ТЗ (мета, цель, скоуп, поведение, критерии, верификация). Потом пишите код. Пустые критерии приёмки означают, что фичу ещё нельзя брать в работу.
- Статус: `черновик` → `в работе` → `на проверке` → `готово`. «Готово» ставьте только когда все критерии приёмки отмечены и блок «Доказательства DoD» заполнен. Код без прогона доказательством не является.
- Не дублируйте exec plan. Если по PLANS.md план обязателен, укажите путь в `docs/exec-plans/inprogress/` и держите там скоуп, верификацию и лог. В FEATURE.md — контракт фичи и доказательства.
- Риски и блокеры дублируйте в PROGRESS.md до конца сессии. Молчаливый «потом» запрещён.
- Перед следующей фичей перенесите готовую в «Лог завершённых» одной–тремя строками и очистите шаблон. Не раздувайте активное ТЗ историей.
- Не записывайте секреты, токены и реальные персональные данные.

## Текущая фича

### Мета

- ID: 2
- Название: Полный подъём в Docker, страница на :8080
- Приоритет: высокий
- Статус: готово
- Дата: 2026-10-27
- Exec plan: docs/exec-plans/completed/2026-10-27-docker-full-stack-8080/

### Проблема и цель

- Проблема: сейчас в Compose поднимается в основном Postgres; UI на :3000, API на :8080; полный стек через один `docker compose up` не является основным путём.
- Цель: `docker compose up --build` поднимает postgres + backend + frontend; пользователь открывает магазин на http://localhost:8080.
- Что должно измениться: публичный порт страницы — 8080; API доступен браузеру через same-origin proxy фронта; health через :8080.

### Скоуп

В скоупе:

- docker-compose.yml (порты, env, depends_on/health)
- Dockerfiles frontend/backend при необходимости
- CORS / base URL под :8080 в Compose
- AGENTS.md / README под новый путь старта

Вне скоупа:

- Смена платёжного провайдера
- Kubernetes / cloud deploy
- HTTPS / TLS termination

### Поведение и контракт

Наблюдаемое поведение: вход, выход, отказ. Без привязки к внутренней реализации.

- Счастливый путь: `docker compose up --build -d` → http://localhost:8080 отдаёт UI; `/actuator/health` через тот же origin → UP; каталог и логин работают.
- Ошибки и отказы: если backend не готов, health/proxy не зелёные; compose не помечает frontend healthy раньше зависимостей.
- Границы и контракт: публичный порт приложения для пользователя — 8080; локальный dev-путь (pnpm/bootRun) может оставаться на 3000+8080.

### Критерии приёмки

Фича «готово» только когда каждый пункт отмечен и есть доказательства ниже.

- [x] `docker compose up --build -d` поднимает postgres, backend, frontend без ручного bootRun/pnpm
- [x] http://localhost:8080 открывает страницу магазина
- [x] `curl http://localhost:8080/actuator/health` → UP
- [x] Базовая верификация (unit backend/frontend) зелёная; smoke UI/API через :8080

### Верификация

Отметьте обязательные уровни. Команды — из AGENTS.md, не выдумывайте раннер.

- [x] Unit
- [x] Интеграция
- [x] Сквозной путь
- [x] Health-check
- [x] Базовая верификация из AGENTS.md зелёная

Команды:

- Поднять приложение: `docker compose up --build -d`
- Прогнать тесты: `cd backend && .\gradlew.bat test`; `cd frontend && pnpm test`
- Верификация: `curl http://localhost:8080/actuator/health`; открыть UI на :8080; smoke API через same-origin

### Ограничения

Соблюдайте конвенции, которые фича задевает. Этот список — напоминание, не копия правил.

- docs/convesion/SECURITY.md
- docs/convesion/TEST.md
- docs/convesion/DEVELOPMENT.md
- docs/convesion/LOGGING.md
- docs/convesion/NETWORK.md
- docs/convesion/DB.md
- docs/convesion/ARCHITECTURE.md
- docs/convesion/UIDESIGN.md
- STACK.md — не добавляйте технологию, которой нет в стэке

### Риски и открытые решения

- На хосте уже могут быть заняты :8080/:3000 локальным jar/dev — перед Compose их нужно остановить.
- Next rewrite к backend идёт по внутреннему DNS `backend:8080`; публичный backend-порт на хост не обязателен.

### Доказательства DoD

Пустой блок значит, что фича не готова. Definition of Done — в AGENTS.md.

- Фича реализована: `docker compose up --build -d` поднимает postgres, backend (Temurin 17) и frontend; UI на http://localhost:8080
- Сквозные тесты: браузер — главная, каталог (Pulse Pro, Nova X1), логин `buyer@phoneshop.local` → шапка «Buyer» / «Заказы»; `POST /api/auth/login` и `GET /api/auth/me` через :8080
- Ошибки: нет на прогоне 2026-10-27
- Health-check: три сервиса healthy; `curl http://localhost:8080/actuator/health` → `{"status":"UP"}`
- Что прогнано и результат: `.\gradlew.bat test` BUILD SUCCESSFUL; `pnpm test` 1 passed; `GET /api/products` отдаёт каталог; в контейнере `java -version` → 17.0.20
- Где лежит артефакт: docs/exec-plans/completed/2026-10-27-docker-full-stack-8080/

## Лог завершённых

Краткая запись прошлых фич. Активное ТЗ живёт только в секции «Текущая фича».

- ID 1 MVP интернет-магазина телефонов — готово 2026-10-27. Playwright e2e catalog→order→stub ЮKassa passed; health UP; unit backend/frontend green. План: docs/exec-plans/completed/2026-10-27-ecommerce-phones-mvp/
- ID 2 Полный Docker, страница на :8080 — готово 2026-10-27. Compose healthy, backend Temurin 17, health UP, каталог и логин на :8080, unit green. План: docs/exec-plans/completed/2026-10-27-docker-full-stack-8080/
