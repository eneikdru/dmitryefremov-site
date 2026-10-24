-- Flyway migration V20261024100401767: Seed authentic Russian-language Canon nodes, cross-references, and Sparks

-- 1. Seed Russian Canon items
INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'deontika-avtonomnyh-sistem',
    'Деонтика и нормативные инварианты автономных систем',
    'MANIFESTO',
    'AUTONOMOUS_ENGINEERING',
    'Формализация должного поведения программных агентов через деонтическую логику и декларативные ограничения.',
    '# Деонтика и нормативные инварианты автономных систем\n\nВ автономных системах действия агентов определяются через деонтические операторы: обязательство (Obligatory), разрешение (Permitted) и запрет (Forbidden). Каждое действие агента или изменение состояния обязано удовлетворять нормативным инвариантам до фиксации в хранилище.',
    TIMESTAMP WITH TIME ZONE '2026-10-16 10:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-16 10:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'deontika-avtonomnyh-sistem'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'rasshirennyy-razum-i-vneshnyaya-pamyat',
    'Расширенный разум и внешняя рабочая память',
    'GLOSSARY_TERM',
    'PHILOSOPHY_OF_MIND',
    'Концепция Энди Кларка о выносе когнитивной нагрузки на внешние цифровые артефакты и граф знаний.',
    '# Расширенный разум и внешняя рабочая память\n\nСогласно гипотезе расширенного разума (Andy Clark & David Chalmers), когнитивные процессы человека и агентов не ограничены физической оболочкой. Внешние базы знаний, графы понятий и структурированные хранилища выступают непосредственным продолжением мышления.',
    TIMESTAMP WITH TIME ZONE '2026-10-16 11:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-16 11:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'rasshirennyy-razum-i-vneshnyaya-pamyat'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'vzaimodejstvie-avtonomnyh-agentov-a2a',
    'Протоколы A2A и экономика автономных агентов',
    'MANIFESTO',
    'AGENTIC_ECONOMY',
    'Принципы прямого взаимодействия и транзакций между ИИ-агентами без человеческого посредничества.',
    '# Протоколы A2A и экономика автономных агентов\n\nВ агентной экономике взаимодействия между автономными узлами строятся на базе строгих машиночитаемых контрактов, криптографических токенов доступа и гарантированных SLA взаимодействия.',
    TIMESTAMP WITH TIME ZONE '2026-10-16 12:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-16 12:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'vzaimodejstvie-avtonomnyh-agentov-a2a'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'dinamicheskij-kanon-i-ontologiya',
    'Динамический Канон и граф онтологий',
    'ONTOLOGY',
    'PRAGMATIC_BUSINESS_STRATEGY',
    'Единое ядро знаний, связывающее концепты, подкасты, эссе и короткие заметки в связанный граф.',
    '# Динамический Канон и граф онтологий\n\nКанон объединяет фрагментированные мысли и медиа-выпуски в живую базу знаний с двунаправленными перекрёстными ссылками.',
    TIMESTAMP WITH TIME ZONE '2026-10-16 13:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-16 13:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'dinamicheskij-kanon-i-ontologiya'
);

-- 2. Seed cross-references between Russian Canon nodes
INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id, t.id, 'EXTENDS', 'Деонтическая архитектура расширяет концепцию внешней памяти нормативными правилами.'
FROM canon_items s, canon_items t
WHERE s.slug = 'deontika-avtonomnyh-sistem'
  AND t.slug = 'rasshirennyy-razum-i-vneshnyaya-pamyat'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references WHERE source_id = s.id AND target_id = t.id AND relationship_type = 'EXTENDS'
  );

INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id, t.id, 'REFERENCES', 'Онтология Канона ссылается на деонтические инварианты систем.'
FROM canon_items s, canon_items t
WHERE s.slug = 'dinamicheskij-kanon-i-ontologiya'
  AND t.slug = 'deontika-avtonomnyh-sistem'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references WHERE source_id = s.id AND target_id = t.id AND relationship_type = 'REFERENCES'
  );

INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id, t.id, 'DEPENDS_ON', 'Агентные экономические протоколы опираются на деонтические правила.'
FROM canon_items s, canon_items t
WHERE s.slug = 'vzaimodejstvie-avtonomnyh-agentov-a2a'
  AND t.slug = 'deontika-avtonomnyh-sistem'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references WHERE source_id = s.id AND target_id = t.id AND relationship_type = 'DEPENDS_ON'
  );

-- 3. Seed initial authentic Russian Sparks
INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at, created_at)
SELECT
    9001,
    -100123456789,
    'Сайт как внешняя рабочая память — это не просто красивый UI, а непрерывный контур мышления. Каждая искра из Telegram сразу ложится в единый граф понятий Канона.',
    'PHILOSOPHY_OF_MIND',
    '#extended_mind #sparks #canon',
    '@efremov_mvp',
    TIMESTAMP WITH TIME ZONE '2026-10-17 09:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-17 09:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 9001
);

INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at, created_at)
SELECT
    9002,
    -100123456789,
    'Формализация нормативных ограничений в код (O, P, F) исключает невалидные переходы состояний еще на этапе валидации Flyway-миграций.',
    'AUTONOMOUS_ENGINEERING',
    '#deontic #invariants #architecture',
    '@efremov_mvp',
    TIMESTAMP WITH TIME ZONE '2026-10-17 10:30:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-17 10:30:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 9002
);

INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at, created_at)
SELECT
    9003,
    -100123456789,
    'Автономные агенты в A2A экосистеме обмениваются не просто текстом, а машиночитаемыми выжимками (llms.txt / JSON) с жесткой семантикой связей.',
    'AGENTIC_ECONOMY',
    '#a2a #agentic_economy #protocols',
    '@efremov_mvp',
    TIMESTAMP WITH TIME ZONE '2026-10-17 12:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-17 12:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 9003
);
