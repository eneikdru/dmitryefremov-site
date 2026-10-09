#!/usr/bin/env bash
set -euo pipefail

# Test Suite for Database Backup and Restore
# Role: BARCAN-TAG-05 (DevOps)

TEST_DIR=$(mktemp -d)
trap 'rm -rf "${TEST_DIR}"' EXIT

echo "=== Running Database Backup and Restore Verification ==="

export BACKUP_DIR="${TEST_DIR}/backups"
export POSTGRES_DB="test_db"
export RETENTION_DAYS=7

# Step 1: Execute Backup
echo "[TEST] Running scripts/backup.sh..."
./scripts/backup.sh

# Step 2: Assert backup file created
DUMP_FILES=("${BACKUP_DIR}"/db_backup_*.dump)
if [ ! -f "${DUMP_FILES[0]}" ]; then
    echo "[FAIL] Backup file was not created in ${BACKUP_DIR}"
    exit 1
fi
echo "[PASS] Backup file created: ${DUMP_FILES[0]}"

# Step 3: Assert file permissions (600 or rw-------)
FILE_PERMS=$(stat -c "%a" "${DUMP_FILES[0]}" 2>/dev/null || stat -f "%Lp" "${DUMP_FILES[0]}")
if [ "${FILE_PERMS}" != "600" ]; then
    echo "[FAIL] Backup file permission is ${FILE_PERMS}, expected 600"
    exit 1
fi
echo "[PASS] Backup file permission verified: ${FILE_PERMS}"

# Step 4: Execute Restore
echo "[TEST] Running scripts/restore.sh on ${DUMP_FILES[0]}..."
./scripts/restore.sh "${DUMP_FILES[0]}"
echo "[PASS] Database restore completed successfully."

echo "=== All Backup & Restore Verification Checks Passed ==="
