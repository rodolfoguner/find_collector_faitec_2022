# Find Collectors project guidance

## Scope and sources of truth

- Treat `find-collectors/` as the active backend.
- Treat `legacy/` and `docs/archive/` as historical references only. Do not make
  new code depend on them.
- Read `docs/architecture/overview.md` and the relevant part of
  `docs/roadmap.md` before making architectural or product-scope decisions.
- Preserve unrelated user changes in the working tree.

## Architecture

- Keep the module flow described in the architecture documentation:
  `api` assembles the application, `application` implements use cases, `domain`
  defines the model and contracts, and `infrastructure` implements persistence
  and technical adapters.
- Do not expose JPA entities in HTTP request or response contracts. Use DTOs and
  explicit mappers in the API module.
- Keep business rules out of controllers and persistence adapters.
- Preserve the standardized API error contract and use stable `ErrorCode`
  values for expected failures.
- Model collection workflow changes through `CollectStatus`; do not reintroduce
  boolean lifecycle flags.

## Database changes

- Flyway migrations are append-only once shared. Add a new incremental migration
  instead of editing an existing migration.
- Preserve existing data before adding constraints, changing types, renaming, or
  dropping columns.
- Keep Flyway schema definitions and JPA mappings aligned.
- Do not remove Docker volumes or local database data unless the user explicitly
  authorizes that destructive action.

## Local environment and verification

- Java and Maven are managed by `mise` and activated by interactive Fish.
- Run Maven commands through Fish, for example:
  `fish -lic 'cd find-collectors; mvn compile'`.
- Use Java 25 for compilation and debugging.
- Verify changes in proportion to their risk. At minimum, compile affected
  modules; run relevant tests and migration checks when they are in scope and
  the environment supports them.
- Report environmental validation failures separately from code failures.

## Git and documentation

- Work on the current feature or refactor branch. Do not switch, merge, or push
  changes to `develop` unless the user explicitly requests it.
- Keep changes focused and do not commit unless requested.
- Update living documentation in the same change when behavior, architecture,
  public API contracts, environment setup, or roadmap completion changes.
- Mark roadmap items complete only when their usable behavior is implemented,
  not when only a type, placeholder, or partial foundation exists.
- Never commit `.env`, credentials, tokens, or other local secrets.
