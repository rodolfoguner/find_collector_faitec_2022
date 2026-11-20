# Continuous integration

The `CI` GitHub Actions workflow runs for pull requests and pushes targeting
`develop` or `master`. Its stable `Merge gate` job succeeds only when all of the
following blocking checks pass:

- the complete Maven test suite against PostgreSQL 16;
- the existing JaCoCo thresholds of 80% line and 70% branch coverage for
  `application` and `api`;
- the SonarQube quality gate, with the aggregate JaCoCo XML report imported;
- Snyk Code findings at `high` severity or above.

Snyk Open Source scans direct and transitive Maven dependencies and retains its
SARIF report for 14 days. That job is intentionally non-blocking while the
initial findings are triaged. Authentication errors and tool failures in that
job also do not block a merge, so its result must still be reviewed.

## Repository configuration

Create these GitHub Actions settings before requiring the workflow:

| Type | Name | Purpose |
| --- | --- | --- |
| Secret | `SONAR_TOKEN` | SonarQube project-analysis token |
| Variable | `SONAR_HOST_URL` | Externally reachable SonarQube Server URL |
| Variable | `SONAR_PROJECT_KEY` | Key of the SonarQube project |
| Secret | `SNYK_TOKEN` | Snyk token with Code and Open Source access |

Configure the SonarQube project quality gate for the desired new-code coverage,
reliability, maintainability, and security criteria. The Maven build enforces
its module coverage minimums before SonarQube runs; SonarQube owns its separate
server-side quality gate. Pull-request analysis and decoration require a
SonarQube edition that supports branch and pull-request analysis (or SonarQube
Cloud).

Then create a branch ruleset for `develop` and `master` in GitHub, require a
pull request before merging, require status checks to pass, and select
`CI / Merge gate`. The workflow alone reports status; the branch ruleset is what
prevents a user from merging a failing pull request. Enable the setting that
requires branches to be up to date before merging if every merge must be tested
against the latest target branch.

GitHub does not provide repository secrets to workflows triggered by pull
requests from forks. Because SonarQube and Snyk Code are blocking checks, forked
pull requests need a trusted-branch review process or separate scanner
integrations that can analyze untrusted contributions without exposing tokens.

## Recommended PostgreSQL Testcontainers migration

The workflow currently supplies PostgreSQL as a GitHub Actions service because
the context and opt-in integration tests use the configured shared data source.
To make the same tests self-contained locally and in CI, migrate them to
Testcontainers in the `api` module:

1. Add test-scoped `org.testcontainers:junit-jupiter`,
   `org.testcontainers:postgresql`, and `org.springframework.boot:spring-boot-testcontainers`
   dependencies. Let Spring Boot dependency management select compatible
   Testcontainers versions.
2. Define a PostgreSQL 16 container in a shared integration-test base class and
   annotate it with `@Container` and `@ServiceConnection`. For tests that need
   custom Flyway schemas, keep `@DynamicPropertySource` only for the schema
   properties; the service connection supplies the JDBC URL and credentials.
3. Remove `@EnabledIfEnvironmentVariable(RUN_POSTGRES_TESTS)` after every
   PostgreSQL test owns a container. This prevents CI from accidentally skipping
   database behavior.
4. Keep the migration test's random schemas so it can validate both an empty
   database and an upgrade from `V3`. Use one container per test class initially;
   switch to a suite-wide singleton only if startup time becomes material and
   test cleanup remains reliable.
5. Once the containerized suite is green in CI, remove the workflow's
   `services.postgres` block and the database environment variables. GitHub's
   Ubuntu runners already provide the Docker runtime required by Testcontainers.

An initial base class can use this shape:

```java
@Testcontainers
abstract class PostgresIntegrationTest {
    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");
}
```

Do not enable Testcontainers' reusable-container option in CI; deterministic
disposal is more important there than reusing state between workflow runs.
