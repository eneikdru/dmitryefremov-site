package com.eneik.generated.dmitryefremov.backup;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BackupRestoreIntegrationTest {

    @Test
    public void testBackupAndRestoreScripts() throws IOException, InterruptedException {
        File backupDir = new File("target/test-backups2");
        backupDir.mkdirs();

        // Write a mock dump cmd to generate output resembling pg_dump
        File dumpCmd = new File("target/fake-pg-dump.sh");
        Files.writeString(dumpCmd.toPath(), "#!/bin/bash\ncat << 'MOCKDUMP' > \"$1\"\n-- PostgreSQL Database Dump\n-- Database: testdb\nCREATE TABLE IF NOT EXISTS publication_schedule (id UUID PRIMARY KEY, title TEXT, status TEXT);\nINSERT INTO publication_schedule (id, title, status) VALUES ('11111111-1111-1111-1111-111111111111', 'Sample Article', 'PUBLISHED');\nMOCKDUMP\nexit 0");
        dumpCmd.setExecutable(true);

        // 1. Run backup script
        ProcessBuilder backupPb = new ProcessBuilder("bash", "scripts/backup.sh");
        backupPb.environment().put("BACKUP_DIR", backupDir.getAbsolutePath());
        backupPb.environment().put("POSTGRES_DB", "testdb");
        backupPb.environment().put("DUMP_CMD", dumpCmd.getAbsolutePath());
        backupPb.environment().put("PATH", "/usr/bin:/bin");

        Process backupProcess = backupPb.start();
        int backupExitCode = backupProcess.waitFor();

        // Log output if it fails
        if (backupExitCode != 0) {
            System.err.println("Backup script failed. Output:");
            backupProcess.getInputStream().transferTo(System.err);
            backupProcess.getErrorStream().transferTo(System.err);
        }

        assertEquals(0, backupExitCode, "Backup script should succeed");

        File[] dumpFiles = backupDir.listFiles((dir, name) -> name.startsWith("db_backup_") && name.endsWith(".dump"));
        assertTrue(dumpFiles != null && dumpFiles.length > 0, "Backup file should be created");

        File dumpFile = dumpFiles[0];

        // Ensure the dump file actually has the correct test output
        String dumpContent = Files.readString(dumpFile.toPath());
        assertTrue(dumpContent.contains("publication_schedule"), "Content integrity check: Backup should contain correct test data");

        // Write a fake restore cmd to simulate db restoration that works on mock data
        File restoreCmd = new File("target/fake-pg-restore.sh");
        Files.writeString(restoreCmd.toPath(), "#!/bin/bash\ncat \"$1\" | grep 'publication_schedule' > /dev/null\nexit 0");
        restoreCmd.setExecutable(true);

        // 2. Run restore script
        ProcessBuilder restorePb = new ProcessBuilder("bash", "scripts/restore.sh", dumpFile.getAbsolutePath());
        restorePb.environment().put("POSTGRES_DB", "testdb");
        restorePb.environment().put("RESTORE_CMD", restoreCmd.getAbsolutePath());
        restorePb.environment().put("PATH", "/usr/bin:/bin");

        Process restoreProcess = restorePb.start();
        int restoreExitCode = restoreProcess.waitFor();

        // Log output if it fails
        if (restoreExitCode != 0) {
            System.err.println("Restore script failed. Output:");
            restoreProcess.getInputStream().transferTo(System.err);
            restoreProcess.getErrorStream().transferTo(System.err);
        }

        assertEquals(0, restoreExitCode, "Restore script should succeed");
    }
}
