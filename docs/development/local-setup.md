# Local development environment

## Requirements

- Java 25;
- Docker and Docker Compose;
- Git.

Maven does not need to be installed globally because the backend includes the
Maven Wrapper.

## Environment variables

Copy the example file from the repository root:

```bash
cp .env.example .env
```

Variables supported by the backend:

| Variable | Default | Purpose |
| --- | --- | --- |
| `SERVER_PORT` | `8080` | API HTTP port |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/find-collectors` | JDBC URL |
| `POSTGRES_USER` | `postgres` | Database user |
| `POSTGRES_PASSWORD` | `postgres` | Database password |
| `POSTGRES_DB` | `find-collectors` | Database created by Compose |
| `DB_PORT` | `5432` | Port published by Compose |

If `DB_PORT` changes, update `DATABASE_URL` accordingly.

## Startup

From the repository root:

```bash
docker compose up -d database
```

From `find-collectors/`:

```bash
./mvnw clean install
./mvnw -pl api spring-boot:run
```

Flyway runs migrations automatically when the application starts. Hibernate
only validates the resulting schema (`ddl-auto=validate`) and must not modify
it.

## Verification

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/v3/api-docs/find-collectors-api
```

Run the complete Maven verification with:

```bash
./mvnw clean verify
```

The current context test requires a PostgreSQL instance accessible through the
application properties.

## Migrations during development

Migrations that have reached shared environments must not be edited. Add a new
incremental migration instead. During the initial consolidation, `V1` was
corrected before a shared baseline existed.

If a local database already received an older `V1`, Flyway may report a checksum
mismatch. Recreating the volume is acceptable only when its local data can be
discarded:

```bash
docker compose down -v
docker compose up -d database
```

The `-v` option permanently removes the local database data.

## Shutdown

```bash
docker compose down
```

This command preserves the PostgreSQL volume.
