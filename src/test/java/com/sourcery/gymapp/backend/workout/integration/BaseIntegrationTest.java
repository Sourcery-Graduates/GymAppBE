package com.sourcery.gymapp.backend.workout.integration;

import com.sourcery.gymapp.backend.workout.integration.config.SingletonPostgresContainer;
import com.sourcery.gymapp.backend.workout.integration.config.TestAuditConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
@Import(TestAuditConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> postgresContainer = SingletonPostgresContainer.getInstance();

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Test
    void connectionEstablished() {
        assertThat(postgresContainer.isCreated()).isTrue();
        assertThat(postgresContainer.isRunning()).isTrue();
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.execute("""
            DO $$ DECLARE r RECORD;
            BEGIN 
                FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'workout_data')
                LOOP 
                    EXECUTE 'TRUNCATE TABLE workout_data.' || r.tablename || ' CASCADE'; 
                END LOOP; 
            END $$;
        """);
    }
}
