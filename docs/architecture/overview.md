# Architecture overview

## Context

The Find Collectors backend exposes a REST API for managing people and
recyclable material collections. PostgreSQL provides persistence, and Flyway
manages schema evolution.

The frontend will be redesigned and is not part of the active build.

## Modules

The backend is located under `find-collectors/` and uses a four-module Maven
reactor:

| Module | Responsibility |
| --- | --- |
| `domain` | Entities, enums, value objects, exceptions, and repository contracts |
| `application` | Use cases and application rules |
| `infrastructure` | JPA, PostgreSQL, and password hashing implementations |
| `api` | REST controllers, DTOs, mappers, and application configuration |

Main dependency flow:

```text
HTTP request
     ↓
    api
     ↓
application ──→ domain ←── infrastructure
     ↑                         ↓
     └──────────────────── PostgreSQL
```

The `api` module assembles the application and depends on `application`,
`domain`, and `infrastructure`. `application` depends on contracts defined in
`domain`, while `infrastructure` implements those contracts.

## Current model

The central entities are:

- `Person`: represents recyclers and collectors;
- `Collect`: represents a collection request;
- `Address`: an address embedded in people and collections;
- `GarbageType`: materials associated with people and collections;
- `PersonType`: distinguishes `RECYCLER` from `COLLECTOR`.

The collection model still uses the `accept`, `collected`, and `recurrent`
booleans. Replacing the first two with an explicit lifecycle is part of the
roadmap.

## External interfaces

- REST API: `/api/**`;
- OpenAPI document: `/v3/api-docs/find-collectors-api`;
- Swagger UI: `/swagger-ui/index.html`;
- application health: `/actuator/health`;
- local database: PostgreSQL 16 through Docker Compose.

## Known limitations

- current authentication only validates credentials and issues no token or session;
- there is no authorization based on resource ownership or user type;
- some controllers still expose JPA entities directly;
- business exceptions are still handled generically;
- the context test uses the PostgreSQL instance configured in the environment;
- no active frontend technology has been selected yet.
