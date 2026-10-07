package br.fai.findcollectors.security;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.sql.DataSource;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "JWT_SECRET=test-secret-with-at-least-thirty-two-bytes")
@EnabledIfEnvironmentVariable(named = "RUN_POSTGRES_TESTS", matches = "true")
class PostgresAuthMigrationTest {
    @Autowired DataSource dataSource;

    @Test
    void migrationShouldWorkOnEmptySchemaAndPreserveExistingPeopleOnUpgrade() throws Exception {
        for (boolean upgrade : new boolean[]{false, true}) {
            String schema = "auth_migration_test_" + UUID.randomUUID().toString().replace("-", "");
            try (var connection = dataSource.getConnection(); var statement = connection.createStatement()) {
                statement.execute("CREATE SCHEMA " + schema);
                try {
                    if (upgrade) {
                        Flyway.configure().dataSource(dataSource).schemas(schema).defaultSchema(schema)
                                .locations("classpath:db/migration").target("3").load().migrate();
                        statement.execute("INSERT INTO " + schema + ".persons(email, person_type, created_at, updated_at) "
                                + "VALUES ('migration-test@example.com', 'COLLECTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)");
                    }
                    Flyway.configure().dataSource(dataSource).schemas(schema).defaultSchema(schema)
                            .locations("classpath:db/migration").load().migrate();
                    try (var result = statement.executeQuery("SELECT count(*) FROM " + schema + ".persons")) {
                        result.next();
                        assertThat(result.getLong(1)).isEqualTo(upgrade ? 1 : 0);
                    }
                    statement.executeQuery("SELECT id, person_id, created_at, expires_at, revoked_at FROM " + schema + ".auth_sessions").close();
                    statement.executeQuery("SELECT token_hash, session_id, consumed_at FROM " + schema + ".auth_refresh_tokens").close();
                } finally {
                    // This random schema belongs exclusively to this test.
                    statement.execute("DROP SCHEMA " + schema + " CASCADE");
                }
            }
        }
    }
}
