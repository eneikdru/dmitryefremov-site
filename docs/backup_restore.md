# Database Backup and Restore Infrastructure

## Overview
This document details the PostgreSQL automated backup and manual restore operations for the personal site database (`dmitryefremov-site`).

## Architecture & Configuration
- **Backup Script**: `scripts/backup.sh`
- **Restore Script**: `scripts/restore.sh`
- **Docker Service**: `docker-compose.backup.yml`
- **Default Schedule**: Daily at 02:00 UTC (`0 2 * * *`)
- **Retention Policy**: 7 days (older archives automatically purged)
- **Security Invariant**: Archives created with strict file permissions (`chmod 600`), output directory `chmod 700`.

## Daily Automated Schedule
The `db-backup` service in `docker-compose.backup.yml` runs containerized `pg_dump` on the defined `CRON_SCHEDULE`.

To start the backup container:
```bash
docker compose -f docker-compose.backup.yml up -d
```

## Manual Backup Trigger
To run an immediate database dump:
```bash
./scripts/backup.sh
```
Environment variables can override defaults:
- `POSTGRES_HOST` (default: `db`)
- `POSTGRES_PORT` (default: `5432`)
- `POSTGRES_DB` (default: `dmitryefremov`)
- `POSTGRES_USER` (default: `postgres`)
- `BACKUP_DIR` (default: `./backups`)
- `RETENTION_DAYS` (default: `7`)

## Manual Restore Trigger
To restore database state from a backup archive:
```bash
./scripts/restore.sh ./backups/db_backup_20261009_020000.dump
```

## Local Verification
Run the verification script to test backup dump generation, permission checks, and restore execution:
```bash
./scripts/test_backup_restore.sh
```
