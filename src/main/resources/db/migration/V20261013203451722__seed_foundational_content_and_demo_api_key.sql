-- Seed demo API key for external AI agent integration and ensure foundational content

INSERT INTO api_credentials (client_id, api_key_hash, role, enabled)
SELECT
    'demo-api-key',
    '$2a$10$e8R6S8k.K/y1dM2K1N8u3eOaK0gO6aH6Z/0v.5M2lO5aG/1R2S3T4',
    'DEMO_AGENT',
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM api_credentials WHERE client_id = 'demo-api-key'
);

INSERT INTO agent_api_tokens (agent_name, token_hash)
SELECT
    'demo-agent',
    '$2a$10$e8R6S8k.K/y1dM2K1N8u3eOaK0gO6aH6Z/0v.5M2lO5aG/1R2S3T4'
WHERE NOT EXISTS (
    SELECT 1 FROM agent_api_tokens WHERE agent_name = 'demo-agent'
);

-- Seed foundational podcast episode with audio url if missing
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, duration_seconds, published_at)
SELECT
    'foundational-scrollytelling-ep5',
    'Системы и реальность #5: Фундаментальные инварианты и scrollytelling',
    'Разбор архитектуры High-End Scrollytelling, абсолютной достоверности материалов и чистого звука подкаста.',
    'В пятом выпуске подкаста «Системы и реальность» Дмитрий Ефремов объясняет принципы создания автономной A2A-инфраструктуры и работы с материалами.',
    'https://dmitryefremov.com/media/podcast/ep5.mp3',
    'https://youtube.com/@efremov_dmitriy/videos/ep5',
    2880,
    TIMESTAMP WITH TIME ZONE '2026-10-13 10:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM podcast_episodes WHERE slug = 'foundational-scrollytelling-ep5'
);

-- Seed timecode for Ep 5
INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    0,
    'Введение в High-End Scrollytelling',
    'Философия чистого звука и достоверности материалов',
    1
FROM podcast_episodes e
WHERE e.slug = 'foundational-scrollytelling-ep5'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 0
  );
