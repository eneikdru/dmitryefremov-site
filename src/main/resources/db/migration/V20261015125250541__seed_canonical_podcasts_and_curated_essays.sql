-- Flyway migration V20261015125250541: Seed canonical podcasts and curated essays

-- 1. Seed Canonical Podcast Episode 01: Ruth Barcan Marcus
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, duration_seconds, published_at)
SELECT
    'episode-01-ruth-barcan-marcus',
    'Episode 01: Ruth Barcan Marcus — Quantified Modal Logic and Essentialism',
    'Analysis of modal logic, the Barcan Formula, direct reference theory, and essentialism in formal philosophy.',
    'In Episode 01, Dmitry Efremov discusses Ruth Barcan Marcus seminal contributions to quantified modal logic, the identity of individuals across possible worlds, and direct reference.',
    'https://dmitryefremov.com/media/podcast/ep01-ruth-barcan-marcus.mp3',
    'https://youtube.com/@efremov_dmitriy/videos/ep1-ruth-barcan-marcus',
    2880,
    TIMESTAMP WITH TIME ZONE '2026-10-01 12:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM podcast_episodes WHERE slug = 'episode-01-ruth-barcan-marcus'
);

-- Seed Timecodes for Episode 01
INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    0,
    'Introduction: Ruth Barcan Marcus & Modal Logic',
    'Overview of modal logic and formalizing necessity',
    1
FROM podcast_episodes e
WHERE e.slug = 'episode-01-ruth-barcan-marcus'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 0
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    600,
    'The Barcan Formula',
    'Possibility, necessity, and domain quantification',
    2
FROM podcast_episodes e
WHERE e.slug = 'episode-01-ruth-barcan-marcus'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 600
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    1800,
    'Direct Reference and Modality',
    'Rigid designators and essential properties',
    3
FROM podcast_episodes e
WHERE e.slug = 'episode-01-ruth-barcan-marcus'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 1800
  );

-- 2. Seed Canonical Podcast Episode 02: Paul Thagard
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, duration_seconds, published_at)
SELECT
    'episode-02-paul-thagard',
    'Episode 02: Paul Thagard — Cognitive Science and Conceptual Coherence',
    'Computational philosophy of mind, explanatory coherence, and neural network models of cognition.',
    'In Episode 02, Dmitry Efremov explores Paul Thagard theory of explanatory coherence, cognitive architecture, and computational decision-making.',
    'https://dmitryefremov.com/media/podcast/ep02-paul-thagard.mp3',
    'https://youtube.com/@efremov_dmitriy/videos/ep2-paul-thagard',
    3120,
    TIMESTAMP WITH TIME ZONE '2026-10-03 12:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM podcast_episodes WHERE slug = 'episode-02-paul-thagard'
);

-- Seed Timecodes for Episode 02
INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    0,
    'Introduction: Paul Thagard & Cognitive Science',
    'Explanatory coherence and mental representations',
    1
FROM podcast_episodes e
WHERE e.slug = 'episode-02-paul-thagard'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 0
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    720,
    'Computational Philosophy of Mind',
    'Connectionist networks and constraint satisfaction',
    2
FROM podcast_episodes e
WHERE e.slug = 'episode-02-paul-thagard'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 720
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    1920,
    'Coherence in Decision Making',
    'Evaluating hypotheses in complex cognitive architectures',
    3
FROM podcast_episodes e
WHERE e.slug = 'episode-02-paul-thagard'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 1920
  );

-- 3. Seed Canonical Podcast Episode 03: Susan Hurley
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, duration_seconds, published_at)
SELECT
    'episode-03-susan-hurley',
    'Episode 03: Susan Hurley — Consciousness in Action and Animal Agency',
    'Perception-action loops, two-way feedback, shared circuits, and embodied cognitive architecture.',
    'In Episode 03, Dmitry Efremov examines Susan Hurley groundbreaking work on action, perception, non-conceptual agency, and animal cognition.',
    'https://dmitryefremov.com/media/podcast/ep03-susan-hurley.mp3',
    'https://youtube.com/@efremov_dmitriy/videos/ep03-susan-hurley',
    3360,
    TIMESTAMP WITH TIME ZONE '2026-10-05 12:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM podcast_episodes WHERE slug = 'episode-03-susan-hurley'
);

-- Seed Timecodes for Episode 03
INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    0,
    'Introduction: Susan Hurley & Action Theory',
    'Critique of the classical input-output sandwich',
    1
FROM podcast_episodes e
WHERE e.slug = 'episode-03-susan-hurley'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 0
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    840,
    'Shared Circuits and Social Cognition',
    'Imitation, action specification, and mirror mechanisms',
    2
FROM podcast_episodes e
WHERE e.slug = 'episode-03-susan-hurley'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 840
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    2100,
    'Embodied Cognition and Agency',
    'Non-conceptual agency in biological and autonomous systems',
    3
FROM podcast_episodes e
WHERE e.slug = 'episode-03-susan-hurley'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 2100
  );

-- 4. Seed Curated Standalone Essays into essays table
INSERT INTO essays (slug, title, summary, content, published_at)
SELECT
    'quantified-modal-logic-and-essential-invariants',
    'Quantified Modal Logic and Essential Invariants in Autonomous Systems',
    'An examination of Ruth Barcan Marcus modal logic applied to modern system engineering, domain invariants, and strict state boundary proofs.',
    '# Quantified Modal Logic and Essential Invariants in Autonomous Systems

## Executive Summary

The transition from classical logic to modal logic provides a powerful framework for reasoning about necessity and possibility. In autonomous system architecture, modal operators allow us to formalize state constraints and prove invariant safety across concurrent environments.

### Key Conceptual Takeaways
1. **The Barcan Formula in System Design**: Quantifying over possible states requires fixed domain semantics.
2. **Rigid Designators for Entity Identity**: Maintaining immutable references across distributed state transitions prevents identity drift.
3. **Deontic and Modal Safety Rules**: System states must satisfy necessity conditions before committing state transitions.',
    TIMESTAMP WITH TIME ZONE '2026-10-14 10:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM essays WHERE slug = 'quantified-modal-logic-and-essential-invariants'
);

INSERT INTO essays (slug, title, summary, content, published_at)
SELECT
    'explanatory-coherence-and-agentic-architecture',
    'Explanatory Coherence and Computational Architectures for AI Agents',
    'Curated analysis of Paul Thagard explanatory coherence model for structuring multi-agent reasoning and belief alignment.',
    '# Explanatory Coherence and Computational Architectures for AI Agents

## Executive Summary

Cognitive systems rely on coherence criteria to evaluate competing hypotheses. Paul Thagard theory of explanatory coherence models belief networks through connectionist constraint satisfaction.

### Key Conceptual Takeaways
1. **Constraint Satisfaction in Multi-Agent Systems**: Agents align internal states by minimizing contradiction and maximizing explanatory coherence.
2. **Symmetrical and Asymmetrical Coherence Relations**: Evidence nodes strengthen dependent hypotheses through weighted graph activation.
3. **Pragmatic Business & AI Decision Making**: Eliminating noisy or contradictory signals ensures resilient decision-making in high-throughput environments.',
    TIMESTAMP WITH TIME ZONE '2026-10-14 14:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM essays WHERE slug = 'explanatory-coherence-and-agentic-architecture'
);

INSERT INTO essays (slug, title, summary, content, published_at)
SELECT
    'consciousness-in-action-and-embodied-agents',
    'Consciousness in Action: Perception-Action Loops in Autonomous Agents',
    'Curated executive summary on Susan Hurley action-perception theory and its application to embodied AI agent design.',
    '# Consciousness in Action: Perception-Action Loops in Autonomous Agents

## Executive Summary

Susan Hurley challenged the traditional input-output sandwich view of cognition, proving that perception and action are dynamically interdependent in feedback loops.

### Key Conceptual Takeaways
1. **Dynamical Perception-Action Loops**: Autonomous agents perceive their environment through continuous action and feedback rather than passive sensory processing.
2. **Shared Circuit Architectures**: Multi-layered motor control and prediction networks allow agents to simulate actions before execution.
3. **Embodied Engineering Invariants**: System boundaries must account for two-way feedback mechanisms between perception, action, and physical environments.',
    TIMESTAMP WITH TIME ZONE '2026-10-15 09:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM essays WHERE slug = 'consciousness-in-action-and-embodied-agents'
);

-- 5. Seed Curated Essays into articles table as well for backwards compatibility
INSERT INTO articles (slug, title, summary, content, published_at)
SELECT
    'quantified-modal-logic-and-essential-invariants',
    'Quantified Modal Logic and Essential Invariants in Autonomous Systems',
    'An examination of Ruth Barcan Marcus modal logic applied to modern system engineering, domain invariants, and strict state boundary proofs.',
    '# Quantified Modal Logic and Essential Invariants in Autonomous Systems

## Executive Summary

The transition from classical logic to modal logic provides a powerful framework for reasoning about necessity and possibility. In autonomous system architecture, modal operators allow us to formalize state constraints and prove invariant safety across concurrent environments.

### Key Conceptual Takeaways
1. **The Barcan Formula in System Design**: Quantifying over possible states requires fixed domain semantics.
2. **Rigid Designators for Entity Identity**: Maintaining immutable references across distributed state transitions prevents identity drift.
3. **Deontic and Modal Safety Rules**: System states must satisfy necessity conditions before committing state transitions.',
    TIMESTAMP WITH TIME ZONE '2026-10-14 10:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM articles WHERE slug = 'quantified-modal-logic-and-essential-invariants'
);

INSERT INTO articles (slug, title, summary, content, published_at)
SELECT
    'explanatory-coherence-and-agentic-architecture',
    'Explanatory Coherence and Computational Architectures for AI Agents',
    'Curated analysis of Paul Thagard explanatory coherence model for structuring multi-agent reasoning and belief alignment.',
    '# Explanatory Coherence and Computational Architectures for AI Agents

## Executive Summary

Cognitive systems rely on coherence criteria to evaluate competing hypotheses. Paul Thagard theory of explanatory coherence models belief networks through connectionist constraint satisfaction.

### Key Conceptual Takeaways
1. **Constraint Satisfaction in Multi-Agent Systems**: Agents align internal states by minimizing contradiction and maximizing explanatory coherence.
2. **Symmetrical and Asymmetrical Coherence Relations**: Evidence nodes strengthen dependent hypotheses through weighted graph activation.
3. **Pragmatic Business & AI Decision Making**: Eliminating noisy or contradictory signals ensures resilient decision-making in high-throughput environments.',
    TIMESTAMP WITH TIME ZONE '2026-10-14 14:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM articles WHERE slug = 'explanatory-coherence-and-agentic-architecture'
);

INSERT INTO articles (slug, title, summary, content, published_at)
SELECT
    'consciousness-in-action-and-embodied-agents',
    'Consciousness in Action: Perception-Action Loops in Autonomous Agents',
    'Curated executive summary on Susan Hurley action-perception theory and its application to embodied AI agent design.',
    '# Consciousness in Action: Perception-Action Loops in Autonomous Agents

## Executive Summary

Susan Hurley challenged the traditional input-output sandwich view of cognition, proving that perception and action are dynamically interdependent in feedback loops.

### Key Conceptual Takeaways
1. **Dynamical Perception-Action Loops**: Autonomous agents perceive their environment through continuous action and feedback rather than passive sensory processing.
2. **Shared Circuit Architectures**: Multi-layered motor control and prediction networks allow agents to simulate actions before execution.
3. **Embodied Engineering Invariants**: System boundaries must account for two-way feedback mechanisms between perception, action, and physical environments.',
    TIMESTAMP WITH TIME ZONE '2026-10-15 09:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM articles WHERE slug = 'consciousness-in-action-and-embodied-agents'
);
