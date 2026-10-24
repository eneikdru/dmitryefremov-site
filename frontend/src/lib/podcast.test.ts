// @vitest-environment jsdom
import { describe, it, expect, beforeEach, beforeAll, vi } from 'vitest';
import { mount, unmount, flushSync } from 'svelte';
import { formatTime, generateWaveformHeights, PODCAST_EPISODES } from './podcast';
import PodcastPlayerView from '../components/PodcastPlayerView.svelte';
import { telemetry } from './telemetry';
import { setConsentStatus, resetConsentStatus } from './consent';

beforeAll(() => {
  Object.defineProperty(window.HTMLMediaElement.prototype, 'paused', {
    get(this: any) {
      return this._paused !== undefined ? this._paused : true;
    },
    configurable: true
  });

  window.HTMLMediaElement.prototype.play = vi.fn().mockImplementation(function (this: any) {
    this._paused = false;
    return Promise.resolve();
  });

  window.HTMLMediaElement.prototype.pause = vi.fn().mockImplementation(function (this: any) {
    this._paused = true;
  });
});

describe('Podcast Helpers & Utilities', () => {
  describe('formatTime', () => {
    it('formats seconds under 1 minute as MM:SS', () => {
      expect(formatTime(0)).toBe('00:00');
      expect(formatTime(5)).toBe('00:05');
      expect(formatTime(45)).toBe('00:45');
    });

    it('formats minutes and seconds as MM:SS', () => {
      expect(formatTime(60)).toBe('01:00');
      expect(formatTime(195)).toBe('03:15');
      expect(formatTime(1240)).toBe('20:40');
    });

    it('formats hours, minutes, and seconds as HH:MM:SS when duration exceeds 1 hour', () => {
      expect(formatTime(3600)).toBe('01:00:00');
      expect(formatTime(3665)).toBe('01:01:05');
      expect(formatTime(7322)).toBe('02:02:02');
    });

    it('handles negative, fractional or invalid numeric inputs safely', () => {
      expect(formatTime(-10)).toBe('00:00');
      expect(formatTime(NaN)).toBe('00:00');
      expect(formatTime(45.8)).toBe('00:45');
    });
  });

  describe('generateWaveformHeights', () => {
    it('generates the specified count of wave bar heights', () => {
      const heights = generateWaveformHeights(40, 'test-seed');
      expect(heights).toHaveLength(40);
    });

    it('produces deterministic heights for the same seed', () => {
      const heights1 = generateWaveformHeights(30, 'ep-01-seed');
      const heights2 = generateWaveformHeights(30, 'ep-01-seed');
      expect(heights1).toEqual(heights2);
    });

    it('bounds bar heights strictly between 15% and 100%', () => {
      const heights = generateWaveformHeights(50, 'random-slug-check');
      heights.forEach(h => {
        expect(h).toBeGreaterThanOrEqual(15);
        expect(h).toBeLessThanOrEqual(100);
      });
    });
  });
});

const sampleEpisodes = [
  {
    id: 1,
    slug: '01-test-episode',
    philosopher: 'Тестовый Философ',
    title: 'Тестовый выпуск подкаста: Модальная логика',
    thesis: 'Тезис для проверки компонентов',
    description: 'Полное описание выпуска подкаста',
    durationSeconds: 1800,
    publishedAt: '2026-09-20',
    audioUrl: '/audio/01-test.m4a',
    youtubeUrl: 'https://youtu.be/1qiPT9UxKNM',
    mediaLink: 'https://youtu.be/1qiPT9UxKNM',
    telegramUrl: 'https://t.me/efremov_mvp',
    timecodes: [
      { id: 101, timeOffsetSeconds: 0, title: 'Введение' },
      { id: 102, timeOffsetSeconds: 340, title: 'Формула Баркан' }
    ]
  },
  {
    id: 2,
    slug: '02-test-episode',
    philosopher: 'Второй Философ',
    title: 'Второй выпуск подкаста',
    thesis: 'Второй тезис',
    description: 'Описание второго выпуска',
    durationSeconds: 1200,
    publishedAt: '2026-09-22',
    audioUrl: '/audio/02-test.m4a',
    youtubeUrl: 'https://youtu.be/jUbOF_YvkfI',
    mediaLink: 'https://youtu.be/jUbOF_YvkfI',
    telegramUrl: 'https://t.me/efremov_mvp',
    timecodes: []
  }
];

describe('PodcastPlayerView Component UI Interactions & Audio Playback', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    resetConsentStatus();
    setConsentStatus('accepted');
    telemetry.clearEvents();
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('renders localized Russian empty state when episodes list is empty', () => {
    component = mount(PodcastPlayerView, { target, props: { episodes: [] } });
    flushSync();
    expect(target.textContent).toContain('Выпуски подкаста отсутствуют');
  });

  it('dispatches outbound_click telemetry events when YouTube and Telegram links are clicked', () => {
    component = mount(PodcastPlayerView, { target, props: { episodes: sampleEpisodes } });

    const ytLink = target.querySelector('a[aria-label="YouTube Channel"]') as HTMLAnchorElement;
    const tgLink = target.querySelector('a[aria-label="Telegram Channel"]') as HTMLAnchorElement;

    ytLink.click();
    tgLink.click();

    const events = telemetry.getEvents().filter(e => e.type === 'outbound_click');
    expect(events).toHaveLength(2);
    expect(events[0].payload.platform).toBe('YouTube');
    expect(events[1].payload.platform).toBe('Telegram');
  });

  it('renders episode title, catalog list, wave visualizer, timecodes, and external YouTube & Telegram links', () => {
    component = mount(PodcastPlayerView, { target, props: { episodes: sampleEpisodes } });

    expect(target.textContent).toContain('Подкаст «Системы и реальность»');
    expect(target.textContent).toContain(sampleEpisodes[0].title);
    expect(target.textContent).toContain('ВИЗУАЛИЗАЦИЯ ЗВУКОВОЙ ВОЛНЫ');
    expect(target.textContent).toContain('ТАЙМКОДЫ И ТЕМЫ');

    sampleEpisodes[0].timecodes.forEach(tc => {
      expect(target.textContent).toContain(tc.title);
    });

    const ytLink = target.querySelector('a[aria-label="YouTube Channel"]') as HTMLAnchorElement;
    const tgLink = target.querySelector('a[aria-label="Telegram Channel"]') as HTMLAnchorElement;

    expect(ytLink).not.toBeNull();
    expect(ytLink.getAttribute('href')).toMatch(/^https:\/\/youtu\.be|^https:\/\/youtube\.com/);
    expect(tgLink).not.toBeNull();
    expect(tgLink.getAttribute('href')).toMatch(/^https:\/\/t\.me/);
  });

  it('toggles play/pause state when clicking play button and interacts with audio element', async () => {
    component = mount(PodcastPlayerView, { target, props: { episodes: sampleEpisodes } });
    flushSync();
    const playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');

    playBtn.click();
    await Promise.resolve();
    flushSync();

    expect(playBtn.getAttribute('aria-label')).toBe('Pause Podcast Episode');
    expect(window.HTMLMediaElement.prototype.play).toHaveBeenCalled();

    playBtn.click();
    flushSync();

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');
    expect(window.HTMLMediaElement.prototype.pause).toHaveBeenCalled();
  });

  it('handles audio playback completion (ended event) by resetting playback state', async () => {
    component = mount(PodcastPlayerView, { target, props: { episodes: sampleEpisodes } });
    flushSync();
    const playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;
    const audioElement = target.querySelector('audio') as HTMLAudioElement;

    playBtn.click();
    await Promise.resolve();
    flushSync();
    expect(playBtn.getAttribute('aria-label')).toBe('Pause Podcast Episode');

    audioElement.dispatchEvent(new Event('ended'));
    flushSync();

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');
    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('00:00');
  });

  it('seeks directly to timestamp when a timecode button is clicked', () => {
    component = mount(PodcastPlayerView, { target, props: { episodes: sampleEpisodes } });

    const targetTc = sampleEpisodes[0].timecodes[1];
    const timecodeBtn = target.querySelector(`#timecode-btn-${targetTc.id}`) as HTMLButtonElement;
    expect(timecodeBtn).not.toBeNull();

    timecodeBtn.click();
    flushSync();

    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('05:40');
    expect(target.textContent).toContain('СЕЙЧАС ИГРАЕТ');
  });

  it('handles waveform keyboard navigation and component seekTo exported function', () => {
    component = mount(PodcastPlayerView, { target, props: { episodes: sampleEpisodes } });
    const waveform = target.querySelector('#waveform-container') as HTMLElement;

    component.seekTo(620);
    flushSync();

    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('10:20');

    waveform.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight', bubbles: true }));
    flushSync();
    expect(currentTimeVal.textContent).toBe('10:25');

    waveform.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowLeft', bubbles: true }));
    flushSync();
    expect(currentTimeVal.textContent).toBe('10:20');
  });

  it('switches active podcast episode when selecting from catalog', () => {
    component = mount(PodcastPlayerView, { target, props: { episodes: sampleEpisodes } });

    const secondEp = sampleEpisodes[1];
    const ep2Card = target.querySelector(`#episode-card-${secondEp.id}`) as HTMLButtonElement;
    expect(ep2Card).not.toBeNull();

    ep2Card.click();
    flushSync();

    const podcastTitle = target.querySelector('#podcast-title') as HTMLElement;
    expect(podcastTitle.textContent).toBe(secondEp.title);
    expect(target.textContent).toContain(secondEp.thesis);
  });
});
