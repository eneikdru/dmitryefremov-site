-- Seed authentic Russian-language canon nodes, essays, podcasts, timecodes, quotes, and sparks

-- 1. Seed Canon Items
INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'rasshirennyy-razum-i-vneshnyaya-pamyat',
    'Концепция расширенного разума и внешней рабочей памяти',
    'MANIFESTO',
    'PHILOSOPHY_OF_MIND',
    'Использование цифровой инфраструктуры как непосредственного продолжения человеческой когнитивной системы по Энди Кларку.',
    '# Концепция расширенного разума и внешней рабочей памяти

Цифровая среда, персональная база знаний и графовые связи являются не просто внешними хранилищами, а полноценными элементами расширенной когнитивной архитектуры (Extended Mind Thesis).

## Ключевые принципы:
1. **Прозрачность интеграции**: Инструмент становится частью когнитивного процесса, когда обращение к нему не требует осознанных накладных расходов.
2. **Графовая связность**: Идеи образуют семантическую сеть, отражающую структуру мысли автора.
3. **Бесшовный вывод**: Выпуски подкаста, эссе и искры из Telegram переплетены с понятийным ядром Канона.',
    TIMESTAMP WITH TIME ZONE '2026-10-18 10:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-18 10:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'rasshirennyy-razum-i-vneshnyaya-pamyat'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'deonticheskaya-avtonomiya-agentov',
    'Деонтическая автономия ИИ-агентов',
    'MANIFESTO',
    'AUTONOMOUS_ENGINEERING',
    'Формализация обязательств, разрешений и запретов при выполнении автономных действий агентами.',
    '# Деонтическая автономия ИИ-агентов

Поведение автономного агента определяется модальной логикой обязанностей O(A), разрешений P(A) и запретов F(A), предотвращая непредвиденные боковые эффекты в распределенной среде.

## Нормативные инварианты:
* **Obligatory (Обязательно)**: Валидация состояния и целостности графа до фиксации записи.
* **Permitted (Разрешено)**: Повторная обработка идемпотентных запросов и чтение открытого графа.
* **Forbidden (Запрещено)**: Несанкционированная модификация пользовательских данных и нарушение изоляции сырых суфлёров.',
    TIMESTAMP WITH TIME ZONE '2026-10-19 12:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-19 12:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'deonticheskaya-avtonomiya-agentov'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'agent-to-agent-ekonomika',
    'Экономика взаимодействия агентов (Agent-to-Agent)',
    'MANIFESTO',
    'AGENTIC_ECONOMY',
    'Прямые микротранзакции, обмен ценностью и автоматическое исполнение контрактов между ИИ-агентами.',
    '# Экономика взаимодействия агентов (Agent-to-Agent)

В агентной экономике автономные программные системы взаимодействуют напрямую через машиночитаемые представления (llms.txt, ai-agent.json, OpenAPI) и согласованные микропротоколы.',
    TIMESTAMP WITH TIME ZONE '2026-10-20 14:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-20 14:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'agent-to-agent-ekonomika'
);

INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at, created_at)
SELECT
    'pragmaticheskaya-arhitektura-myshleniya',
    'Прагматическая архитектура мышления и инженерная сдержанность',
    'GLOSSARY_TERM',
    'PRAGMATIC_BUSINESS_STRATEGY',
    'Принцип отсечения информационного шума и фокусировка на реальной ценности и проверяемых инвариантах.',
    '# Прагматическая архитектура мышления

Инженерная сдержанность предполагает отказ от визуального шума и фиктивных метрик в пользу детерминированных контрактов и проверенных данных.',
    TIMESTAMP WITH TIME ZONE '2026-10-21 09:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-21 09:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM canon_items WHERE slug = 'pragmaticheskaya-arhitektura-myshleniya'
);

-- 2. Seed Canon Cross References
INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id,
    t.id,
    'EXTENDS',
    'Деонтическая автономия агентов расширяет концепцию внешней памяти нормативными правилами.'
FROM canon_items s, canon_items t
WHERE s.slug = 'deonticheskaya-avtonomiya-agentov'
  AND t.slug = 'rasshirennyy-razum-i-vneshnyaya-pamyat'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references xr WHERE xr.source_id = s.id AND xr.target_id = t.id AND xr.relationship_type = 'EXTENDS'
  );

INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id,
    t.id,
    'DEPENDS_ON',
    'Агентная экономика опирается на формализованную деонтическую автономию агентов.'
FROM canon_items s, canon_items t
WHERE s.slug = 'agent-to-agent-ekonomika'
  AND t.slug = 'deonticheskaya-avtonomiya-agentov'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references xr WHERE xr.source_id = s.id AND xr.target_id = t.id AND xr.relationship_type = 'DEPENDS_ON'
  );

INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT
    s.id,
    t.id,
    'REFERENCES',
    'Прагматическая архитектура ссылается на концепцию внешней рабочей памяти.'
FROM canon_items s, canon_items t
WHERE s.slug = 'pragmaticheskaya-arhitektura-myshleniya'
  AND t.slug = 'rasshirennyy-razum-i-vneshnyaya-pamyat'
  AND NOT EXISTS (
      SELECT 1 FROM canon_cross_references xr WHERE xr.source_id = s.id AND xr.target_id = t.id AND xr.relationship_type = 'REFERENCES'
  );

-- 3. Seed Russian Essays
INSERT INTO essays (slug, title, summary, content, published_at)
SELECT
    'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
    'Сайт как внешняя рабочая память: от плоских заметок к графу понятий',
    'Как превратить персональный сайт в динамический граф знаний и продолжение естественного мышления.',
    '# Сайт как внешняя рабочая память: от плоских заметок к графу понятий

Традиционные персональные сайты представляют собой статичные витрины. В концепции расширенного разума (Extended Mind Theory) сайт становится внешней рабочей памятью автора, где каждая мысль автоматически связывается с понятийным ядром Канона.',
    TIMESTAMP WITH TIME ZONE '2026-10-22 10:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM essays WHERE slug = 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy'
);

INSERT INTO published_essays (slug, title, summary, content, published_at, curated_by)
SELECT
    'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
    'Сайт как внешняя рабочая память: от плоских заметок к графу понятий',
    'Как превратить персональный сайт в динамический граф знаний и продолжение естественного мышления.',
    '# Сайт как внешняя рабочая память: от плоских заметок к графу понятий

Традиционные персональные сайты представляют собой статичные витрины. В концепции расширенного разума (Extended Mind Theory) сайт становится внешней рабочей памятью автора, где каждая мысль автоматически связывается с понятийным ядром Канона.',
    TIMESTAMP WITH TIME ZONE '2026-10-22 10:00:00+00',
    'Дмитрий Ефремов'
WHERE NOT EXISTS (
    SELECT 1 FROM published_essays WHERE slug = 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy'
);

INSERT INTO articles (slug, title, summary, content, published_at)
SELECT
    'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
    'Сайт как внешняя рабочая память: от плоских заметок к графу понятий',
    'Как превратить персональный сайт в динамический граф знаний и продолжение естественного мышления.',
    '# Сайт как внешняя рабочая память: от плоских заметок к графу понятий

Традиционные персональные сайты представляют собой статичные витрины. В концепции расширенного разума (Extended Mind Theory) сайт становится внешней рабочей памятью автора, где каждая мысль автоматически связывается с понятийным ядром Канона.',
    TIMESTAMP WITH TIME ZONE '2026-10-22 10:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM articles WHERE slug = 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy'
);

INSERT INTO essays (slug, title, summary, content, published_at)
SELECT
    'deonticheskie-invarianty-v-agentnyh-systemah',
    'Деонтические инварианты и безопасность автономных агентов',
    'Применение модальной логики и деонтических операторов в проектировании автономных систем.',
    '# Деонтические инварианты и безопасность автономных агентов

При проектировании ИИ-агентов с высокой степенью автономии критически важно гарантировать соблюдение деонтических ограничений на уровне архитектуры.',
    TIMESTAMP WITH TIME ZONE '2026-10-23 11:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM essays WHERE slug = 'deonticheskie-invarianty-v-agentnyh-systemah'
);

INSERT INTO published_essays (slug, title, summary, content, published_at, curated_by)
SELECT
    'deonticheskie-invarianty-v-agentnyh-systemah',
    'Деонтические инварианты и безопасность автономных агентов',
    'Применение модальной логики и деонтических операторов в проектировании автономных систем.',
    '# Деонтические инварианты и безопасность автономных агентов

При проектировании ИИ-агентов с высокой степенью автономии критически важно гарантировать соблюдение деонтических ограничений на уровне архитектуры.',
    TIMESTAMP WITH TIME ZONE '2026-10-23 11:00:00+00',
    'Дмитрий Ефремов'
WHERE NOT EXISTS (
    SELECT 1 FROM published_essays WHERE slug = 'deonticheskie-invarianty-v-agentnyh-systemah'
);

INSERT INTO articles (slug, title, summary, content, published_at)
SELECT
    'deonticheskie-invarianty-v-agentnyh-systemah',
    'Деонтические инварианты и безопасность автономных агентов',
    'Применение модальной логики и деонтических операторов в проектировании автономных систем.',
    '# Деонтические инварианты и безопасность автономных агентов

При проектировании ИИ-агентов с высокой степенью автономии критически важно гарантировать соблюдение деонтических ограничений на уровне архитектуры.',
    TIMESTAMP WITH TIME ZONE '2026-10-23 11:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM articles WHERE slug = 'deonticheskie-invarianty-v-agentnyh-systemah'
);

-- 4. Seed Russian Podcast Episode
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, youtube_url, telegram_url, spotify_url, apple_podcasts_url, platforms, concept_links, duration_seconds, published_at)
SELECT
    'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura',
    'Выпуск 05: Расширенный разум, граф Канона и агентная экономика',
    'Обсуждение того, как персональный сайт превращается во внешнюю память автора и платформу для ИИ-агентов.',
    'В пятом выпуске Дмитрий Ефремов разбирает концепцию Энди Кларка, структуру Канона, связность эссе и подкастов, а также деонтическую логику в автономных системах.',
    'https://dmitryefremov.com/media/podcast/ep05-extended-mind.mp3',
    'https://www.youtube.com/watch?v=efremov_ep05',
    'https://www.youtube.com/watch?v=efremov_ep05',
    'https://t.me/efremov_mvp/1005',
    'https://open.spotify.com/episode/efremov_ep05',
    'https://podcasts.apple.com/podcast/efremov_ep05',
    'YouTube, Apple Podcasts, Spotify, Telegram',
    'rasshirennyy-razum-i-vneshnyaya-pamyat,deonticheskaya-avtonomiya-agentov',
    3600,
    TIMESTAMP WITH TIME ZONE '2026-10-23 15:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM podcast_episodes WHERE slug = 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura'
);

-- Timecodes for Ep 05
INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    0,
    'Введение: Концепция Extended Mind и сайт как память',
    'Почему сайт должен являться продолжением мышления, а не витриной.',
    1
FROM podcast_episodes e
WHERE e.slug = 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 0
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    900,
    'Канон как семантическое ядро системы взглядов',
    'Связывание понятий с выпусками подкаста и эссе.',
    2
FROM podcast_episodes e
WHERE e.slug = 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 900
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    2100,
    'Деонтическая автономия и Agent-to-Agent протоколы',
    'Как ИИ-агенты взаимодействуют с графом знаний сайта.',
    3
FROM podcast_episodes e
WHERE e.slug = 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 2100
  );

-- Quotes for Ep 05
INSERT INTO podcast_quotes (episode_id, quote_text, speaker, time_offset_seconds, concept_links, display_order)
SELECT
    e.id,
    'Сайт — это не просто публикация текстов, это внешняя рабочая память, где каждая мысль мгновенно находит свое место в графе.',
    'Дмитрий Ефремов',
    450,
    'rasshirennyy-razum-i-vneshnyaya-pamyat',
    1
FROM podcast_episodes e
WHERE e.slug = 'vypusk-05-rasshirennyy-razum-i-kognitivnaya-arhitektura'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_quotes q WHERE q.episode_id = e.id AND q.time_offset_seconds = 450
  );

-- 5. Seed Telegram Sparks
INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at, created_at)
SELECT
    8001,
    -1001234567890,
    'Архитектура расширенного разума начинается с отказа от незафиксированных мыслей. Каждая искра должна становиться узлом графа.',
    'PHILOSOPHY_OF_MIND',
    '#extendedmind #canon #architecture',
    'Дмитрий Ефремов',
    TIMESTAMP WITH TIME ZONE '2026-10-24 08:00:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-24 08:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 8001
);

INSERT INTO sparks (telegram_message_id, telegram_chat_id, content, domain, tags, author, published_at, created_at)
SELECT
    8002,
    -1001234567890,
    'Деонтика в коде: если транзакция не разрешена формальной спецификацией, она должна быть невозможна на уровне типов.',
    'AUTONOMOUS_ENGINEERING',
    '#deontic #safety #types',
    'Дмитрий Ефремов',
    TIMESTAMP WITH TIME ZONE '2026-10-24 09:30:00+00',
    TIMESTAMP WITH TIME ZONE '2026-10-24 09:30:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM sparks WHERE telegram_message_id = 8002
);
