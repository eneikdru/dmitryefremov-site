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

---

Entries: 3. Anything this product claims - a page
heading, a filter, a capability - must trace to one of them or to a declared route.
