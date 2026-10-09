// @vitest-environment jsdom
import { describe, it, expect, beforeEach, beforeAll } from 'vitest';
import { mount, unmount } from 'svelte';
import { formatTime, generateWaveformHeights, PODCAST_EPISODES } from './podcast';
import PodcastPlayerView from '../components/PodcastPlayerView.svelte';

beforeAll(() => {
  window.HTMLMediaElement.prototype.play = () => Promise.resolve();
  window.HTMLMediaElement.prototype.pause = () => {};
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

describe('PodcastPlayerView Component UI Interactions', () => {
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

  it('renders episode title, catalog list, wave visualizer, and timecodes', () => {
    component = mount(PodcastPlayerView, { target });

    expect(target.textContent).toContain('«Системы и реальность» Podcast');
    expect(target.textContent).toContain(PODCAST_EPISODES[0].title);
    expect(target.textContent).toContain('WAVE_VISUALIZATION');
    expect(target.textContent).toContain('TIMECODES & TOPICS');

    // Check timecodes rendered
    PODCAST_EPISODES[0].timecodes.forEach(tc => {
      expect(target.textContent).toContain(tc.title);
    });
  });

  it('toggles play/pause state when clicking play button', async () => {
    component = mount(PodcastPlayerView, { target });
    const playBtn = target.querySelector('#play-pause-btn') as HTMLButtonElement;

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');

    playBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(playBtn.getAttribute('aria-label')).toBe('Pause Podcast Episode');

    playBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(playBtn.getAttribute('aria-label')).toBe('Play Podcast Episode');
  });

  it('seeks directly to timestamp when a timecode button is clicked', async () => {
    component = mount(PodcastPlayerView, { target });

    // Timecode 2 for Episode 01 is 195 seconds ("03:15")
    const targetTc = PODCAST_EPISODES[0].timecodes[1];
    const timecodeBtn = target.querySelector(`#timecode-btn-${targetTc.id}`) as HTMLButtonElement;
    expect(timecodeBtn).not.toBeNull();

    timecodeBtn.click();
    await new Promise(r => setTimeout(r, 0));

    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('03:15');
    expect(target.textContent).toContain('NOW_PLAYING');
  });

  it('switches active podcast episode when selecting from catalog', async () => {
    component = mount(PodcastPlayerView, { target });

    const secondEp = PODCAST_EPISODES[1];
    const ep2Card = target.querySelector(`#episode-card-${secondEp.id}`) as HTMLButtonElement;
    expect(ep2Card).not.toBeNull();

    ep2Card.click();
    await new Promise(r => setTimeout(r, 0));

    const podcastTitle = target.querySelector('#podcast-title') as HTMLElement;
    expect(podcastTitle.textContent).toBe(secondEp.title);
    expect(target.textContent).toContain(secondEp.thesis);
  });
});
