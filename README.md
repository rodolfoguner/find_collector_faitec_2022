# Find Collectors

Find Collectors is a platform that connects people who have recyclable
materials with waste collectors. Its goal is to make collectors and collection
points easier to find while supporting the scheduling and tracking of
collections.

The project is being modernized. The backend is the active implementation; the
previous Thymeleaf client is kept under [`legacy/`](legacy/README.md) only as a
reference for the future frontend rebuild.

## Current status

The backend currently provides:

- registration and basic email/password authentication;
- person lookup, update, and deletion;
- collection creation, lookup, update, and deletion;
- queries for available, owned, and accepted collections;
- persistence of addresses and recyclable material types;
- database migrations with Flyway;
- OpenAPI/Swagger documentation;
- health checks with Spring Boot Actuator.

Per-user authorization, password recovery, the complete collection lifecycle,
points, chat, and notifications are not finished yet. See the
[`roadmap`](docs/roadmap.md).

## Technology stack

- Java 25 (LTS)
- Spring Boot 3.5
- Spring Web
- Spring Data JPA and Hibernate
- PostgreSQL 16
- Flyway
- Spring Security Crypto and BCrypt
- Springdoc OpenAPI
- Spring Boot Actuator
- Maven Wrapper
- Docker Compose

## Repository structure

```text
.
├── find-collectors/        # Active multi-module Maven backend
│   ├── api/                # Controllers, DTOs, and HTTP configuration
│   ├── application/        # Use cases
│   ├── domain/             # Domain entities and contracts
│   └── infrastructure/     # JPA, PostgreSQL, and BCrypt
├── docs/                   # Living documentation and historical material
├── legacy/                 # Discontinued Thymeleaf client
├── docker-compose.yml      # PostgreSQL for local development
└── .env.example            # Example local environment variables
```

See the [architecture overview](docs/architecture/overview.md) for details.

## Running locally

Prerequisites:

- Java 25;
- Docker with Docker Compose;
- Git.

Clone the repository and prepare the local environment variables:

```bash
cp .env.example .env
docker compose up -d database
```

Build and test the backend:

```bash
cd find-collectors
./mvnw clean install
```

Start the API:

```bash
./mvnw -pl api spring-boot:run
```

The application uses port `8080` by default. Once it has started:

- Health check: <http://localhost:8080/actuator/health>
- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs/find-collectors-api>

Stop the database with:

```bash
docker compose down
```

See the [local development guide](docs/development/local-setup.md) for
configuration details, troubleshooting, and migration precautions.

## Documentation

- [Documentation index](docs/README.md)
- [Architecture overview](docs/architecture/overview.md)
- [Modernization history](docs/modernization/refactoring-log.md)
- [Technical and product roadmap](docs/roadmap.md)
- [Legacy client](legacy/README.md)
- [Original 2022 documentation](docs/archive/README.md)

## Product context

The project was inspired by the UN Sustainable Development Goal 11, which
focuses on sustainable cities and communities. Its long-term vision is to build
a network that encourages recycling, improves the visibility and income of
waste collectors, and rewards participants for completed collections.

## Authors

- [Anilson22](https://github.com/Anilson22)
- [enthonyedu](https://github.com/enthonyedu)
- [rodolfoguner](https://github.com/rodolfoguner)
