---
name: find-collectors-backend-change
description: Implement a backend feature or refactoring in the Find Collectors Java modules. Use for endpoints, DTOs, use cases, domain behavior, repositories, and coordinated cross-module changes; do not use for review-only requests.
---

# Find Collectors backend change

Deliver a focused vertical change while preserving the module boundaries and
public API conventions in `AGENTS.md`.

## Workflow

1. Read the relevant roadmap and architecture sections, then inspect the current
   controller, use case, domain contract, persistence adapter, and migrations
   touched by the request.
2. Define the smallest complete behavior. Distinguish requested work from
   adjacent roadmap items and avoid implementing the latter implicitly.
3. Keep HTTP DTOs and mapping in `api`, orchestration and business rules in
   `application`, domain concepts and contracts in `domain`, and technical
   implementations in `infrastructure`.
4. When persistence changes, follow the repository's Flyway rules and keep the
   entity mapping consistent with the resulting schema.
5. Preserve stable error codes and translate expected failures through the
   existing exception contract.
6. Run focused verification through interactive Fish. Compile at minimum and
   run relevant tests or database validation when they are part of the task.
7. Update living documentation when the implementation changes behavior,
   architecture, setup, or roadmap status.

Before finishing, inspect the diff for accidental entity exposure, sensitive
fields, unrelated edits, and incomplete cross-module updates. Summarize what is
implemented, what remains intentionally out of scope, and what was verified.
