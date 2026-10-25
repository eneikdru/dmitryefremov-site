<script lang="ts">
  export let theme: 'dark' | 'light' = 'dark';

  interface ReleaseItem {
    id: string;
    title: string;
    platform: 'YouTube' | 'Podcast' | 'Telegram' | 'Article';
    status: 'in_recording' | 'scheduled' | 'released';
    statusLabel: string;
    dateOrTiming: string;
    thesis: string;
    tag: string;
    link?: string;
  }

  const releases: ReleaseItem[] = [
    {
      id: 'ep-04-ryle',
      title: 'Выпуск #04: Гилберт Райл — «Призрак в машине» и процедурное знание',
      platform: 'Podcast',
      status: 'in_recording',
      statusLabel: 'В СЪЁМКЕ (СТУДИЯ)',
      dateOrTiming: 'Октябрь 2026',
      thesis: 'Категориальная ошибка Декарта: разум — это не скрытая субстанция внутри шестеренок, а слаженная диспозиция поведения. Различие между Знанием-Что и Знанием-Как в эпоху автономных агентов.',
      tag: 'Подкаст «Системы и реальность»',
      link: 'https://youtube.com/@dmitrii.efremov'
    },
    {
      id: 'ep-05-dennett',
      title: 'Выпуск #05: Дэниел Деннет — Интенциональная установка и эволюция разума',
      platform: 'Podcast',
      status: 'in_recording',
      statusLabel: 'В СЪЁМКЕ (СТУДИЯ)',
      dateOrTiming: 'Октябрь 2026',
      thesis: 'Три уровня предсказания сложных систем: физический, проектный и интенциональный. Почему отношение к ИИ как к разумному агенту — это прагматическая стратегия, а не метафизика.',
      tag: 'Подкаст «Системы и реальность»',
      link: 'https://youtube.com/@dmitrii.efremov'
    },
    {
      id: 'tg-mvp-daily',
      title: 'Автономная инженерия, теория ограничений (TOC) и деонтическая логика',
      platform: 'Telegram',
      status: 'scheduled',
      statusLabel: 'ЕЖЕДНЕВНЫЙ ПОТОК',
      dateOrTiming: 'Понедельник — Пятница',
      thesis: 'Прагматика выживания в эпоху AI-агентов, архитектурные инварианты, почему «done» не равно «delivered», и как строить цифровые системы без галлюцинаций.',
      tag: 'Telegram @efremov_mvp',
      link: 'https://t.me/efremov_mvp'
    },
    {
      id: 'art-deontic-systems',
      title: 'Деонтические инварианты в распределенных агентах: запрет как код',
      platform: 'Article',
      status: 'scheduled',
      statusLabel: 'ГОТОВИТСЯ К ПУБЛИКАЦИИ',
      dateOrTiming: 'Скоро',
      thesis: 'Математическая формализация нормативных состояний систем: обязательства, разрешения и исключающие запреты (exclusionary reasons Джозефа Раза).',
      tag: 'Thought Stream',
      link: '#'
    },
    {
      id: 'ep-01-marcus',
      title: 'Выпуск #01: Рут Баркан Маркус — Модальная логика и деонтические инварианты',
      platform: 'Podcast',
      status: 'released',
      statusLabel: 'В ЭФИРЕ',
      dateOrTiming: '2026-09-20',
      thesis: 'Формула Баркан как математический мост между возможными мирами и инвариантами автономных систем. Если нечто возможно необходимо, оно существует.',
      tag: 'Подкаст «Системы и реальность»',
      link: 'https://youtu.be/1qiPT9UxKNM'
    },
    {
      id: 'ep-02-thagard',
      title: 'Выпуск #02: Пол Тагард — Вычислительная когерентность и преодоление галлюцинаций',
      platform: 'Podcast',
      status: 'released',
      statusLabel: 'В ЭФИРЕ',
      dateOrTiming: '2026-09-22',
      thesis: 'Объяснительная когерентность (ECHO) как математическая модель преодоления галлюцинаций в сложных информационных сетях.',
      tag: 'Подкаст «Системы и реальность»',
      link: 'https://youtu.be/jUbOF_YvkfI'
    },
    {
      id: 'ep-03-hurley',
      title: 'Выпуск #03: Сюзан Хёрли — Сознание в действии и обратная связь среды',
      platform: 'Podcast',
      status: 'released',
      statusLabel: 'В ЭФИРЕ',
      dateOrTiming: '2026-09-25',
      thesis: 'Критика сэндвич-модели. Сознание рождается в непрерывной сенсомоторной петле сопротивления физической и цифровой реальности.',
      tag: 'Подкаст «Системы и реальность»',
      link: 'https://youtu.be/mLJm6YKm0ds'
    }
  ];

  let selectedPlatform: 'ALL' | 'Podcast' | 'Telegram' | 'Article' = 'ALL';

  $: filteredReleases = selectedPlatform === 'ALL'
    ? releases
    : releases.filter(r => r.platform === selectedPlatform);
</script>

<div class="space-y-6">
  <!-- Section Header -->
  <div class="border-b pb-4 {theme === 'dark' ? 'border-[#1f2433]' : 'border-[#e2e8f0]'} flex flex-col sm:flex-row sm:items-center justify-between gap-4">
    <div>
      <h2 class="text-2xl sm:text-3xl font-bold tracking-tight {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
        График релизов & Съёмочный процесс
      </h2>
      <p class="text-sm font-mono mt-1 {theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">
        Актуальный статус съёмок подкаста «Системы и реальность», публикации лонгридов и стрим инсайтов.
      </p>
    </div>

    <!-- Filter Buttons -->
    <div class="flex items-center gap-1.5 p-1 rounded-xl border font-mono text-xs shrink-0 {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433]' : 'bg-white border-[#e2e8f0] shadow-sm'}">
      <button
        type="button"
        on:click={() => (selectedPlatform = 'ALL')}
        class="px-2.5 py-1.5 rounded-lg transition-colors {selectedPlatform === 'ALL' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold' : 'bg-[#0284c7] text-white font-bold') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
      >
        ВСЕ ({releases.length})
      </button>
      <button
        type="button"
        on:click={() => (selectedPlatform = 'Podcast')}
        class="px-2.5 py-1.5 rounded-lg transition-colors {selectedPlatform === 'Podcast' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold' : 'bg-[#0284c7] text-white font-bold') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
      >
        🎙️ ПОДКАСТ
      </button>
      <button
        type="button"
        on:click={() => (selectedPlatform = 'Telegram')}
        class="px-2.5 py-1.5 rounded-lg transition-colors {selectedPlatform === 'Telegram' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold' : 'bg-[#0284c7] text-white font-bold') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
      >
        TELEGRAM
      </button>
      <button
        type="button"
        on:click={() => (selectedPlatform = 'Article')}
        class="px-2.5 py-1.5 rounded-lg transition-colors {selectedPlatform === 'Article' ? (theme === 'dark' ? 'bg-[#1f2433] text-white font-bold' : 'bg-[#0284c7] text-white font-bold') : (theme === 'dark' ? 'text-[#94a3b8] hover:text-white' : 'text-[#64748b] hover:text-[#0f172a]')}"
      >
        СТАТЬИ
      </button>
    </div>
  </div>

  <!-- Timeline Cards -->
  <div class="space-y-4">
    {#each filteredReleases as item (item.id)}
      <article class="p-5 sm:p-6 rounded-2xl border transition-all hover:border-[#38bdf8]/40 {theme === 'dark' ? 'bg-[#0f1118] border-[#1f2433] hover:bg-[#12151e]' : 'bg-white border-[#e2e8f0] shadow-sm hover:shadow-md'}">
        <div class="flex flex-col sm:flex-row sm:items-start justify-between gap-3">
          <div class="space-y-2 flex-1">
            <div class="flex flex-wrap items-center gap-2 text-xs font-mono">
              <!-- Status Pill -->
              {#if item.status === 'in_recording'}
                <span class="px-2.5 py-1 rounded-full font-bold bg-[#f59e0b]/15 text-[#fbbf24] border border-[#f59e0b]/30 flex items-center gap-1.5">
                  <span class="w-2 h-2 rounded-full bg-[#f59e0b] animate-pulse"></span>
                  {item.statusLabel}
                </span>
              {:else if item.status === 'released'}
                <span class="px-2.5 py-1 rounded-full font-bold bg-[#10b981]/15 text-[#34d399] border border-[#10b981]/30">
                  {item.statusLabel}
                </span>
              {:else}
                <span class="px-2.5 py-1 rounded-full font-bold bg-[#38bdf8]/15 text-[#38bdf8] border border-[#38bdf8]/30">
                  {item.statusLabel}
                </span>
              {/if}

              <span class="{theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">•</span>
              <span class="{theme === 'dark' ? 'text-[#94a3b8]' : 'text-[#64748b]'}">{item.tag}</span>
              <span class="{theme === 'dark' ? 'text-[#64748b]' : 'text-[#94a3b8]'}">•</span>
              <span class="text-[#38bdf8]">{item.dateOrTiming}</span>
            </div>

            <h3 class="text-lg sm:text-xl font-bold {theme === 'dark' ? 'text-white' : 'text-[#0f172a]'}">
              {item.title}
            </h3>

            <p class="text-sm leading-relaxed {theme === 'dark' ? 'text-[#cbd5e1]' : 'text-[#475569]'}">
              {item.thesis}
            </p>
          </div>

          {#if item.link && item.link !== '#'}
            <a
              href={item.link}
              target="_blank"
              rel="noopener noreferrer"
              class="self-start px-3.5 py-2 rounded-xl text-xs font-mono font-bold transition-all shrink-0 flex items-center gap-1.5 {theme === 'dark' ? 'bg-[#141824] text-[#38bdf8] border border-[#1f2433] hover:border-[#38bdf8] hover:bg-[#1a2032]' : 'bg-[#f1f5f9] text-[#0284c7] border border-[#cbd5e1] hover:border-[#0284c7] hover:bg-[#e2e8f0]'}"
            >
              <span>Смотреть / Слушать</span>
              <span>↗</span>
            </a>
          {/if}
        </div>
      </article>
    {/each}
  </div>
</div>
