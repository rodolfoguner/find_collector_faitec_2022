---
name: api-contract-review
description: Review the Find Collectors REST API contract for DTO boundaries, validation, HTTP semantics, error handling, sensitive data, and OpenAPI consistency. Use for review or audit requests; do not modify code unless the user also asks for fixes.
---

# API contract review

Review the implemented behavior, not only controller signatures.

## Review areas

- Confirm every endpoint uses request and response DTOs rather than JPA
  entities, including nested response objects.
- Trace serialization paths for passwords, audit internals, relationships, and
  other fields that must not become public.
- Check Bean Validation coverage, nullability, enum parsing, immutable fields,
  and consistency between DTO rules and database constraints.
- Check status codes and response shapes for create, update, delete, validation,
  authentication, authorization, conflict, and not-found cases.
- Confirm expected failures use the standardized `ExceptionResponse` contract
  and stable `ErrorCode` values. Flag internal exception details exposed by
  fallback errors.
- Compare endpoint paths, payloads, filters, pagination, and documented schemas
  with OpenAPI and the living documentation.
- Account for `spring.jpa.open-in-view=false`: identify lazy data accessed only
  after the persistence context closes.

Report findings in severity order with exact file and line evidence. Explain the
observable impact and the smallest safe correction. If no actionable findings
remain, state that explicitly and mention any verification limits.
