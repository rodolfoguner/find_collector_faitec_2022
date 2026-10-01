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

The collection lifecycle is represented by `CollectStatus`, with `PENDING`,
`ACCEPTED`, `COMPLETED`, and `CANCELLED` states. Recurrence remains represented
by the `recurrent` attribute.

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

## Account password policy

New accounts must use a password containing between 8 and 100 characters, at
least one number, and at least one uppercase letter. The signup endpoint rejects
passwords that do not meet this policy with `400 Bad Request` and the standard
`VALIDATION_ERROR` response. Login does not revalidate password strength so
accounts created before this policy remain able to authenticate.

## Authentication

Authentication is stateless and uses signed JWTs. `POST /api/login` validates
credentials and returns a 15-minute access token plus a 7-day refresh token.
`POST /api/refresh` validates a refresh token and returns a new token pair.
Refresh tokens are rejected as bearer credentials for protected resources.
`GET /api/me` resolves the current account from the access-token subject.

`POST /api/login`, `POST /api/signup`, `POST /api/refresh`, health, and OpenAPI
resources are public. Other endpoints require `Authorization: Bearer <token>`.
The signing secret comes from the required `JWT_SECRET` environment variable
and must contain at least 32 bytes.

## Known limitations

- accounts have no email activation, password-change, or recovery flow;
- there is no authorization based on resource ownership or user type;
- refresh tokens are stateless and cannot yet be revoked before expiration;
- collection points have no dedicated search or geolocation resource;
- there is no communication channel between collectors and recyclers;
- the context test uses the PostgreSQL instance configured in the environment;
- no active frontend technology has been selected yet.
