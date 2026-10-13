// @vitest-environment jsdom
import { describe, it, expect, beforeEach, beforeAll, vi } from 'vitest';
import { mount, unmount, flushSync, tick } from 'svelte';
import { formatTime, generateWaveformHeights, PODCAST_EPISODES } from './podcast';
import PodcastPlayerView from '../components/PodcastPlayerView.svelte';

beforeAll(() => {
  let isPaused = true;
  Object.defineProperty(window.HTMLMediaElement.prototype, 'paused', {
    get() { return isPaused; },
    configurable: true
  });
  window.HTMLMediaElement.prototype.play = vi.fn().mockImplementation(function (this: HTMLAudioElement) {
    isPaused = false;
    this.dispatchEvent(new Event('play'));
    return Promise.resolve();
  });
  window.HTMLMediaElement.prototype.pause = vi.fn().mockImplementation(function (this: HTMLAudioElement) {
    isPaused = true;
    this.dispatchEvent(new Event('pause'));
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

describe('PodcastPlayerView Component UI Interactions & Audio Playback', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('renders episode title, catalog list, wave visualizer, timecodes, and external YouTube & Telegram links', () => {
    component = mount(PodcastPlayerView, { target });

    expect(target.textContent).toContain('Подкаст «Системы и реальность»');
    expect(target.textContent).toContain(PODCAST_EPISODES[0].title);
    expect(target.textContent).toContain('ВИЗУАЛИЗАЦИЯ ЗВУКОВОЙ ВОЛНЫ');
    expect(target.textContent).toContain('ТАЙМКОДЫ И ТЕМЫ');

    // Check timecodes rendered
    PODCAST_EPISODES[0].timecodes.forEach(tc => {
      expect(target.textContent).toContain(tc.title);
    });

    // Check external platform links (YouTube & Telegram)
    const ytLink = target.querySelector('a[aria-label="YouTube Channel"]') as HTMLAnchorElement;
    const tgLink = target.querySelector('a[aria-label="Telegram Channel"]') as HTMLAnchorElement;

    expect(ytLink).not.toBeNull();
    expect(ytLink.getAttribute('href')).toMatch(/^https:\/\/(www\.)?(youtube\.com|youtu\.be)/);
    expect(ytLink.getAttribute('target')).toBe('_blank');
    expect(ytLink.getAttribute('rel')).toBe('noopener noreferrer');

    expect(tgLink).not.toBeNull();
    expect(tgLink.getAttribute('href')).toMatch(/^https:\/\/t\.me/);
    expect(tgLink.getAttribute('target')).toBe('_blank');
    expect(tgLink.getAttribute('rel')).toBe('noopener noreferrer');
  });

  it('toggles play/pause state when clicking play button and interacts with audio element', async () => {
    component = mount(PodcastPlayerView, { target });
    await tick();
    let playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');

    playBtn.click();
    await tick();
    await new Promise(r => setTimeout(r, 10));
    await tick();

    playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;
    expect(playBtn.getAttribute('aria-label')).toBe('Pause Podcast Episode');

    playBtn.click();
    await tick();
    await new Promise(r => setTimeout(r, 10));
    await tick();

    playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;
    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');
  });

  it('handles audio playback completion (ended event) by resetting playback state', async () => {
    component = mount(PodcastPlayerView, { target });
    await tick();
    let playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;
    const audioElement = target.querySelector('audio') as HTMLAudioElement;

    playBtn.click();
    await tick();
    await new Promise(r => setTimeout(r, 10));
    await tick();

    playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;
    expect(playBtn.getAttribute('aria-label')).toBe('Pause Podcast Episode');

    audioElement.dispatchEvent(new Event('ended'));
    await tick();

    playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;
    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');
    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('00:00');
  });

  it('seeks directly to timestamp when a timecode button is clicked', () => {
    component = mount(PodcastPlayerView, { target });

    const targetTc = PODCAST_EPISODES[0].timecodes[1];
    const timecodeBtn = target.querySelector(`#timecode-btn-${targetTc.id}`) as HTMLButtonElement;
    expect(timecodeBtn).not.toBeNull();

    timecodeBtn.click();
    flushSync();

    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe(formatTime(targetTc.timeOffsetSeconds));
    expect(target.textContent).toContain('СЕЙЧАС ИГРАЕТ');
  });

  it('handles waveform keyboard navigation and component seekTo exported function', () => {
    component = mount(PodcastPlayerView, { target });
    const waveform = target.querySelector('#waveform-container') as HTMLElement;

    // Test exported seekTo function directly
    component.seekTo(620);
    flushSync();

    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    // 620 seconds = 10:20
    expect(currentTimeVal.textContent).toBe('10:20');

    // Test ArrowRight key down (adds 5 seconds -> 625 seconds = 10:25)
    waveform.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowRight', bubbles: true }));
    flushSync();
    expect(currentTimeVal.textContent).toBe('10:25');

    // Test ArrowLeft key down (subtracts 5 seconds -> 620 seconds = 10:20)
    waveform.dispatchEvent(new KeyboardEvent('keydown', { key: 'ArrowLeft', bubbles: true }));
    flushSync();
    expect(currentTimeVal.textContent).toBe('10:20');
  });

  it('switches active podcast episode when selecting from catalog', () => {
    component = mount(PodcastPlayerView, { target });

    const secondEp = PODCAST_EPISODES[1];
    const ep2Card = target.querySelector(`#episode-card-${secondEp.id}`) as HTMLButtonElement;
    expect(ep2Card).not.toBeNull();

    ep2Card.click();
    flushSync();

    const podcastTitle = target.querySelector('#podcast-title') as HTMLElement;
    expect(podcastTitle.textContent).toBe(secondEp.title);
    expect(target.textContent).toContain(secondEp.thesis);
  });
});
