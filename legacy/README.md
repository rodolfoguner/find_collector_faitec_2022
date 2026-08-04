# Legacy client

This directory preserves the former Spring MVC/Thymeleaf client as a reference
during modernization. It is not part of the active Maven build.

## Contents

- `thymeleaf-client/`: the former web client, based on Java 8, Spring 5, and
  Thymeleaf.

The client references a model module that no longer exists in the current Maven
reactor and must not be treated as an executable application. New backend
changes do not need to maintain compatibility with this code.

Before building the new frontend, use these files only to recover journeys,
content, and visual references that remain relevant. Features must be confirmed
against the current roadmap and OpenAPI contract.
