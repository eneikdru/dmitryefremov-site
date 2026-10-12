-- Migration to expand podcast episodes schema with media URLs and platform attributes
ALTER TABLE podcast_episodes ADD COLUMN IF NOT EXISTS youtube_url VARCHAR(1024);
ALTER TABLE podcast_episodes ADD COLUMN IF NOT EXISTS telegram_url VARCHAR(1024);
ALTER TABLE podcast_episodes ADD COLUMN IF NOT EXISTS spotify_url VARCHAR(1024);
ALTER TABLE podcast_episodes ADD COLUMN IF NOT EXISTS apple_podcasts_url VARCHAR(1024);
ALTER TABLE podcast_episodes ADD COLUMN IF NOT EXISTS platforms TEXT;

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
    duration_seconds,
    published_at,
    created_at
FROM podcast_episodes;
