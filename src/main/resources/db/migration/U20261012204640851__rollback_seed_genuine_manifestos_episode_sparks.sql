-- Manual rollback procedure for V20261012204640851
-- Warning: This migration performed destructive DELETE operations to replace placeholder data.
-- Since the exact prior state was not preserved in a temp table, reversing this migration requires
-- manually re-running the earlier seed migrations (e.g., V20261009115824162, V20261012183303520)
-- or restoring from a database snapshot taken prior to this migration.

DELETE FROM canon_cross_references;
DELETE FROM canon_items;
DELETE FROM podcast_quotes;
DELETE FROM podcast_timecodes;
DELETE FROM podcast_episodes;
DELETE FROM sparks;
DELETE FROM articles;
DELETE FROM publication_schedules;
