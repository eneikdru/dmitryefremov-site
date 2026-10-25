-- Migration V20261024235512133: Seed realistic knowledge base author concepts and applied model configuration

-- 1. Seed Applied Model Configuration and Author Concept Canon Nodes
INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'prikladnaya-konfiguraciya-modeli-avtonomii',
    'Прикладная конфигурация модели автономии и детерминированного контроля',
    'MODEL_CONFIG',
    'AUTONOMOUS_ENGINEERING',
    'Прикладная модель конфигурации автономных ИИ-агентов, определяющая модальные операторы должного O(A), допустимого P(A) и запрещённого F(A) для среды dmitrii.eneik.com.',
    '# Прикладная конфигурация модели автономии

Спецификация конфигурации детерминированной модели автономии для ресурса dmitrii.eneik.com.

## Конфигурационные параметры модели:
* **Идентичность ресурса**: dmitrii.eneik.com / Dmitry Efremov
* **Протоколы валидации**: Деонтический контроль транзакций и атомарность операций.
* **Модальные операторы**:
  * `O(A)` (Obligatory): Обязательная проверка инвариантов базы знаний перед фиксацией.
  * `P(A)` (Permitted): Чтение открытых графов понятий и вызов A2A-эндпоинтов.
  * `F(A)` (Forbidden): Несанкционированная модификация пользовательских данных и использование фиктивных плейсхолдеров.
* **A2A Инфраструктура**: llms.txt, ai-agent.json, OpenAPI v3.',
    TIMESTAMP WITH TIME ZONE '2026-10-24 12:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-24 12:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'prikladnaya-konfiguraciya-modeli-avtonomii'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'pamyat-i-graf-znaniy-dmitrii-eneik',
    'Цифровая память и граф знаний Dmitry Efremov',
    'MANIFESTO',
    'PHILOSOPHY_OF_MIND',
    'База знаний dmitrii.eneik.com как внешняя рабочая память автора по Энди Кларку.',
    '# Цифровая память и граф знаний Dmitry Efremov

Каноническая структура базы знаний Dmitry Efremov на dmitrii.eneik.com является единым смысловым графом, объединяющим манифесты, выходы подкастов, эссе и телеграм-искры.',
    TIMESTAMP WITH TIME ZONE '2026-10-24 12:30:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-24 12:30:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'pamyat-i-graf-znaniy-dmitrii-eneik'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'a2a-protokoly-i-mashinochitaemyy-kontekst',
    'A2A-протоколы и машиночитаемый контекст dmitrii.eneik.com',
    'GLOSSARY_TERM',
    'AGENTIC_ECONOMY',
    'Протоколы взаимодействия ИИ-агентов и открытые машиночитаемые интерфейсы llms.txt и ai-agent.json.',
    '# A2A-протоколы и машиночитаемый контекст

Открытые спецификации для автономного взаимодействия ИИ-агентов с платформой dmitrii.eneik.com без человеческого вмешательства.',
    TIMESTAMP WITH TIME ZONE '2026-10-24 13:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-24 13:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'a2a-protokoly-i-mashinochitaemyy-kontekst'
);

-- 2. Seed Canon Cross References for Model Configuration and Concepts
INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id,
    t.id,
    'EXTENDS',
    'Прикладная конфигурация модели расширяет деонтическую автономию агентов нормативными правилами.'
FROM canon_items s, canon_items t
WHERE s.slug = 'prikladnaya-konfiguraciya-modeli-avtonomii'
  AND t.slug = 'deonticheskaya-avtonomiya-agentov'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references xr WHERE xr.source_id = s.id AND xr.target_id = t.id AND xr.relationship_type = 'EXTENDS'
  );

INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id,
    t.id,
    'DEPENDS_ON',
    'Прикладная конфигурация модели опирается на цифровую память и граф знаний dmitrii.eneik.com.'
FROM canon_items s, canon_items t
WHERE s.slug = 'prikladnaya-konfiguraciya-modeli-avtonomii'
  AND t.slug = 'pamyat-i-graf-znaniy-dmitrii-eneik'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references xr WHERE xr.source_id = s.id AND xr.target_id = t.id AND xr.relationship_type = 'DEPENDS_ON'
  );

INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id,
    t.id,
    'REFERENCES',
    'Граф знаний dmitrii.eneik.com ссылается на A2A-протоколы и машиночитаемый контекст.'
FROM canon_items s, canon_items t
WHERE s.slug = 'pamyat-i-graf-znaniy-dmitrii-eneik'
  AND t.slug = 'a2a-protokoly-i-mashinochitaemyy-kontekst'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references xr WHERE xr.source_id = s.id AND xr.target_id = t.id AND xr.relationship_type = 'REFERENCES'
  );

-- 3. Seed Concept Media Links
INSERT INTO concept_media_links (canon_item_id, media_type, podcast_episode_id, relationship_type, description)
SELECT c.id, 'PODCAST', p.id, 'EXPLAINS', 'Объяснение прикладной конфигурации модели автономии в подкасте'
FROM canon_items c, podcast_episodes p
WHERE c.slug = 'prikladnaya-konfiguraciya-modeli-avtonomii'
  AND p.slug = 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura'
  AND NOT EXISTS (
      SELECT 1 FROM concept_media_links l
      WHERE l.canon_item_id = c.id AND l.podcast_episode_id = p.id
  );

INSERT INTO concept_media_links (canon_item_id, media_type, essay_id, relationship_type, description)
SELECT c.id, 'ESSAY', e.id, 'ELABORATES', 'Эссе, раскрывающее концепцию внешней рабочей памяти и графа знаний'
FROM canon_items c, essays e
WHERE c.slug = 'pamyat-i-graf-znaniy-dmitrii-eneik'
  AND e.slug = 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy'
  AND NOT EXISTS (
      SELECT 1 FROM concept_media_links l
      WHERE l.canon_item_id = c.id AND l.essay_id = e.id
  );
