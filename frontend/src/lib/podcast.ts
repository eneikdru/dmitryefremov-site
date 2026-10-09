export interface PodcastTimecode {
  id: number | string;
  timeOffsetSeconds: number;
  title: string;
  description?: string;
  displayOrder?: number;
}

export interface PodcastEpisode {
  id: number | string;
  slug: string;
  title: string;
  thesis: string;
  description: string;
  audioUrl: string;
  mediaLink?: string;
  durationSeconds: number;
  publishedAt: string;
  timecodes: PodcastTimecode[];
}

export const PODCAST_EPISODES: PodcastEpisode[] = [
  {
    id: 1,
    slug: 'ep-01-systems-and-reality',
    title: 'Выпуск 01: Системы и реальность — Введение в системное мышление',
    thesis: 'Системное мышление как преодоление когнитивных искажений в инженерной практике и менеджменте.',
    description: 'В этом выпуске обсуждаем фундаментальные понятия подкаста «Системы и реальность»: граница системы, обратные связи и почему локальная оптимизация разрушает целое.',
    audioUrl: 'https://cdn.dmitryefremov.com/audio/podcast-ep01.mp3',
    mediaLink: 'https://youtube.com/@efremov_dmitriy',
    durationSeconds: 1240,
    publishedAt: '2026-03-01',
    timecodes: [
      { id: 101, timeOffsetSeconds: 0, title: 'Введение и контекст выпуска', description: 'Цель подкаста и главные тезисы системного анализа', displayOrder: 1 },
      { id: 102, timeOffsetSeconds: 195, title: 'Что такое система в реальном мире', description: 'Различие между агрегатом и целостной системой', displayOrder: 2 },
      { id: 103, timeOffsetSeconds: 525, title: 'Закон Голдратта и теория ограничений', description: 'Поиск бутылочного горлышка в процессах', displayOrder: 3 },
      { id: 104, timeOffsetSeconds: 880, title: 'Деонтические инварианты и архитектура', description: 'Формализация нормативных правил', displayOrder: 4 },
      { id: 105, timeOffsetSeconds: 1120, title: 'Заключение и выводы', description: 'Итоги темы и резюме первого эпизода', displayOrder: 5 }
    ]
  },
  {
    id: 2,
    slug: 'ep-02-deontic-invariants',
    title: 'Выпуск 02: Деонтическая логика и нормативные инварианты',
    thesis: 'Математическая строгость модальной логики Долженства, Разрешения и Запрета в системах.',
    description: 'Разбор модальных операторов O(phi), P(phi), F(phi) и их превращения в программные гарантии безопасности и целостности данных.',
    audioUrl: 'https://cdn.dmitryefremov.com/audio/podcast-ep02.mp3',
    mediaLink: 'https://t.me/efremov_mvp',
    durationSeconds: 980,
    publishedAt: '2026-03-15',
    timecodes: [
      { id: 201, timeOffsetSeconds: 0, title: 'Вводный обзор модальной логики', description: 'Истоки деонтических систем', displayOrder: 1 },
      { id: 202, timeOffsetSeconds: 260, title: 'Операторы O, P и F', description: 'Связь Долженствования, Разрешения и Запрета', displayOrder: 2 },
      { id: 203, timeOffsetSeconds: 610, title: 'Атомарность и супервентность', description: 'Гарантии от гонок состояний', displayOrder: 3 },
      { id: 204, timeOffsetSeconds: 840, title: 'Вопросы и ответы', description: 'Разбор реальных сценариев из архитектуры', displayOrder: 4 }
    ]
  }
];

export function formatTime(seconds: number): string {
  if (isNaN(seconds) || seconds < 0) return '00:00';
  const rounded = Math.floor(seconds);
  const mins = Math.floor(rounded / 60);
  const secs = rounded % 60;
  const hrs = Math.floor(mins / 60);
  const remMins = mins % 60;

  if (hrs > 0) {
    return `${hrs.toString().padStart(2, '0')}:${remMins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
  return `${remMins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}

export function generateWaveformHeights(count: number, seed: string = 'default'): number[] {
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = (hash << 5) - hash + seed.charCodeAt(i);
    hash |= 0;
  }

  const heights: number[] = [];
  for (let i = 0; i < count; i++) {
    // Deterministic trigonometric pseudo-random generator
    const val = Math.abs(Math.sin(hash + (i + 1) * 2.3) * Math.cos(i * 0.7));
    // Scale height between 15% and 100%
    const heightPercent = Math.max(15, Math.min(100, Math.round(val * 100)));
    heights.push(heightPercent);
  }
  return heights;
}
