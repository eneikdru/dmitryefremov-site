package com.dmitryefremov.site.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

public class BackupRestoreAcceptanceTest {

    @Test
    void testBackupAndRestore() throws Exception {
        // Since testcontainers is unavailable due to docker overlayfs issues in sandbox,
        // and @SpringBootTest wrappers around ProcessBuilder were rejected in review,
        // this test executes the bash script as a plain integration test without Spring context bootstrapping.
        ProcessBuilder processBuilder = new ProcessBuilder("./scripts/test_backup_restore.sh");

        processBuilder.environment().put("POSTGRES_HOST", "127.0.0.1");
        processBuilder.environment().put("POSTGRES_PORT", "5432");
        processBuilder.environment().put("POSTGRES_USER", "postgres");
        processBuilder.environment().put("POSTGRES_PASSWORD", "postgres");
        processBuilder.environment().put("POSTGRES_DB", "test_db");
        // Ensure path includes locations where psql might be
        processBuilder.environment().put("PATH", System.getenv("PATH") + ":/usr/bin:/bin:/usr/local/bin");

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        int exitCode = process.waitFor();

        System.out.println(output.toString());

        assertThat(exitCode).isEqualTo(0);
        assertThat(output.toString()).contains("=== All Backup & Restore Verification Suite Checks Passed ===");

        // Assert some data via JDBC to prove restoration succeeded on the test_db
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/test_db", "postgres", "postgres");
             Statement stmt = conn.createStatement()) {
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM publication_schedule");
            rs1.next();
            assertThat(rs1.getInt(1)).isGreaterThanOrEqualTo(1);

            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM service_orders");
            rs2.next();
            assertThat(rs2.getInt(1)).isGreaterThanOrEqualTo(1);

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM direct_messages");
            rs3.next();
            assertThat(rs3.getInt(1)).isGreaterThanOrEqualTo(1);
        }
    }
}
