// @vitest-environment jsdom
import { describe, it, expect, beforeEach, beforeAll, vi } from 'vitest';
import { mount, unmount, flushSync } from 'svelte';
import { formatTime, generateWaveformHeights, PODCAST_EPISODES } from './podcast';
import PodcastPlayerView from '../components/PodcastPlayerView.svelte';

beforeAll(() => {
  window.HTMLMediaElement.prototype.play = vi.fn().mockImplementation(() => Promise.resolve());
  window.HTMLMediaElement.prototype.pause = vi.fn();
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

    expect(target.textContent).toContain('«Системы и реальность» Podcast');
    expect(target.textContent).toContain(PODCAST_EPISODES[0].title);
    expect(target.textContent).toContain('WAVE_VISUALIZATION');
    expect(target.textContent).toContain('TIMECODES & TOPICS');

    // Check timecodes rendered
    PODCAST_EPISODES[0].timecodes.forEach(tc => {
      expect(target.textContent).toContain(tc.title);
    });

    // Check external platform links (YouTube & Telegram)
    const ytLink = target.querySelector('a[aria-label="YouTube Channel"]') as HTMLAnchorElement;
    const tgLink = target.querySelector('a[aria-label="Telegram Channel"]') as HTMLAnchorElement;

    expect(ytLink).not.toBeNull();
    expect(ytLink.getAttribute('href')).toMatch(/^https:\/\/youtube\.com/);
    expect(ytLink.getAttribute('target')).toBe('_blank');
    expect(ytLink.getAttribute('rel')).toBe('noopener noreferrer');

    expect(tgLink).not.toBeNull();
    expect(tgLink.getAttribute('href')).toMatch(/^https:\/\/t\.me/);
    expect(tgLink.getAttribute('target')).toBe('_blank');
    expect(tgLink.getAttribute('rel')).toBe('noopener noreferrer');
  });

  it('toggles play/pause state when clicking play button and interacts with audio element', () => {
    component = mount(PodcastPlayerView, { target });
    const playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');

    playBtn.click();
    flushSync();

    expect(playBtn.getAttribute('aria-label')).toBe('Pause Podcast Episode');

    playBtn.click();
    flushSync();

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');
  });

  it('handles audio playback completion (ended event) by resetting playback state', () => {
    component = mount(PodcastPlayerView, { target });
    const playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;
    const audioElement = target.querySelector('audio') as HTMLAudioElement;

    playBtn.click();
    flushSync();
    expect(playBtn.getAttribute('aria-label')).toBe('Pause Podcast Episode');

    audioElement.dispatchEvent(new Event('ended'));
    flushSync();

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');
    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('00:00');
  });

  it('seeks directly to timestamp when a timecode button is clicked', () => {
    component = mount(PodcastPlayerView, { target });

    // Timecode 2 for Episode 01 is 195 seconds ("03:15")
    const targetTc = PODCAST_EPISODES[0].timecodes[1];
    const timecodeBtn = target.querySelector(`#timecode-btn-${targetTc.id}`) as HTMLButtonElement;
    expect(timecodeBtn).not.toBeNull();

    timecodeBtn.click();
    flushSync();

    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('03:15');
    expect(target.textContent).toContain('NOW_PLAYING');
  });

  it('seeks directly to thesis timestamp when a thesis seek button is clicked', () => {
    component = mount(PodcastPlayerView, { target });

    const ep1 = PODCAST_EPISODES[0];
    const firstThesis = ep1.coreTheses![0]; // timestamp 525s = "08:45"

    const seekBtn = target.querySelector(`#thesis-seek-btn-${firstThesis.id}`) as HTMLButtonElement;
    expect(seekBtn).not.toBeNull();

    seekBtn.click();
    flushSync();

    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('08:45');

    const thesisCard = target.querySelector(`#thesis-card-${firstThesis.id}`) as HTMLElement;
    expect(thesisCard.textContent).toContain('NOW PLAYING');
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

  it('complies with WCAG 2.1 AA accessibility properties and aria roles', () => {
    component = mount(PodcastPlayerView, { target });

    const waveform = target.querySelector('#waveform-container');
    expect(waveform?.getAttribute('role')).toBe('slider');
    expect(waveform?.getAttribute('aria-label')).toBe('Audio Waveform Visualization Progress Bar');

    const tabList = target.querySelector('[role="tablist"]');
    expect(tabList).not.toBeNull();

    const thesesTab = target.querySelector('#tab-btn-theses');
    expect(thesesTab?.getAttribute('role')).toBe('tab');
    expect(thesesTab?.getAttribute('aria-selected')).toBe('true');
  });
});
