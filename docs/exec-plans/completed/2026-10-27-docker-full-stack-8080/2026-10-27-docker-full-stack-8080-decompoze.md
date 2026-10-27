# Декомпозиция

1. [x] FEATURE + plan
2. [x] docker-compose: frontend :8080, backend internal, CORS/base URL на localhost:8080
3. [x] Frontend Dockerfile: HOSTNAME=0.0.0.0; build-arg API → http://backend:8080
4. [x] Backend Dockerfile: Temurin 17, healthcheck через java
5. [x] AGENTS/README/.env.example
6. [x] Прогон compose + health + smoke + unit
7. [x] DoD в FEATURE, progress, ranking, move plan completed
