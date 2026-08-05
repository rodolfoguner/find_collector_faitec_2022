# Technical and product roadmap

The roadmap prioritizes a predictable, testable backend before the frontend is
rebuilt. The order may change, but every phase should end with a green build,
passing tests, and updated documentation.

## Completed: executable foundation

- [x] Upgrade Java and Spring Boot.
- [x] Add the Maven Wrapper.
- [x] Stabilize PostgreSQL with Docker Compose.
- [x] Correct Spring and Flyway properties.
- [x] Align the database schema with JPA mappings.
- [x] Add a minimal application context test.
- [x] Add a health check.
- [x] Upgrade Springdoc/Swagger.
- [x] Separate active code, legacy code, and historical documentation.

## Next phase: API consistency

- [x] Replace `RuntimeException` with specific business exceptions.
- [x] Standardize the error response format.
- [x] Use consistent HTTP status codes.
- [ ] Add request and response DTOs to every endpoint.
- [ ] Prevent controllers from exposing JPA entities directly.
- [ ] Version endpoints under `/api/v1`.
- [ ] Add pagination, sorting, and filters to list operations.

## Automated quality

- [ ] Add unit tests for use cases with JUnit and Mockito.
- [ ] Add controller tests with MockMvc.
- [ ] Isolate integration tests with Testcontainers and PostgreSQL.
- [ ] Validate migrations against an empty database in CI.
- [ ] Add a continuous integration pipeline.

## Authentication and accounts

- [ ] Implement Spring Security for the API.
- [ ] Define and implement access and refresh tokens.
- [ ] Add an endpoint for the authenticated user.
- [ ] Enforce authorization by user type and resource ownership.
- [ ] Implement password changes.
- [ ] Implement password recovery with short-lived tokens.

## Core collection lifecycle

- [ ] Replace `accept` and `collected` with `CollectStatus`.
- [ ] Define `PENDING`, `ACCEPTED`, `COMPLETED`, and `CANCELLED` transitions.
- [ ] Allow recyclers to publish and track collections.
- [ ] Allow collectors to find and accept collections.
- [ ] Protect collection acceptance against concurrency.
- [ ] Authorize cancellation and completion operations.
- [ ] Implement history and recurrence.

## Discovery and engagement

- [ ] Search collectors by city, material, and eventually distance.
- [ ] Model collection points as a dedicated resource.
- [ ] Evolve sponsorship into a relationship with history.
- [ ] Add public collector profiles and stories.
- [ ] Evaluate reputation and ratings after completed collections.

## Incentives and integrations

- [ ] Model point transactions as an auditable ledger.
- [ ] Award points for completed collections.
- [ ] Add partners, rewards, and redemptions.
- [ ] Add email notifications.
- [ ] Evaluate WhatsApp, maps, and geolocation.
- [ ] Evaluate chat only after the core workflow is stable.

## Frontend

The frontend technology will be selected after the API has authentication,
stable contracts, complete OpenAPI documentation, pagination, and a defined
error format. The client under `legacy/` is only a reference for journeys,
content, and visual elements; it is not a required foundation for the new
implementation.
