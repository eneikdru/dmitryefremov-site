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

export const PODCAST_EPISODES: PodcastEpisode[] = [];

export const CANONICAL_EPISODES: PodcastEpisode[] = [
  {
    id: 1,
    slug: '01-ruth-barcan-marcus',
    philosopher: 'Рут Баркан Маркус',
    title: 'Имя не равно описанию. Почему имя — это не описание человека?',
    thesis: 'Имя напрямую связывается с объектом, а не через набор признаков или контекстуальные описания. Принципы не сводятся к сиюминутной выгоде.',
    description: 'Разбор модальной логики, формулы Баркан и природы жесткой референции. Почему попытка свести сущность человека или контрактную обязанность системы к временному описанию разрушает преемственность ответственности.',
    durationSeconds: 1780,
    publishedAt: '2026-09-25',
    audioUrl: '/audio/01-ruth-barcan-marcus.m4a',
    youtubeUrl: 'https://youtu.be/1qiPT9UxKNM',
    mediaLink: 'https://youtu.be/1qiPT9UxKNM',
    telegramUrl: 'https://t.me/efremov_mvp',
    timecodes: [
      { id: 101, timeOffsetSeconds: 0, title: 'Введение: имя не равно описанию' },
      { id: 102, timeOffsetSeconds: 255, title: 'Прямая референция и жесткие десигнаторы' },
      { id: 103, timeOffsetSeconds: 510, title: 'Формула Баркан: необходимость и возможность' },
      { id: 104, timeOffsetSeconds: 765, title: 'Модальная логика контрактов и системных обязательств' },
      { id: 105, timeOffsetSeconds: 920, title: 'Моральные дилеммы и неизгладимый остаток обязательств' },
      { id: 106, timeOffsetSeconds: 1270, title: 'Почему агент не может переписать свои обещания' }
    ]
  },
  {
    id: 2,
    slug: '02-paul-thagard',
    philosopher: 'Пол Таггард',
    title: 'Почему логичная ложь побеждает факты?',
    thesis: 'Стройность и связность гипотезы не гарантируют её истинность. Красивая ложь побеждает факты, если из картины методично исключены неудобные свидетельства.',
    description: 'Вычислительная эпистемология, модель когерентности ECHO и архитектура AIMS. Как отличить научную гипотезу от замкнутой пропагандистской картины мира и почему вопрос «Что могло бы меня опровергнуть?» является главным тестом на честность.',
    durationSeconds: 1850,
    publishedAt: '2026-10-01',
    audioUrl: '/audio/02-paul-thagard.m4a',
    youtubeUrl: 'https://youtu.be/jUbOF_YvkfI',
    mediaLink: 'https://youtu.be/jUbOF_YvkfI',
    telegramUrl: 'https://t.me/efremov_mvp',
    timecodes: [
      { id: 201, timeOffsetSeconds: 36, title: 'Право проверить картину мира' },
      { id: 202, timeOffsetSeconds: 215, title: 'ECHO: вычислительная сеть объяснений' },
      { id: 203, timeOffsetSeconds: 300, title: 'Связность не равна истине' },
      { id: 204, timeOffsetSeconds: 383, title: 'AIMS: четыре процесса информации' },
      { id: 205, timeOffsetSeconds: 535, title: 'Цензура отключает наблюдение' },
      { id: 206, timeOffsetSeconds: 982, title: 'Пропаганда склеивает ложь' },
      { id: 207, timeOffsetSeconds: 1138, title: 'Эмоции удерживают веру' },
      { id: 208, timeOffsetSeconds: 1331, title: 'Патриархат как сеть власти' },
      { id: 209, timeOffsetSeconds: 1666, title: 'Что могло бы меня опровергнуть?' },
      { id: 210, timeOffsetSeconds: 1760, title: 'Конкурирующие гипотезы в реальном бизнесе' }
    ]
  },
  {
    id: 3,
    slug: '03-susan-hurley',
    philosopher: 'Сьюзан Хёрли',
    title: 'Почему мы копируем других? Где заканчивается подражание и начинается ваше решение?',
    thesis: 'Сознание действует в мире, а не заперто в черепной коробке. Действие непрерывно учится у сенсомоторного отклика среды, а не у пассивного созерцания правил.',
    description: 'Теория воплощенного познания, модель общих контуров (Shared Circuits Model) и различие между механическим копированием и подлинным авторством действия. Почему свобода воли возникает в петле обратной связи со средой.',
    durationSeconds: 2060,
    publishedAt: '2026-10-08',
    audioUrl: '/audio/03-susan-hurley.m4a',
    youtubeUrl: 'https://youtu.be/mLJm6YKm0ds',
    mediaLink: 'https://youtu.be/mLJm6YKm0ds',
    telegramUrl: 'https://t.me/efremov_mvp',
    timecodes: [
      { id: 301, timeOffsetSeconds: 60, title: 'Право быть автором мысли' },
      { id: 302, timeOffsetSeconds: 250, title: 'Причины не свести к одной шкале' },
      { id: 303, timeOffsetSeconds: 605, title: 'Сознание действует в мире' },
      { id: 304, timeOffsetSeconds: 700, title: 'Действие учится у отклика' },
      { id: 305, timeOffsetSeconds: 975, title: 'Подражание не равно копированию' },
      { id: 306, timeOffsetSeconds: 1170, title: 'Где начинается своё решение?' },
      { id: 307, timeOffsetSeconds: 1520, title: 'Пропаганда стирает авторство' },
      { id: 308, timeOffsetSeconds: 1680, title: 'Когда государство становится ордой' },
      { id: 309, timeOffsetSeconds: 1825, title: 'Патриархат распределяет роли' },
      { id: 310, timeOffsetSeconds: 1920, title: 'Компания учит повторять' }
    ]
  },
  {
    id: 4,
    slug: '04-gilbert-ryle',
    philosopher: 'Гилберт Райл',
    title: 'Знать — не значит уметь. Почему инструкции не спасают?',
    thesis: 'Категориальная ошибка — искать «призрак в машине». Знание как практический навык (knowing-how) первично по отношению к знанию формулировок и регламентов (knowing-that).',
    description: 'Разоблачение декартовского дуализма и бесконечного регресса мета-правил. Почему регламенты не спасают бизнес при кризисе, а интеллект проявляется непосредственно в действии под реальной нагрузкой.',
    durationSeconds: 2045,
    publishedAt: '2026-10-16',
    audioUrl: '/audio/04-gilbert-ryle.m4a',
    youtubeUrl: 'https://youtu.be/Pj5_BBLTiOM',
    mediaLink: 'https://youtu.be/Pj5_BBLTiOM',
    telegramUrl: 'https://t.me/efremov_mvp',
    timecodes: [
      { id: 401, timeOffsetSeconds: 74, title: 'Мышление происходит в действии' },
      { id: 402, timeOffsetSeconds: 128, title: 'Категориальная ошибка' },
      { id: 403, timeOffsetSeconds: 299, title: 'Навык раньше инструкции' },
      { id: 404, timeOffsetSeconds: 523, title: 'Бесконечный регресс правил' },
      { id: 405, timeOffsetSeconds: 652, title: 'Алгоритм не несет риска' },
      { id: 406, timeOffsetSeconds: 880, title: 'Вес аргумента важнее статуса' },
      { id: 407, timeOffsetSeconds: 1200, title: 'Свойства видны под нагрузкой' },
      { id: 408, timeOffsetSeconds: 1370, title: 'Регламенты не спасают бизнес' },
      { id: 409, timeOffsetSeconds: 1650, title: 'Насыщенное описание реальности' },
      { id: 410, timeOffsetSeconds: 1858, title: 'Код не бывает нейтральным' }
    ]
  }
];

export async function fetchPodcastsFromApi(customFetch = fetch): Promise<PodcastEpisode[]> {
  try {
    const res = await customFetch('/api/v1/podcasts');
    if (!res.ok) return CANONICAL_EPISODES;
    const data = await res.json();
    const rawList = Array.isArray(data) ? data : (data?.content || []);
    if (rawList.length === 0) return CANONICAL_EPISODES;
    return rawList
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
        youtubeUrl: ep.youtubeUrl || ep.mediaLink || 'https://www.youtube.com/@dmitrii.efremov',
        telegramUrl: ep.telegramUrl || 'https://t.me/efremov_mvp',
        durationSeconds: ep.durationSeconds || 0,
        publishedAt: ep.publishedAt ? String(ep.publishedAt).slice(0, 10) : '',
        timecodes: Array.isArray(ep.timecodes) ? ep.timecodes : []
      }));
  } catch {
    return CANONICAL_EPISODES;
  }
}

export function formatTime(seconds: number): string {
  if (isNaN(seconds) || seconds < 0) return '00:00';
  const total = Math.floor(seconds);
  const hours = Math.floor(total / 3600);
  const minutes = Math.floor((total % 3600) / 60);
  const secs = total % 60;

  const mm = String(minutes).padStart(2, '0');
  const ss = String(secs).padStart(2, '0');

  if (hours > 0) {
    const hh = String(hours).padStart(2, '0');
    return `${hh}:${mm}:${ss}`;
  }
  return `${mm}:${ss}`;
}

export function generateWaveformHeights(barsCount: number = 40, seed: string = 'default'): number[] {
  let hash = 0;
  for (let i = 0; i < seed.length; i++) {
    hash = (hash << 5) - hash + seed.charCodeAt(i);
    hash |= 0;
  }

  const heights: number[] = [];
  for (let i = 0; i < barsCount; i++) {
    const x = Math.sin(hash + i * 0.75) * 10000;
    const pseudoRandom = x - Math.floor(x);
    const height = Math.floor(15 + pseudoRandom * 85);
    heights.push(height);
  }
  return heights;
}
