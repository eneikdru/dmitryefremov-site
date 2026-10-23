// @vitest-environment jsdom
import { describe, it, expect, beforeEach, beforeAll, vi } from 'vitest';
import { mount, unmount, flushSync } from 'svelte';
import EpisodePlayerView from '../components/EpisodePlayerView.svelte';
import { PODCAST_EPISODES } from './podcast';

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

describe('EpisodePlayerView Component UI Interactions & Stream Player', () => {
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

  it('renders unified media player header, core theses, waveform scrubber, and live companion transcript', () => {
    component = mount(EpisodePlayerView, { target });

    expect(target.textContent).toContain('NOCTURNE DISPATCH');
    expect(target.textContent).toContain(PODCAST_EPISODES[0].title);
    expect(target.textContent).toContain('Core Theses');
    expect(target.textContent).toContain('Live Companion Transcript');
    expect(target.textContent).toContain('Trustless consensus shifts governance');
  });

  it('toggles play and pause on play button click', async () => {
    component = mount(EpisodePlayerView, { target });
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

  it('seeks accurately when clicking jump-to button on a thesis card or exported seekTo', () => {
    component = mount(EpisodePlayerView, { target });

    // Click jump to 14:28 on thesis 1
    const jumpBtn = target.querySelector('#thesis-card-thesis-01 button') as HTMLButtonElement;
    expect(jumpBtn).not.toBeNull();

    jumpBtn.click();
    flushSync();

    const currentTimeVal = target.querySelector('#current-time-val') as HTMLElement;
    expect(currentTimeVal.textContent).toBe('14:28');

    // Test exported seekTo function
    component.seekTo(1730); // 28:50
    flushSync();
    expect(currentTimeVal.textContent).toBe('28:50');
  });

  it('toggles thesis bookmark state when bookmark button is clicked', () => {
    component = mount(EpisodePlayerView, { target });

    const buttons = target.querySelectorAll('#thesis-card-thesis-01 button');
    const saveBtn = Array.from(buttons).find(b => b.textContent?.includes('Save Thesis') || b.textContent?.includes('Saved')) as HTMLButtonElement;
    expect(saveBtn).not.toBeNull();
    expect(saveBtn.textContent).toContain('Save Thesis');

    saveBtn.click();
    flushSync();

    expect(saveBtn.textContent).toContain('Saved');
  });

  it('handles playback speed toggle button', () => {
    component = mount(EpisodePlayerView, { target });
    const speedBtn = target.querySelector('#speed-btn') as HTMLButtonElement;

    expect(speedBtn.textContent).toContain('1×');

    speedBtn.click();
    flushSync();

    expect(speedBtn.textContent).toContain('1.25×');
  });
});
