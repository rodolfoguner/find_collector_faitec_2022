---
name: documentation-sync
description: Synchronize Find Collectors living Markdown documentation with the current code and Git changes. Use for documentation audits, roadmap updates, architecture changes, or release-readiness checks; treat archived 2022 documents as historical input only.
---

# Documentation synchronization

Use code and migrations as evidence for implemented behavior. Use the archived
2022 documents only to identify historical product intent that still requires a
current decision.

## Workflow

1. Inspect the relevant Git diff and recent commits.
2. Compare behavior with `README.md`, `docs/architecture/overview.md`,
   `docs/roadmap.md`, `docs/development/local-setup.md`, and
   `docs/modernization/refactoring-log.md` as applicable.
3. Update only documents affected by the change:
   - README for current capabilities and entry points;
   - architecture for module boundaries, model, interfaces, contracts, and
     known limitations;
   - roadmap for completed and pending work;
   - local setup for executable environment or operational changes;
   - refactoring log for significant platform, architecture, data model,
     security, or public-contract decisions.
4. Mark a roadmap checkbox complete only when the usable behavior exists. Keep
   partial foundations and future transitions explicitly pending.
5. Remove contradictions and stale claims without rewriting historical archive
   files as if they described the current system.

Finish with a concise list of documents changed, discrepancies resolved, and
remaining decisions or limitations. Do not change implementation code during a
documentation-only request unless the user expands the scope.
