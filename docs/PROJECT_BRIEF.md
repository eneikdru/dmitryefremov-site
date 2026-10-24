# Project Brief

## Customer Job

The client's own entries, quoted verbatim and in the client's own language. This file is
maintained by the factory from the wishlist and must not be edited by hand or translated -
it is the referent every later artifact is checked against.

1. # Персональный представительский сайт: Dmitry Efremov (dmitryefremov.com)
   
   ## 1. Стек и Архитектура
   - Frontend: SvelteKit (Svelte 5 / TypeScript) + TailwindCSS (Vite).
   - Backend: Java 17 + Spring Boot 3 (REST API, RSS/Atom Feed, Sitemap, SEO OpenGraph).
   - Модули бэкенда:
     - PublicationScheduleService: сервис интерактивного расписания публикаций с синхронизацией тем (Telegram @efremov_mvp, YouTube @efremov_dmitriy, статьи). Статусы: SCHEDULED, IN_PRODUCTION, PUBLISHED.
     - PodcastEpisodeService: каталог выпусков подкаста «Системы и реальность» с таймкодами, тезисами и медиа-линками.
     - ArticleService: поток заметок и лонгридов (Markdown, типографика, формулы, цитаты).
     - FeedController: динамический RSS (/feed.xml) и sitemap.xml.
   - Контейнеризация: Docker Compose (backend + frontend nginx с кэшированием и SSL proxy). Zero-bloat, Google PageSpeed 95+.
   
   ## 2. Дизайн и Эстетика (Превосходит Framer)
   - Стиль: Modern Swiss Editorial / Technical Brutalism (эстетика Linear, Vercel, Stripe Press).
   - Никаких шаблонов инфобизнеса и коучинга. Инженерный манифест, строгая логика и деонтическая чистота.
   - Палитра: Dark Void (#090a0f, #12151e, #1f2433) с контрастным переключением в Light Monochrome.
   - Типографика: Geist / Inter Display для заголовков, JetBrains Mono для системных тегов ([SYS_CORE], [EP_08], даты).
   - Микро-анимации: интерактивные hover-эффекты границ, плавный скролл, визуализация аудио-волны подкаста.
   
   ## 3. Ключевые страницы и компоненты
   - Hero-секция: Заголовок «Дмитрий Ефремов», подзаголовок «Системы, логика мышления и автономные процессы», манифест: «Ваш код и ваши решения — это то, кто вы есть на самом деле.»
   - Интерактивная матрица расписания (Release Timeline Calendar) с фильтрами по платформам.
   - Медиа-хаб выпусков авторского подкаста.
   - Thought Stream: глубокие статьи о деонтической логике, теории ограничений и автономных системах.
   - Терминал контактов: ссылки на Telegram, YouTube, GitHub, LinkedIn, email.

2. Свободное управление контентом через любых AI-агентов после готовности сайта: предусмотреть открытые интерфейсы (Headless API / Markdown / JSON / RSS) для автономного обновления материалов (подкасты, темы, статьи, ссылки дистрибуции) внешними агентами. Доступ агентов строго ограничен только уровнем контента, без вмешательства в код, стили и инфраструктуру проекта.

3. Открытый API для взаимодействия с внешними AI-агентами клиентов: реализовать публичные машиночитаемые API-эндпоинты (OpenAPI / JSON schema / llms.txt), позволяющие любым внешним ИИ (ChatGPT, Claude, персональные ассистенты людей) свободно оформлять заказы на услуги (MVP, аудит, автоматизация) и отправлять прямые сообщения автору с сайта от имени пользователей.

4. Оптимизация и обновление визуальных материалов (фото): использовать кадры и материалы с официального YouTube-канала автора (@dmitrii.efremov) вместо старого сайта, перегенерировать/улучшить их до чистого студийного качества. Обеспечить максимальную легковесность графики (современные форматы WebP/AVIF, адаптивный скейлинг, сжатие без потери резкости), чтобы сайт загружался мгновенно и оставался лёгким.

5. Полная трансформация визуального стиля и архитектуры фронтенда персональной платформы Дмитрия Ефремова. Генерация через Stitch детерминированной дизайн-системы: премиальный минимализм (High-End Dark / Editorial), типографика (заголовки гротеск, эссе и цитаты с комфортным чтением), ликвидация любых отладочных терминальных рамок и логов телеметрии на фасаде. Полноценная интеграция медиахаба подкаста «Системы и реальность» с плеером и платформами (YouTube, Telegram), чистый ридер эссе и статей деонтической логики, эстетичная контактная карточка. Pixel-perfect перенос сгенерированного Stitch-макета в компоненты Svelte.

6. # Product Evolution: Sovereign Digital Node of Dmitry Efremov (A2A Gateway & Real Thought Stream)
   
   ## 1. Zero Hallucination: Real Content Synchronization
   - Replace all placeholder/mock content with Dmitry Efremov's genuine intellectual artifacts.
   - Sync episodes from podcast/YouTube archive (Ruth Barcan Marcus, Paul Thagard, Susan Hurley, Gilbert Ryle, Daniel Dennett, Andy Clark, David Chalmers) with key insights, timestamps, and quotes.
   - Sync real-world insights from Telegram channel @efremov_mvp (MVP pragmatics, autonomous engineering, business survival in AI era).
   
   ## 2. 3-Tier Content Architecture on Frontend
   - Tier 1: The Canon (Evergreen manifestos, ontology, glossary of autonomy and extended mind).
   - Tier 2: The Stream / Episodes (Unified media player + structured anatomy: core thesis, paradoxes, timestamps, concept links).
   - Tier 3: The Sparks / Quantized Thoughts (Fast stream of real-time insights from Telegram).
   - Organize across 4 practical domains: Autonomous Engineering, Agentic Economy (A2A), Pragmatic Business Strategy, Philosophy of Mind.
   
   ## 3. High-Traffic Machine Layer (For Hundreds of Thousands of External Agents)
   - Provide /.well-known/llms.txt and /.well-known/ai-agent.json providing clean markdown index of all concepts.
   - Semantic SEO & Open Graph meta tags optimized for LLM web crawlers (PerplexityBot, GPTBot, ClaudeBot).
   - Clean high-density text representations without heavy DOM/JS blocking for machine readers.
   
   ## 4. Black Box Architecture & Absolute NDA
   - Strict Black Box invariant: Zero internal factory mechanics (Jules, TOC, Poka-Yoke pipelines) exposed to visitors.
   - Clean client intake: Input idea/wishlist -> output turnkey result.
   - Commercial confidentiality and NDA guaranteed.

7. Безусловная реализация финального облика сайта Дмитрия Ефремова: High-End Scrollytelling, абсолютная достоверность материалов, чистый звук подкаста без студийных фото и автономная A2A-инфраструктура

8. # Black Box Invariant & Dual-Contour Architecture
   
   ## 1. Strict Separation: Public Client Portal vs Private Admin Dashboard
   - Complete removal of internal mechanics from the public user interface (Zero leakage of internal telemetry, GQM metrics, [SYS_CORE] tags, debug frames, and production recording schedules).
   - Move all telemetry, dwell time tracking metrics, and recording pipeline data into a secure private admin route (/admin) accessible only to Dmitry Efremov.
   
   ## 2. Public Client Portal (dmitryefremov.com) - Purpose-Built for Readers & Thinkers
   - High-End Swiss Editorial / Technical Brutalism aesthetic (clean typography, no AI-generated cognitive noise, no intrusive popups).
   - Podcast: showcase only released canonical episodes (Episode 01 Ruth Barcan Marcus, Episode 02 Paul Thagard, Episode 03 Susan Hurley) with high-fidelity audio playback and topic navigation.
   - Thoughts & Essays: publish clean, standalone, edited articles and key conceptual takeaways derived from the distribution queue (not raw teleprompter drafts).
   - Confidential Client Intake & A2A Gateway: secure entrypoint for high-end systems audit and agentic architecture requests.
   
   ## 3. Strict Content Guardrail: Raw Teleprompter Isolation
   - Teleprompter scripts (/mnt/HC_Volume_106932004/podcast/episodes/*) are internal recording instruments and must never be exposed directly as live site articles.
   - Public reading materials must be curated, polished executive summaries and essays.

9. # Сшивка расширенного разума: сайт как внешняя рабочая память Дмитрия Ефремова (по Энди Кларку)
   
   Цель: сайт перестаёт быть витриной и становится продолжением мышления автора. Критерий успеха один: мысль автора (выпуск подкаста, эссе, короткая заметка из Telegram @efremov_mvp, понятие Канона) попадает на сайт без ручного труда и сразу оказывается связана со всем остальным. Считать закрытые задачи недостаточно: приёмка идёт по тому, что видит и чувствует живой пользователь.
   
   ## 1. Живой контур памяти: фронтенд работает только на реальных данных бэкенда
   - Все экраны публичного портала (подкаст, эссе, Sparks, Канон) получают данные только из боевых API бэкенда (PodcastEpisodeService, EssayService, CanonService, приём Telegram-искр через TelegramSparkMapper, PublicationScheduleService). Записи, зашитые в TypeScript-константы, удаляются.
   - Полностью удалить все моки и англоязычные заглушки: вымышленных авторов (@alexp и аналоги), фиктивные счётчики (1.4k, spk/m), демо-статьи (например Deontic Logic in Autonomous Process Architecture). Если данных нет, экран показывает честное пустое состояние на русском, а не выдуманную запись.
   - Показываются только реально вышедшие на канале выпуски подкаста (сейчас это выпуски с рабочими YouTube-ссылками). Плашка с числом выпусков считается из API, а не пишется текстом.
   - Замкнуть конвейер без ручного труда: новая запись в Telegram @efremov_mvp или материал, отправленный автором или его ИИ-агентом через Headless API (только уровень контента, без доступа к коду и инфраструктуре), появляется на сайте автоматически. Сырые суфлёры подкаста на сайт не попадают никогда (инвариант Raw Teleprompter Isolation из брифа): публикуются только отредактированные эссе и тезисы.
   - Приёмка: вся публичная страница при запуске на пустой базе не содержит ни одной записи, не принадлежащей автору, а после появления записи в БД она видна на сайте без изменения кода.
   
   ## 2. Единый граф понятий вместо плоских вкладок
   - Канон (ядро системы взглядов автора) выводится на первый план: понятие Канона связано живыми перекрёстными ссылками с выпусками подкаста, эссе и короткими заметками, где оно разбирается. С выпуска можно перейти к его опорным понятиям, с понятия к выпускам, эссе и заметкам.
   - Мгновенный поиск по всему корпусу (подкасты, эссе, искры, понятия Канона) с вызовом по Cmd+K / Ctrl+K, работает с клавиатуры, без перезагрузки страницы, отдаёт результат по смыслу узла, а не только по подстроке.
   - У каждого узла (понятие, выпуск, эссе, искра) постоянный читаемый адрес, чистый HTML для чтения и машиночитаемое представление (llms.txt, ai-agent.json, Markdown/JSON), чтобы внешние ИИ-агенты видели ту же связность, что и человек. Схема связей хранится в БД и отдаётся через API, а не собирается руками во фронтенде.
   - Внешней рабочей памятью должен быть удобен пользоваться сам автор: быстро найти своё понятие, увидеть, где он о нём говорил, и добавить заметку с минимальным трением.
   
   ## 3. Чистый швейцарский фасад: только то, что работает на мысль
   - Убрать с публичного портала вкладку «Студия и сетап» вместе с карточками рабочего пространства и студийными кадрами.
   - Убрать из публичного подвала и интерфейса ссылку на /admin и любые упоминания внутренней кухни (инвариант Black Box: никаких внутренних механик фабрики, Jules, TOC, служебных тегов вроде [SYS_CORE], отладочных рамок, расписания записи). Административный контур остаётся доступен автору только по прямому защищённому маршруту.
   - Эстетика сохраняется: Modern Swiss Editorial / Technical Brutalism, Dark Void и Light Monochrome, без попапов и визуального шума. Лишнее удаляется, новые декоративные элементы не добавляются.
   
   ## Явно вне этого задания
   Автоматическая пересборка и выкатка живого стенда на сервере в это задание не входит: этим автор занимается вручную. Задача не должна менять Dockerfile, docker-compose, nginx и CI ради автовыкатки.

---

Entries: 9. Anything this product claims - a page
heading, a filter, a capability - must trace to one of them or to a declared route.
