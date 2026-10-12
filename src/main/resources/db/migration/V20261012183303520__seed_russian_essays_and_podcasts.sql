-- Seed realistic Russian-language essays, podcast episodes, timecodes, and publication schedules

-- 1. Seed Articles (Essays)
INSERT INTO articles (slug, title, summary, content, published_at)
SELECT
    'high-end-editorial-minimalism',
    'Эстетика инженерного молчания и редакционный минимализм',
    'Отказ от визуального шума и отладочных элементов как проявление уважения к читателю и архитектурной зрелости.',
    '# Эстетика инженерного молчания и редакционный минимализм

## Введение

Настоящая архитектура не заявляет о себе громкими терминальными логами и ярко-красными рамками ошибок на клиентском фасаде. В эпоху информационного перенасыщения наивысшим проявлением качества системы становится молчание — точность, сдержанность и абсолютная предсказуемость.

### Принципы редакционного минимализма:
1. **Чистота восприятия** — отсутствие любых отладочных элементов, декоративных псевдо-терминалов и ненужной телеметрии на фронтенде.
2. **Типографика как каркас** — строгое контрастное сочетание акцидентных гротесков для заголовков и комфортных гарнитур с засечками для долгого и глубокого чтения.
3. **Строгая деонтика** — интерфейс должен выражать только разрешенные и доступные действия, не вводя пользователя в заблуждение.',
    TIMESTAMP WITH TIME ZONE '2026-10-10 11:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM articles WHERE slug = 'high-end-editorial-minimalism'
);

INSERT INTO articles (slug, title, summary, content, published_at)
SELECT
    'deontic-operators-in-distributed-systems',
    'Деонтическая логика и нормативные инварианты в распределенных сервисах',
    'Формализация нормативных состояний системы через модальную деонтическую логику: обязательства, разрешения и запреты.',
    '# Деонтическая логика и нормативные инварианты в распределенных сервисах

## Обоснование

Когда микросервисы обмениваются сообщениями, традиционных логических категорий «истина/ложь» становится недостаточно. Система функционирует в терминах должного: какие транзакции **обязаны** совершиться, какие **разрешены**, а какие **запрещены**.

```
O(A) -> ~P(~A)
```

### Формальные категории:
* **O (Obligatory)** — Гарнитура данных обязана пройти проверку целостности до фиксации транзакции.
* **P (Permitted)** — Повторная отправка сообщений с единым ключом идемпотентности разрешена в любой момент.
* **F (Forbidden)** — Прямая модификация схемы в обход транзакционного контроля категорически запрещена.',
    TIMESTAMP WITH TIME ZONE '2026-10-11 14:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM articles WHERE slug = 'deontic-operators-in-distributed-systems'
);

-- 2. Seed Podcast Episode
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, duration_seconds, published_at)
SELECT
    'systems-and-reality-ep4',
    'Системы и реальность #4: Редакционный дизайн и деонтика интерфейсов',
    'Обсуждение философии High-End Dark дизайна, чистоты читательского опыта и формальных контрактов взаимодействия.',
    'В четвертом выпуске подкаста «Системы и реальность» Дмитрий Ефремов разбирает принципы минимализма, отсутствие лишних терминальных логов на фасаде и проектирование пользовательского опыта как нормативной системы.',
    'https://dmitryefremov.com/media/podcast/ep4.mp3',
    'https://youtube.com/@efremov_dmitriy/videos/ep4',
    3240,
    TIMESTAMP WITH TIME ZONE '2026-10-11 16:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM podcast_episodes WHERE slug = 'systems-and-reality-ep4'
);

-- 3. Seed Timecodes for Ep 4
INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    0,
    'Введение: Чистый UI и инженерная этика',
    'Зачем убирать визуальный шум и отладочную телеметрию с пользовательского фасада.',
    1
FROM podcast_episodes e
WHERE e.slug = 'systems-and-reality-ep4'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 0
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    720,
    'Деонтика пользовательского опыта',
    'Обязательства, разрешения и запреты при проектировании клиентских контрактов.',
    2
FROM podcast_episodes e
WHERE e.slug = 'systems-and-reality-ep4'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 720
  );

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT
    e.id,
    2100,
    'Архитектура медиахаба',
    'Как проектировать синхронизацию аудиоматериалов и таймкодов без разрыва связей.',
    3
FROM podcast_episodes e
WHERE e.slug = 'systems-and-reality-ep4'
  AND NOT EXISTS (
      SELECT 1 FROM podcast_timecodes t WHERE t.episode_id = e.id AND t.time_offset_seconds = 2100
  );

-- 4. Seed Publication Schedules
INSERT INTO publication_schedules (title, platform, status, topic_summary, scheduled_at)
SELECT
    'Эстетика инженерного молчания',
    'ARTICLE',
    'PUBLISHED',
    'Лонгрид о редакционном минимализме и отсутствии шума',
    TIMESTAMP WITH TIME ZONE '2026-10-10 11:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM publication_schedules WHERE title = 'Эстетика инженерного молчания'
);

INSERT INTO publication_schedules (title, platform, status, topic_summary, scheduled_at)
SELECT
    'Системы и реальность #4',
    'YOUTUBE',
    'PUBLISHED',
    'Выпуск подкаста о редакционном дизайне и деонтике интерфейсов',
    TIMESTAMP WITH TIME ZONE '2026-10-11 16:00:00+00'
WHERE NOT EXISTS (
    SELECT 1 FROM publication_schedules WHERE title = 'Системы и реальность #4'
);
