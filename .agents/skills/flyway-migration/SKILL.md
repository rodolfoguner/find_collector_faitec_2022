---
name: flyway-migration
description: Design or review a PostgreSQL Flyway migration for Find Collectors and align it with JPA mappings. Use for schema, constraint, enum persistence, data migration, or migration-failure work; do not use for persistence-neutral changes.
---

# Flyway migration

Preserve upgrade safety for both an empty database and a database containing
data from every previously shared migration.

## Workflow

1. Read all existing migrations in order and inspect the affected entity and
   repository mappings.
2. Add the next incremental migration. Never rewrite a shared migration to make
   the final schema look cleaner.
3. Sequence data changes safely: add a compatible structure, backfill or
   transform existing rows, verify assumptions, then enforce constraints or
   remove obsolete structures.
4. Keep PostgreSQL types, lengths, nullability, defaults, foreign keys, indexes,
   enum representations, and naming aligned with JPA.
5. Consider locking and table-rewrite impact before applying changes that may
   become expensive as data grows.
6. Validate against an empty database and an upgraded database when database
   verification is in scope and available. Use the configured Docker Compose
   service; do not destroy volumes or data without explicit authorization.
7. Update architecture or modernization documentation when the data model or
   migration policy materially changes.

Report the upgrade path, preservation strategy, verification performed, and any
rollback or operational caveat. Do not describe a destructive reset as the
normal migration strategy.
