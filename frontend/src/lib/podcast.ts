// Canonical Podcast Episodes with verified audio streams
// Source: Hetzner production volume /mnt/HC_Volume_106932004/podcast/episodes/

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
  philosopher: string;
  title: string;
  thesis: string;
  description: string;
  audioUrl: string;
  audio_url?: string;
  mediaLink?: string;
  youtubeUrl?: string;
  telegramUrl?: string;
  durationSeconds: number;
  publishedAt: string;
  timecodes: PodcastTimecode[];
}

export const DEFAULT_CANONICAL_PODCASTS: PodcastEpisode[] = [
  {
    id: 1,
    slug: 'ruth-barcan-marcus',
    philosopher: 'Рут Баркан Маркус',
    title: 'Выпуск 01: Системы и реальность',
    thesis: 'Модальная логика и деонтические операторы в инженерных системах.',
    description: 'Фундаментальный разбор модальной логики и связности объектов.',
    audioUrl: 'https://cdn.dmitryefremov.com/podcasts/ep-01.mp3',
    durationSeconds: 1240,
    publishedAt: '2026-10-01',
    timecodes: [
      { id: 101, timeOffsetSeconds: 0, title: 'Введение в деонтическую логику', description: 'Базовые операторы O(p) и P(p)' },
      { id: 102, timeOffsetSeconds: 340, title: 'Формула Баркан', description: 'Связь модальностей и кванторов' }
    ]
  },
  {
    id: 2,
    slug: 'paul-thagard',
    philosopher: 'Пол Таггард',
    title: 'Выпуск 02: Понятийные структуры',
    thesis: 'Когнитивная согласованность и мыслительные карты.',
    description: 'Разбор концептуальных моделей согласованности.',
    audioUrl: 'https://cdn.dmitryefremov.com/podcasts/ep-02.mp3',
    durationSeconds: 1560,
    publishedAt: '2026-10-05',
    timecodes: [
      { id: 201, timeOffsetSeconds: 0, title: 'Введение', description: 'Понятие когнитивной согласованности' }
    ]
  },
  {
    id: 3,
    slug: 'susan-hurley',
    philosopher: 'Сюзан Хёрли',
    title: 'Выпуск 03: Сознание и действие',
    thesis: 'Расширенный разум и когнитивное сопряжение.',
    description: 'Концепция встроенности разума в среду.',
    audioUrl: 'https://cdn.dmitryefremov.com/podcasts/ep-03.mp3',
    durationSeconds: 1800,
    publishedAt: '2026-10-10',
    timecodes: [
      { id: 301, timeOffsetSeconds: 0, title: 'Введение', description: 'Внешняя память и интерфейсы' }
    ]
  }
];

export const PODCAST_EPISODES: PodcastEpisode[] = [];

export async function fetchPodcastsFromApi(customFetch = fetch): Promise<PodcastEpisode[]> {
  try {
    const res = await customFetch('/api/v1/podcasts');
    if (!res.ok) return DEFAULT_CANONICAL_PODCASTS;
    const data = await res.json();
    const rawList = Array.isArray(data) ? data : (data?.content || []);
    const parsed = rawList
      .filter((ep: any) => {
        const title = (ep.title || '').toLowerCase();
        const slug = (ep.slug || '').toLowerCase();
        if (title.includes('teleprompter') || slug.includes('teleprompter') || title.includes('raw_') || title.includes('суфлёр')) {
          return false;
        }
        return true;
      })
      .map((ep: any) => ({
        id: ep.id,
        slug: ep.slug || `ep-${ep.id}`,
        philosopher: ep.philosopher || 'Дмитрий Ефремов',
        title: ep.title || 'Выпуск подкаста',
        thesis: ep.thesis || ep.description || '',
        description: ep.description || ep.thesis || '',
        audioUrl: ep.audioUrl || ep.audio_url || '',
        mediaLink: ep.mediaLink || ep.youtubeUrl || '',
        youtubeUrl: ep.youtubeUrl || ep.mediaLink || 'https://youtube.com/@dmitrii.efremov',
        telegramUrl: ep.telegramUrl || 'https://t.me/efremov_mvp',
        durationSeconds: ep.durationSeconds || 0,
        publishedAt: ep.publishedAt ? String(ep.publishedAt).slice(0, 10) : '',
        timecodes: Array.isArray(ep.timecodes) ? ep.timecodes : []
      }));
    return parsed.length > 0 ? parsed : DEFAULT_CANONICAL_PODCASTS;
  } catch {
    return DEFAULT_CANONICAL_PODCASTS;
  }
}

export function formatTime(seconds: number): string {
  if (!seconds || isNaN(seconds) || seconds < 0) return '00:00';
  const totalSeconds = Math.floor(seconds);
  const hours = Math.floor(totalSeconds / 3600);
  const mins = Math.floor((totalSeconds % 3600) / 60);
  const secs = totalSeconds % 60;
  if (hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
}

export function generateWaveformHeights(barsCount: number = 40, seed: string = 'default'): number[] {
  let h = 0;
  for (let i = 0; i < seed.length; i++) {
    h = (Math.imul(31, h) + seed.charCodeAt(i)) | 0;
  }
  const heights: number[] = [];
  for (let i = 0; i < barsCount; i++) {
    h = Math.sin(i * 0.45 + (h % 100)) * 10000;
    const norm = Math.abs(h - Math.floor(h));
    heights.push(Math.max(16, Math.floor(norm * 92)));
  }
  return heights;
}
