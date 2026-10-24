-- Undo Migration: U20261009115809541
-- Manual Disaster Recovery Instructions:
-- Note: Flyway Community Edition does not automatically run undo scripts.
-- If rollback is required in emergency disaster recovery, execute the following statements manually:

DROP INDEX IF EXISTS idx_publication_schedules_status;
DROP INDEX IF EXISTS idx_publication_schedules_platform;
DROP TABLE IF EXISTS publication_schedules;
