-- Migration to add metadata column to podcast_episodes table and update episodes view
ALTER TABLE podcast_episodes ADD COLUMN IF NOT EXISTS metadata TEXT;

-- See the earlier concept_links migration: adding a column in the middle of
-- a view projection requires dropping and recreating the view on PostgreSQL.
DROP VIEW IF EXISTS episodes;

CREATE OR REPLACE VIEW episodes AS
SELECT
    id,
    slug,
    title,
    thesis,
    description,
    audio_url,
    media_link,
    youtube_url,
    telegram_url,
    spotify_url,
    apple_podcasts_url,
    platforms,
    concept_links,
    metadata,
    duration_seconds,
    published_at,
    created_at
FROM podcast_episodes;
