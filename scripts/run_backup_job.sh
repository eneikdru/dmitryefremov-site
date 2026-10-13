#!/usr/bin/env bash
set -euo pipefail

# Helper Entrypoint for Scheduled Automated Database Backup Execution
# Role: BARCAN-TAG-05 (DevOps)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKUP_SCRIPT="${SCRIPT_DIR}/backup.sh"

if [ ! -f "${BACKUP_SCRIPT}" ]; then
    echo "[ERROR] Backup script not found at ${BACKUP_SCRIPT}" >&2
    exit 1
fi

chmod +x "${BACKUP_SCRIPT}"

echo "[INFO] Invoking automated database backup job..."
exec "${BACKUP_SCRIPT}" "$@"
