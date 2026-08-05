# Modernization history

This document records significant structural changes made during the project
modernization. It does not replace Git history; its purpose is to explain the
consolidated state and the motivation behind key decisions.

## Foundation consolidation — August 2026

### Platform and build

- upgraded Java 21 to Java 25 LTS;
- upgraded Spring Boot 3.5.11 to 3.5.16;
- added Maven Wrapper 3.9.16;
- explicitly configured Lombok as an annotation processor for Java 25;
- upgraded Springdoc OpenAPI from 2.3.0 to 2.8.17.

### Database and local execution

- simplified Docker Compose to run PostgreSQL 16;
- removed the reference to the former, missing `db-scripts` directory;
- externalized ports and credentials through environment variables;
- enabled and corrected the official Flyway and Hibernate properties;
- standardized primary keys as `BIGSERIAL` and foreign keys as `BIGINT`;
- aligned garbage-type table names with their JPA mappings;
- made audit columns mandatory in the database schema.

### Temporary security scope

- retained BCrypt through `spring-security-crypto`;
- temporarily removed web security, which does not yet provide complete
  authentication and authorization;
- removed the manual `SecurityAutoConfiguration` exclusion.

### Operations and quality

- added Spring Boot Actuator;
- exposed the `health` and `info` endpoints;
- added a minimal test that loads the complete application context;
- validated migrations and JPA mappings against an empty PostgreSQL database.

### Repository organization

- moved the former Thymeleaf client to `legacy/thymeleaf-client`;
- removed the standalone static HTML prototype from the maintained repository;
- moved the 2022 academic documents to `docs/archive/project-2022`;
- introduced living Markdown documentation for architecture, environment, and
  roadmap.

## API error contract — August 2026

- replaced generic exceptions in application operations with business
  exceptions carrying stable `ErrorCode` values;
- mapped not-found, conflict, unauthorized, and unprocessable-entity exception
  categories to HTTP statuses `404`, `409`, `401`, and `422` respectively;
- retained `400` for request validation failures and `500` as the fallback for
  unexpected runtime failures;
- standardized error responses with `code`, `message`, `path`, and `timestamp`;
- stopped exposing Java exception class names as public error codes;
- added unit coverage for the global exception handler and its business
  exception hierarchy.

## Policy for future entries

Add a section whenever a change significantly affects:

- the platform version;
- architecture or module boundaries;
- the data model;
- the public API contract;
- authentication and authorization;
- deployment or operations.

Small fixes remain documented through commits and pull requests only.
