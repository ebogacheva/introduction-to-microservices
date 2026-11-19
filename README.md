# Introduction to Microservices

Practical task for the "Introduction to Microservices" course: two Spring Boot services that store MP3 files and their metadata.

## Services

| Service | Port | Database | What it does |
|---|---|---|---|
| resource-service | 8080 | resource-db (5432) | Stores MP3 files, extracts tags with Apache Tika and sends them to Song Service |
| song-service | 8081 | song-db (5433) | Stores song metadata (name, artist, album, duration, year) |

- Database tables: created by SQL scripts in `init-scripts/` (run inside the database containers on their first start).
- Settings for Docker Compose: in `.env` (committed on purpose: local development values only).

## Endpoints

**Resource Service**
- `POST /resources` uploads an MP3 (`Content-Type: audio/mpeg`)
- `GET /resources/{id}` downloads the MP3
- `DELETE /resources?id=1,2` deletes resources and their song metadata

**Song Service**
- `POST /songs` creates song metadata
- `GET /songs/{id}` returns song metadata
- `DELETE /songs?id=1,2` deletes song metadata

## Tech stack

Java 17, Spring Boot 4, Spring Data JPA, PostgreSQL, Apache Tika, Maven, Docker Compose.

## How to run

Requires Docker Desktop (and Java 17 for the local run).

### Option 1: everything in Docker

```
docker compose up -d --build
```

This builds both service images and starts all four containers. The services start after the databases report healthy.
Resource Service is available at `http://localhost:8080`, Song Service at `http://localhost:8081`.

Stop and remove everything (database data is not kept):

```
docker compose down
```

### Option 2: services locally, databases in Docker

If Option 1 is running, stop it first with `docker compose down`.

1. Start only the databases:
   ```
   docker compose up -d resource-db song-db
   ```
2. Start each service from its folder (or with the Run button in IntelliJ):
   ```
   cd song-service
   ./mvnw spring-boot:run
   ```
   ```
   cd resource-service
   ./mvnw spring-boot:run
   ```

`application.properties` reads environment variables when they are set (Docker)
or use local defaults. 

