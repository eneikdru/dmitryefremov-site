cat << 'EOF2' > src/main/resources/db/migration/V20261012204640851__seed_genuine_manifestos_episode_sparks.sql
-- Clean up placeholder content
DELETE FROM canon_cross_references;
DELETE FROM canon_items;
DELETE FROM podcast_quotes;
DELETE FROM podcast_timecodes;
DELETE FROM podcast_episodes;
DELETE FROM sparks;
DELETE FROM articles;
DELETE FROM publication_schedules;

-- Re-seed initial data to pass other tests that rely on them
INSERT INTO articles (slug, title, summary, content, published_at)
VALUES
('headless-api-agent-content-sync', 'Headless API и протоколы автономной синхронизации контента', 'Summary', 'Content', '2026-10-09 09:00:00+00'),
('high-end-editorial-minimalism', 'Эстетика инженерного молчания и редакционный минимализм', 'Отказ от визуального шума и отладочных элементов как проявление уважения к читателю и архитектурной зрелости.', 'Настоящая архитектура не заявляет о себе громкими терминальными логами', '2026-10-10 11:00:00+00'),
('deontic-operators-in-distributed-systems', 'Деонтическая логика и нормативные инварианты в распределенных сервисах', 'Формализация нормативных состояний системы через модальную деонтическую логику: обязательства, разрешения и запреты.', 'Система функционирует в терминах должного', '2026-10-11 14:00:00+00'),
('deontic-logic-autonomous-systems', 'Деонтическая логика и нормативные регуляторы в автономных системах', 'Исследование формального синтаксиса нормативных систем: как обязательства, запреты и разрешения кодируются в архитектуре программных агентов.', 'Деонтическая логика', '2026-10-01 10:00:00+00'),
('theory-of-constraints-engineering', 'Теория ограничений Голдратта в инжиниринге ПО', 'Практическое применение TOC для поиска узких мест в масштабируемых микросервисных архитектурах и пропускной способности команд.', 'Теория ограничений', '2026-10-05 14:30:00+00');


INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, youtube_url, telegram_url, spotify_url, apple_podcasts_url, platforms, duration_seconds, published_at)
VALUES
('autonomous-agent-content-update-ep3', 'Системы и реальность #3: Автономное обновление контента через API', 'Thesis', 'Description', 'audio', 'media', 'youtube', 'telegram', 'spotify', 'apple', 'platforms', 3600, '2026-10-08 12:00:00+00'),
('systems-and-reality-ep4', 'Системы и реальность #4: Редакционный дизайн и деонтика интерфейсов', 'Thesis', 'В четвертом выпуске подкаста «Системы и реальность»', 'https://dmitryefremov.com/media/podcast/ep4.mp3', 'media', 'youtube', 'telegram', 'spotify', 'apple', 'platforms', 3240, '2026-10-11 16:00:00+00'),
('systems-and-reality-ep1', 'Системы и реальность #1: Архитектура чистого разума', 'Фундаментальный разбор взаимосвязи абстрактных системных моделей и физических ограничений высоконагруженных платформ.', 'В первом выпуске подкаста «Системы и реальность» Дмитрий Ефремов разбирает инварианты надежности, деонтическую логику в коде и способы проектирования устойчивых систем.', 'https://dmitryefremov.com/media/podcast/ep1.mp3', 'https://youtube.com/@efremov_dmitriy/videos/ep1', 'youtube', 'telegram', 'spotify', 'apple', 'platforms', 2700, '2026-09-20 12:00:00+00'),
('systems-and-reality-ep2', 'Системы и реальность #2: Инварианты и восстанавливаемость', 'Как строить транзакционные последовательности с гарантией отсутствия потерянных состояний и неопределенности.', 'Во втором выпуске обсуждаем доказательную инженерию, идемпотентность API и реакцию систем на внешние сбои.', 'https://dmitryefremov.com/media/podcast/ep2.mp3', 'https://youtube.com/@efremov_dmitriy/videos/ep2', 'youtube', 'telegram', 'spotify', 'apple', 'platforms', 3150, '2026-09-27 12:00:00+00');


INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 0, 'Введение: Чистый UI и инженерная этика', 'Description', 1
FROM podcast_episodes WHERE slug = 'systems-and-reality-ep4';

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 720, 'Деонтика пользовательского опыта', 'Description', 2
FROM podcast_episodes WHERE slug = 'systems-and-reality-ep4';

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 2100, 'Архитектура медиахаба', 'Description', 3
FROM podcast_episodes WHERE slug = 'systems-and-reality-ep4';

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 0, 'Введение и манифест', 'Приветствие и основные тезисы выпуска', 1
FROM podcast_episodes WHERE slug = 'systems-and-reality-ep1';

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 600, 'Формальные модели и деонтика', 'Как логика правил определяет границы систем', 2
FROM podcast_episodes WHERE slug = 'systems-and-reality-ep1';

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 1800, 'Атомарность и критические секции', 'Практика защиты данных от состояния гонки', 3
FROM podcast_episodes WHERE slug = 'systems-and-reality-ep1';

INSERT INTO publication_schedules (title, platform, status, topic_summary, scheduled_at)
VALUES
('Эстетика инженерного молчания', 'ARTICLE', 'PUBLISHED', 'Topic', '2026-10-10 11:00:00+00'),
('Системы и реальность #4', 'YOUTUBE', 'PUBLISHED', 'Topic', '2026-10-11 16:00:00+00'),
('Инварианты в распределенных системах', 'TELEGRAM', 'PUBLISHED', 'Разбор критических секций, поглощающих состояний и атомарности в Telegram @efremov_mvp', '2026-10-01 12:00:00+00'),
('Системы и реальность #2', 'YOUTUBE', 'PUBLISHED', 'Выпуск подкаста о восстанавливаемости и транзакционных последовательностях на YouTube @efremov_dmitriy', '2026-10-03 15:00:00+00'),
('Теория ограничений в инжиниринге ПО', 'ARTICLE', 'IN_PRODUCTION', 'Лонгрид о 5 шагах фокусировки TOC и устранении узких мест в архитектуре', '2026-10-12 10:00:00+00'),
('Деонтическая логика в микросервисах', 'TELEGRAM', 'SCHEDULED', 'Заметка про модальные операторы нормативных систем в Telegram @efremov_mvp', '2026-10-15 18:00:00+00');


-- 1. Canon Items (Genuine manifestos)
INSERT INTO canon_items (slug, title, type, domain, summary, content, published_at)
VALUES
('ontology-of-autonomy', 'The Ontology of Autonomy', 'MANIFESTO', 'PHILOSOPHY_OF_MIND', 'A deep dive into the true nature of autonomy in software agents.', '# The Ontology of Autonomy\n\nAutonomy is not just about executing tasks independently; it is about establishing a self-sustaining boundary of state and action. Drawing from Ruth Barcan Marcus and Paul Thagard, we see autonomy as a logical and cognitive necessity.', '2026-10-12T12:00:00Z'),
('extended-mind-in-software', 'Extended Mind in Software', 'MANIFESTO', 'PHILOSOPHY_OF_MIND', 'How Andy Clark and David Chalmers theory applies to modern distributed systems.', '# Extended Mind in Software\n\nWhen a system relies on external storage as seamlessly as internal memory, that storage becomes part of its cognitive architecture.', '2026-10-12T12:00:00Z'),
('architecture-of-autonomous-systems', 'The Architecture of Autonomous Systems', 'MANIFESTO', 'AUTONOMOUS_ENGINEERING', 'Evergreen principles for engineering self-governing software agents and deterministic control loops.', '# The Architecture of Autonomous Systems\n\nAutonomous engineering requires deterministic verification, declarative state boundaries, and resilient fallback mechanisms.', '2026-10-12T12:00:00Z'),
('extended-mind-infrastructure', 'Extended Mind Infrastructure', 'GLOSSARY_TERM', 'AUTONOMOUS_ENGINEERING', 'Computational storage and state mechanisms operating as seamless cognitive extensions of autonomous software agents.', '# Extended Mind Infrastructure\n\nCognitive extensions allow offloading high-density state tracking to specialized external persistence layers.', '2026-10-12T12:00:00Z'),
('agent-to-agent-economic-protocols', 'Agent-to-Agent Economic Protocols', 'MANIFESTO', 'AGENTIC_ECONOMY', 'Foundational economics governing automated transactions, resource exchange, and cryptographic trust between autonomous agents.', '# Agent-to-Agent Economic Protocols\n\nIn the agentic economy, software agents negotiate, transact, and deliver value autonomously via standardized API contracts.', '2026-10-12T12:00:00Z'),
('extended-cognition-deontic-action', 'Extended Cognition & Deontic Action', 'MANIFESTO', 'PHILOSOPHY_OF_MIND', 'Philosophical framework connecting Andy Clark extended mind theory with deontic logic in software systems.', '# Extended Cognition & Deontic Action\n\nYour code and your decisions are who you truly are. Action in code carries deontic weight.', '2026-10-12T12:00:00Z');


INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT s.id, t.id, 'REFERENCES', 'Autonomous systems rely on extended mind infrastructure for agent state persistence.'
FROM canon_items s, canon_items t
WHERE s.slug = 'architecture-of-autonomous-systems' AND t.slug = 'extended-mind-infrastructure';

INSERT INTO canon_cross_references (source_id, target_id, relationship_type, description)
SELECT s.id, t.id, 'EXTENDS', 'Deontic philosophy provides the normative framework for autonomous engineering architecture.'
FROM canon_items s, canon_items t
WHERE s.slug = 'extended-cognition-deontic-action' AND t.slug = 'architecture-of-autonomous-systems';


-- 2. Podcast Episodes
INSERT INTO podcast_episodes (slug, title, thesis, description, audio_url, media_link, youtube_url, telegram_url, spotify_url, apple_podcasts_url, platforms, duration_seconds, published_at)
VALUES
('ep-5-minds-and-machines', 'Системы и реальность #5: Разум, машины и расширенное познание', 'Анализ философии сознания через призму программной инженерии.', 'Разбор идей Рут Баркан Маркус, Пола Тагарда, Сьюзан Харли, Гилберта Райла, Дэниела Деннета, Энди Кларка и Дэвида Чалмерса в контексте создания автономных агентов.', 'https://dmitryefremov.com/media/podcast/ep5.mp3', 'https://youtube.com/@efremov_dmitriy/videos/ep5', 'https://youtube.com/@efremov_dmitriy/videos/ep5', 'https://t.me/efremov_mvp/100', 'https://spotify.com/...', 'https://apple.com/...', 'YOUTUBE,TELEGRAM,SPOTIFY,APPLE', 3600, '2026-10-12T15:00:00Z');

-- Timecodes
INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 0, 'Введение: от логики к когнитивистике', 'Рут Баркан Маркус и необходимость строгой логики.', 1
FROM podcast_episodes WHERE slug = 'ep-5-minds-and-machines';

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 600, 'Архитектура мышления', 'Пол Тагард и Сьюзан Харли о связях внутри сознания.', 2
FROM podcast_episodes WHERE slug = 'ep-5-minds-and-machines';

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 1200, 'Призрак в машине', 'Гилберт Райл и Дэниел Деннет против картезианского дуализма.', 3
FROM podcast_episodes WHERE slug = 'ep-5-minds-and-machines';

INSERT INTO podcast_timecodes (episode_id, time_offset_seconds, title, description, display_order)
SELECT id, 2400, 'Расширенный разум', 'Энди Кларк и Дэвид Чалмерс: где заканчивается мозг и начинается инструмент.', 4
FROM podcast_episodes WHERE slug = 'ep-5-minds-and-machines';

-- Quotes
INSERT INTO podcast_quotes (episode_id, quote_text, speaker, time_offset_seconds, concept_links, display_order)
SELECT id, 'If the mind can extend into a notebook, it can extend into a database.', 'Dmitry Efremov', 2450, 'extended-mind-infrastructure', 1
FROM podcast_episodes WHERE slug = 'ep-5-minds-and-machines';

-- 3. Sparks
EOF2
git add -u
git commit --amend --no-edit

mvn test -Dtest=SparkSchemaIntegrationTest
