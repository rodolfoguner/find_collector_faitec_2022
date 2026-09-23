---
name: collection-lifecycle
description: Implement or review Find Collectors collection workflow behavior involving CollectStatus, acceptance, completion, cancellation, ownership, recurrence, history, or concurrency. Use only for collection lifecycle work.
---

# Collection lifecycle

Treat `CollectStatus` as the single lifecycle source of truth. Never add boolean
flags that duplicate its meaning.

## Domain analysis

Before changing behavior, write down the affected current state, requested
transition, actor, ownership requirement, and expected failure. Use the existing
roadmap as scope guidance, but do not invent unresolved product policy. Ask for
direction only when a missing rule would materially change user-visible
behavior.

## Implementation constraints

- Put transition rules in application/domain behavior, not controllers.
- Derive the acting recycler or collector from authenticated identity once
  authentication is available; until then, do not mistake a request-supplied ID
  for authorization.
- Reject invalid or repeated transitions with a stable business error.
- Make collection acceptance atomic so two collectors cannot both succeed.
  Choose optimistic locking or a conditional database update based on the
  implementation context, and report the concurrency guarantee.
- Preserve the publisher relationship and set the accepting collector only as
  part of a valid acceptance transition.
- Keep list queries explicit about which statuses they include; avoid ambiguous
  interpretations of "available", "owned", and "accepted".
- Ensure API DTOs expose the status without accepting unrestricted lifecycle
  mutation through general create or update requests.
- When history, recurrence, points, or notifications are not requested, expose
  extension points only if needed and leave those features out of scope.

Update migrations and documentation when the persisted lifecycle changes.
Verify compilation plus the most relevant transition, repository, or
concurrency checks that are in scope.
