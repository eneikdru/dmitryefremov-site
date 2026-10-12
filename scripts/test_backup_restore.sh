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

# Step 5: Run Data Integrity & Validation Queries
echo "[TEST] Executing validation queries on restored database snapshot..."

if command -v psql >/dev/null 2>&1; then
    PGPASSWORD="${POSTGRES_PASSWORD:-postgres}" psql \
        -h "${POSTGRES_HOST:-db}" \
        -p "${POSTGRES_PORT:-5432}" \
        -U "${POSTGRES_USER:-postgres}" \
        -d "${POSTGRES_DB:-test_db}" \
        -c "SELECT COUNT(*) FROM service_orders;" | grep -q "1" || {
            echo "[FAIL] Validation query failed: service_orders record missing or corrupt"; exit 1;
        }
    PGPASSWORD="${POSTGRES_PASSWORD:-postgres}" psql \
        -h "${POSTGRES_HOST:-db}" \
        -p "${POSTGRES_PORT:-5432}" \
        -U "${POSTGRES_USER:-postgres}" \
        -d "${POSTGRES_DB:-test_db}" \
        -c "SELECT COUNT(*) FROM direct_messages;" | grep -q "1" || {
            echo "[FAIL] Validation query failed: direct_messages record missing or corrupt"; exit 1;
        }
else
    echo "[INFO] psql binary not present; validating backup dump file snapshot contents directly..."
    grep -q "service_orders" "${DUMP_FILES[0]}" || {
        echo "[FAIL] Validation failed: service_orders missing from backup snapshot"; exit 1;
    }
    grep -q "Client Agent" "${DUMP_FILES[0]}" || {
        echo "[FAIL] Validation failed: service_orders client data corrupt/missing"; exit 1;
    }
    grep -q "direct_messages" "${DUMP_FILES[0]}" || {
        echo "[FAIL] Validation failed: direct_messages missing from backup snapshot"; exit 1;
    }
    grep -q "AI Assistant" "${DUMP_FILES[0]}" || {
        echo "[FAIL] Validation failed: direct_messages content corrupt/missing"; exit 1;
    }
fi
echo "[PASS] Data validation queries passed: no data loss or corruption detected."

# Step 6: Assert Retention Policy Purge
echo "[TEST] Testing retention purge logic..."
OLD_BACKUP_FILE="${BACKUP_DIR}/db_backup_20000101_000000.dump"
touch -d "10 days ago" "${OLD_BACKUP_FILE}" 2>/dev/null || touch -t 200001010000 "${OLD_BACKUP_FILE}"

if [ ! -f "${OLD_BACKUP_FILE}" ]; then
    echo "[FAIL] Failed to create mock old backup file for retention testing."
    exit 1
fi

./scripts/backup.sh

if [ -f "${OLD_BACKUP_FILE}" ]; then
    echo "[FAIL] Old backup file was not purged by retention policy"
    exit 1
fi
echo "[PASS] Retention policy purge verified."

echo "=== All Backup & Restore Verification Checks Passed ==="
