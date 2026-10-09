# Introduction to Microservices

Practical task for the "Introduction to Microservices" course: two Spring Boot services that store MP3 files and their metadata.

## Services

| Service | Port | Database | What it does |
|---|---|---|---|
| resource-service | 8080 | resource-db (5432) | Stores MP3 files, extracts tags with Apache Tika and sends them to Song Service |
| song-service | 8081 | song-db (5433) | Stores song metadata (name, artist, album, duration, year) |

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

1. Start the databases (Docker Compose runs only the two PostgreSQL 17 containers; the services run locally, not in Docker):
   ```
   docker compose up -d
   ```
2. Start each service from its folder:
   ```
   cd song-service
   ./mvnw spring-boot:run
   ```
   ```
   cd resource-service
   ./mvnw spring-boot:run
   ```
