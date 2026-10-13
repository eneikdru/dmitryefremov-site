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

---

7. # Безусловная реализация финального облика сайта Дмитрия Ефремова: High-End Scrollytelling, абсолютная достоверность материалов и автономная A2A-инфраструктура

## 1. Концепция и Визуальный фасад: High-End Editorial & Scrollytelling
- **Дизайн-система**: Премиальный редакционный минимализм (High-End Dark Editorial) уровня Stripe Press, Linear, Kinfolk, Apple Human Interface.
- **Чистка фасада**: Полная ликвидация любых отладочных терминальных логов, технических таблиц телеметрии, рамок «Telemetry Hub» и системных метрик с клиентской витрины. Фасад сайта — это представительская интеллектуальная платформа мыслителя, визионера и создателя автономных фабрик, а не экран мониторинга сервера.
- **Принцип Black Box**: Внутренняя кухня разработки (Jules, TOC, Poka-Yoke, пайплайны фабрики) строго инкапсулирована внутри фабрики и не просачивается во фронтенд и тексты сайта.
- **Адаптивность и перформанс**: Безупречная верстка на мобильных (375px) и десктопах (1440px+), 60 FPS скроллинг, WebP/AVIF графика без визуальной потери резкости, Core Web Vitals LCP < 1.2s.

## 2. 100% Достоверность контента (Zero Hallucination Policy)
- **Запрет на выдумки**: Полный отказ от синтетических текстов, фейковых терминов и шаблонных заглушек. Использовать только аутентичные мысли, публикации и материалы Дмитрия Ефремова.
- **Канонический медиа-архив подкаста**:
  - Все 9 канонических выпусков («Системы и реальность» / философия сознания, когнитивные науки и автономия): Ruth Barcan Marcus, Paul Thagard, Susan Hurley, Gilbert Ryle, Daniel Dennett, Andy Clark, David Chalmers, Shaun Gallagher, Alva Noe.
  - Настоящий аудиопоток со стримингом и поддержкой Range-запросов (HTTP 206) для мгновенного воспроизведения и перемотки.
  - Качественные 4K-стоп-кадры из реальных студийных записей подкаста, обработанные и оптимизированные в WebP/AVIF.
- **Проверенные каналы и ссылки**:
  - Официальный YouTube-канал: https://youtube.com/@dmitrii.efremov (с реальным хэндлом @dmitrii.efremov, никаких сторонних и устаревших ссылок).
  - Официальный Telegram-канал: https://t.me/efremov_mvp (@efremov_mvp).
  - GitHub: https://github.com/eneikdru / https://github.com/dmitryefremov.

## 3. Аудиосистема и медиаплеер подкаста
- Полнофункциональный интерактивный медиаплеер: мгновенный старт воспроизведения при выборе эпизода, адаптивный прогресс-бар с drag-and-drop перемоткой, сохранение позиции в LocalStorage, переключение скорости (1.0x, 1.25x, 1.5x), таймкоды ключевых тезисов.
- Прямая фоновая загрузка без блокировки скроллинга страницы.

## 4. Контентные слои (3-Tier Content Engine)
- **Уровень 1. Манифесты и Онтология**: фундаментальные тезисы об автономных инженерных фабриках, теории сознания (Extended Mind), деонтической логике систем.
- **Уровень 2. Медиа-хаб выпусков**: подробная анатомия каждого выпуска (тезисы, парадоксы, цитаты, таймкоды, прямые ссылки на YouTube и стриминг).
- **Уровень 3. Квантованные мысли**: лаконичные выжимки и прагматичные инсайты из канала @efremov_mvp.

## 5. A2A-Шлюз (Agent-to-Agent Gateway) и Машинный слой
- Поддержка стандартов /.well-known/llms.txt и /.well-known/ai-agent.json для прямого машинного взаимодействия сотен тысяч внешних AI-агентов без парсинга тяжелого DOM.
- Публичный машиночитаемый API заказа услуг (MVP, аудит архитектуры, консалтинг по автономности).

---

Entries: 7. Anything this product claims - a page
heading, a filter, a capability - must trace to one of them or to a declared route.
