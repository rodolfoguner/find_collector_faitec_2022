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

Authentication uses HS256-signed JWTs backed by PostgreSQL sessions. HTTP
requests do not use server-side HTTP sessions, but token validity depends on the
persisted authentication session.

`POST /api/login` validates credentials and creates an independent session for
each login. It returns a 15-minute access token and a refresh token with an
absolute 7-day session deadline. Both tokens carry the same `sid` (session UUID)
and distinct `jti` values (token UUIDs). Refreshing preserves `sid` and the
original deadline; access expiration is capped at that deadline.

`POST /api/refresh` accepts `{ "refreshToken": "..." }`, validates the refresh
JWT, consumes its persisted SHA-256 hash, and returns a new token pair. Used
refresh hashes remain recorded until the session expires. Reusing a consumed
refresh revokes the entire session, including its latest refresh and all access
tokens. The revocation commits before the API returns `401 INVALID_TOKEN`.
Concurrent renewals are serialized by a PostgreSQL row lock; at most one
renewal succeeds and a repeated use revokes the session. Clients must coordinate
refresh calls across requests/tabs. A lost refresh response followed by a retry
can require a new login; there is no retry grace period.

`POST /api/logout` requires a valid access bearer token and returns
`204 No Content` after revoking its session. All tokens belonging to that `sid`
are rejected on subsequent checks, while other logins remain active. An expired
or revoked access token cannot authenticate logout; a repeated logout with that
token returns `401`. Already authorized requests may finish.

Protected requests verify the JWT signature, issuer, expiration, token type and
required identity/session claims, then query the session's owner, revocation
and absolute expiration. Refresh tokens are rejected as bearer credentials.
`GET /api/me` resolves the current account from the access-token subject.
Missing, expired and revoked sessions yield `401 INVALID_TOKEN`. Session-store
failures yield `503 AUTHENTICATION_UNAVAILABLE`, without accepting unchecked
tokens or returning database details. Missing bearer credentials retain
`401 AUTHENTICATION_REQUIRED`.

`domain` defines session state, rotation decisions, persistence and token codec
contracts; `application` orchestrates login sessions, refresh and logout;
`infrastructure` implements transactional session persistence; `api` implements
the JWT codec, HTTP DTO mapping, security integration and scheduled cleanup.
Session and refresh persistence entities stay in `infrastructure` and are never
part of HTTP contracts.

Migration `V4` adds `auth_sessions` and `auth_refresh_tokens`, preserving existing
people and collection data. Only refresh hashes and session metadata are stored;
raw JWTs are not persisted. Deleting a person cascades to their sessions and
refresh history. Expired sessions are cleaned up hourly, cascading to their
refresh history; session validation rejects expired records even before cleanup.
The interval can be configured through `security.sessions.cleanup-interval`
(default `PT1H`).

`POST /api/login`, `POST /api/signup`, `POST /api/refresh`, health, and OpenAPI
resources are public. Other endpoints require `Authorization: Bearer <token>`.
The signing secret comes from the required `JWT_SECRET` environment variable
and must contain at least 32 UTF-8 bytes. Tokens issued before migration to
session-backed authentication lack `sid`/`jti` and require a new login. Deploy
all API instances together: old instances do not enforce session revocation.

## Known limitations

- accounts have no email activation, password-change, or recovery flow;
- there is no authorization based on resource ownership or user type;
- logout revokes the current session only; account-wide logout is not implemented;
- collection points have no dedicated search or geolocation resource;
- there is no communication channel between collectors and recyclers;
- the context test uses the PostgreSQL instance configured in the environment;
- no active frontend technology has been selected yet.
