-- Seed additional manifestos, an episode, and sparks as per acceptance criteria

-- 1. Seed Manifestos into canon_items
INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'gilbert-ryle-category-mistake',
    'Gilbert Ryle and the Category Mistake',
    'MANIFESTO',
    'PHILOSOPHY_OF_MIND',
    'The concept of the mind as a category mistake and its implications for autonomous systems.',
    '# Gilbert Ryle and the Category Mistake

In *The Concept of Mind*, Gilbert Ryle argued that the mind is not an independent entity inhabiting the body (the "ghost in the machine"). It is a category mistake to treat the mind as a substance. In autonomous systems, this maps to avoiding the treatment of "intelligence" as a separate, magical component rather than emergent from deterministic, observable actions and state transitions.',
    TIMESTAMP WITH TIME ZONE '2026-10-15 12:00:00+00',
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'gilbert-ryle-category-mistake'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'daniel-dennett-intentional-stance',
    'Daniel Dennett and the Intentional Stance',
    'MANIFESTO',
    'PHILOSOPHY_OF_MIND',
    'Adopting the intentional stance to understand complex autonomous behavior.',
    '# Daniel Dennett and the Intentional Stance

Daniel Dennett''s intentional stance proposes that we can understand complex systems by treating them as rational agents with beliefs and desires. For autonomous agents in the agentic economy, this stance provides a pragmatic interface for predicting behavior, independent of the internal implementation details of the agent''s logic.',
    TIMESTAMP WITH TIME ZONE '2026-10-16 12:00:00+00',
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'daniel-dennett-intentional-stance'
);

-- 2. Seed an Episode into podcast_episodes
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, duration_seconds, published_at)
SELECT
    'episode-06-ghost-in-the-machine',
    'Episode 06: Gilbert Ryle and the Ghost in the Machine',
    'Applying Ryle''s category mistake to modern AI architecture and the myth of the black box mind.',
    'In Episode 06, Dmitry Efremov discusses Gilbert Ryle''s *The Concept of Mind* and how the persistent myth of the "ghost in the machine" clouds our understanding of autonomous software agents.',
    'https://dmitryefremov.com/media/podcast/ep06-ghost-in-the-machine.mp3',
    'https://youtube.com/@efremov_dmitriy/videos/ep06-ghost-in-the-machine',
    2800,
    TIMESTAMP WITH TIME ZONE '2026-10-16 14:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM podcast_episodes WHERE slug = 'episode-06-ghost-in-the-machine'
);

-- Seed Timecodes for the new Episode
INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    0,
    'Introduction: The Category Mistake',
    'Defining the category mistake and its historical context.',
    1
FROM podcast_episodes e
WHERE e.slug = 'episode-06-ghost-in-the-machine'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 0
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    900,
    'AI and the Ghost in the Machine',
    'Why treating LLMs as "minds" is a category mistake.',
    2
FROM podcast_episodes e
WHERE e.slug = 'episode-06-ghost-in-the-machine'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 900
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    1800,
    'Deterministic Actions as Intelligence',
    'Building reliable systems without relying on magical thinking.',
    3
FROM podcast_episodes e
WHERE e.slug = 'episode-06-ghost-in-the-machine'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 1800
  );

-- 3. Seed Sample Sparks
INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at, created_at)
SELECT
    8001,
    -100999888777,
    'MVP Pragmatics: Ship the critical path, discard the noise. If it doesn''t serve the invariant, it''s technical debt before it''s even deployed. #mvp #architecture',
    'Pragmatic Business Strategy',
    'mvp,architecture',
    '@efremov_mvp',
    TIMESTAMP WITH TIME ZONE '2026-10-14 09:00:00+00',
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 8001
);

INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at, created_at)
SELECT
    8002,
    -100999888777,
    'Autonomous engineering requires a philosophical foundation. You aren''t just writing code; you are defining the ontological boundaries of a synthetic agent. #philosophy #engineering',
    'Autonomous Engineering',
    'philosophy,engineering',
    '@efremov_mvp',
    TIMESTAMP WITH TIME ZONE '2026-10-15 15:30:00+00',
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 8002
);

INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at, created_at)
SELECT
    8003,
    -100999888777,
    'Business survival in the AI era demands decoupling from vendor lock-in. Build sovereign data pipelines and own your semantic index. #business #sovereignty',
    'Pragmatic Business Strategy',
    'business,sovereignty',
    '@efremov_mvp',
    TIMESTAMP WITH TIME ZONE '2026-10-16 10:45:00+00',
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 8003
);
