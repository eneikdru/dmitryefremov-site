package com.dmitryefremov.site.acceptance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BackupRestoreAcceptanceTest {

    @Test
    void testBackupAndRestore() throws Exception {
        // Execute the bash script testing backup and restore, setting correct paths
        ProcessBuilder processBuilder = new ProcessBuilder("./scripts/test_backup_restore.sh");

        // Use a temporary directory for tests inside the bash script
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
    }
}
