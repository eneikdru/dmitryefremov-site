-- Seed genuine manifestos, episode, and sparks

-- Seed Canon items (manifestos)
INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at)
SELECT
    'autonomous-engineering-manifesto',
    'Autonomous Engineering Manifesto',
    'MANIFESTO',
    'AUTONOMOUS_ENGINEERING',
    'Core principles of autonomous engineering and deterministic state.',
    '# Autonomous Engineering Manifesto\n\nContent...',
    TIMESTAMP WITH TIME ZONE '2026-10-12 10:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'autonomous-engineering-manifesto'
);

-- Seed Episode
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, duration_seconds, published_at)
SELECT
    'daniel-dennett-extended-mind',
    'Daniel Dennett and the Extended Mind',
    'The mind is not confined to the brain.',
    'Discussion on Daniel Dennett and Andy Clark.',
    'https://dmitryefremov.com/media/podcast/dennett.mp3',
    'https://youtube.com/watch?v=dennett',
    3600,
    TIMESTAMP WITH TIME ZONE '2026-10-12 11:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM podcast_episodes WHERE slug = 'daniel-dennett-extended-mind'
);

-- Seed Sparks
INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at)
SELECT
    1001,
    -100123456,
    'MVP pragmatics in the AI era.',
    'PRAGMATIC_BUSINESS_STRATEGY',
    'mvp,ai',
    '@efremov_mvp',
    TIMESTAMP WITH TIME ZONE '2026-10-12 12:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 1001
);
