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

## API error contract

Application and domain operations throw business exceptions carrying a stable
`ErrorCode`. The API translates each exception category into the corresponding
HTTP status without exposing Java exception class names as public error codes.

| Exception category | HTTP status |
| --- | --- |
| `NotFoundException` | `404 Not Found` |
| `ConflictException` | `409 Conflict` |
| `UnauthorizedException` | `401 Unauthorized` |
| `UnprocessableEntityException` | `422 Unprocessable Entity` |
| Request validation failure | `400 Bad Request` |
| Unexpected runtime failure | `500 Internal Server Error` |

Every error response uses the same JSON structure:

```json
{
  "code": "PERSON_NOT_FOUND",
  "message": "Person with id 1 not found",
  "path": "/api/person/1",
  "timestamp": "2026-08-04T22:38:57.123Z"
}
```

`code` is the stable machine-readable identifier and `message` describes the
specific failure. `path` is the request URI, and `timestamp` is the instant at
which the handler created the response. Validation failures use
`VALIDATION_ERROR`; unexpected failures use `INTERNAL_SERVER_ERROR`.

## Known limitations

- current authentication only validates credentials and issues no token or session;
- there is no authorization based on resource ownership or user type;
- some controllers still expose JPA entities directly;
- the context test uses the PostgreSQL instance configured in the environment;
- no active frontend technology has been selected yet.
