-- Seed Curated Standalone Essays into published_essays table

INSERT INTO published_essays (slug, title, summary, content, published_at, curated_by)
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
    TIMESTAMP WITH TIME ZONE '2026-10-14 10:00:00+00',
    'Dmitry Efremov'
WHERE NOT EXISTS (
    SELECT 1 FROM published_essays WHERE slug = 'quantified-modal-logic-and-essential-invariants'
);

INSERT INTO published_essays (slug, title, summary, content, published_at, curated_by)
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
    TIMESTAMP WITH TIME ZONE '2026-10-14 14:00:00+00',
    'Dmitry Efremov'
WHERE NOT EXISTS (
    SELECT 1 FROM published_essays WHERE slug = 'explanatory-coherence-and-agentic-architecture'
);

INSERT INTO published_essays (slug, title, summary, content, published_at, curated_by)
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
    TIMESTAMP WITH TIME ZONE '2026-10-15 09:00:00+00',
    'Dmitry Efremov'
WHERE NOT EXISTS (
    SELECT 1 FROM published_essays WHERE slug = 'consciousness-in-action-and-embodied-agents'
);
