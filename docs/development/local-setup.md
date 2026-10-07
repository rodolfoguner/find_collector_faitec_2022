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
| `JWT_SECRET` | none | Secret used to sign access and refresh tokens; at least 32 bytes |

If `DB_PORT` changes, update `DATABASE_URL` accordingly.

Generate a local JWT secret before starting the API and expose it to the Java
process. For Fish:

```fish
set -gx JWT_SECRET (openssl rand -base64 48)
```

Use an independently generated secret in each deployed environment. Never
commit its value.

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

For interactive API tests, import the [Postman collection](../postman/README.md).
Fill in `loginEmail` and `loginPassword` in the collection variables; protected
requests inherit Bearer authentication and automatically log in or refresh
tokens as needed.

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

## Session authentication and verification

No Redis service is required. Migration `V4` creates authentication-session and
refresh-hash tables in PostgreSQL. Existing account and collection data is
preserved. Previously issued JWTs require a new login after deployment.

Each login has a separate session. Use `POST /api/logout` with the current
access token in `Authorization: Bearer <token>` to revoke that session. Refresh
is single-use: concurrent or repeated refresh calls can revoke the session, so
clients must coordinate renewal. Sessions last at most 7 days; refreshing does
not extend this deadline.

Expired sessions and refresh history are deleted hourly. To change the interval,
set the Spring property `security.sessions.cleanup-interval`, for example `PT6H`.
Validation does not depend on cleanup having run.

Run authentication and migration integration tests against a dedicated test
PostgreSQL database (credentials use the same variables as the API):

```bash
RUN_POSTGRES_TESTS=true mvn test
```

Run this command from `find-collectors/` with Java 25. These tests create and
remove isolated schemas for sessions and migration checks. The migration
tests cover both an empty schema and upgrading `V3` with existing account data.
The configured database user needs permission to create schemas. Without
`RUN_POSTGRES_TESTS=true`, these additional database tests are skipped; the
existing application-context test still requires PostgreSQL.
