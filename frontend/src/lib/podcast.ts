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

export async function fetchPodcastsFromApi(customFetch = fetch): Promise<PodcastEpisode[]> {
  try {
    const res = await customFetch('/api/v1/podcasts');
    if (!res.ok) return [];
    const data = await res.json();
    const rawList = Array.isArray(data) ? data : (data?.content || []);
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
        philosopher: ep.philosopher || '',
        title: ep.title || '',
        thesis: ep.thesis || ep.description || '',
        description: ep.description || ep.thesis || '',
        audioUrl: ep.audioUrl || ep.audio_url || '',
        mediaLink: ep.mediaLink || ep.youtubeUrl || '',
        youtubeUrl: ep.youtubeUrl || ep.mediaLink || '',
        telegramUrl: ep.telegramUrl || '',
        durationSeconds: ep.durationSeconds || 0,
        publishedAt: ep.publishedAt ? String(ep.publishedAt).slice(0, 10) : '',
        timecodes: Array.isArray(ep.timecodes) ? ep.timecodes : []
      }));
  } catch {
    return [];
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
