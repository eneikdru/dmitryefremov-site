// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import {
  getInitialSparks,
  filterSparks,
  createSpark,
  toggleSparkFire,
  toggleSparkBookmark,
  calculateTelemetry
} from './sparks';
import SparksView from '../components/SparksView.svelte';
import { mount, unmount } from 'svelte';

describe('Sparks Domain Model & Logic', () => {
  it('loads initial sparks dataset correctly', () => {
    const sparks = getInitialSparks();
    expect(sparks.length).toBeGreaterThan(0);
    expect(sparks[0].author).toBe('@alexp');
    expect(sparks[0].tag).toBe('#architecture');
  });

  it('filters sparks by category', () => {
    const sparks = getInitialSparks();

    const filteredModels = filterSparks(sparks, '#psychology');
    expect(filteredModels.length).toBe(1);
    expect(filteredModels[0].author).toBe('@rachel.v');

    const allSparks = filterSparks(sparks, 'All Sparks');
    expect(allSparks.length).toBe(sparks.length);
  });

  it('filters sparks by search query', () => {
    const sparks = getInitialSparks();

    const searchResult = filterSparks(sparks, 'All Sparks', 'complexity');
    expect(searchResult.length).toBe(1);
    expect(searchResult[0].id).toBe('spk-1');

    const authorSearch = filterSparks(sparks, 'All Sparks', '@dan_k');
    expect(authorSearch.length).toBe(1);
    expect(authorSearch[0].id).toBe('spk-4');
  });

  it('creates new spark correctly with trimmed length and default tag', () => {
    const newSpark = createSpark('Fast-stream layout for quantized thoughts');
    expect(newSpark.author).toBe('@dmitryefremov');
    expect(newSpark.tag).toBe('#heuristics');
    expect(newSpark.body).toBe('"Fast-stream layout for quantized thoughts"');
    expect(newSpark.isFired).toBe(true);
  });

  it('toggles spark fire status and increments/decrements spark count', () => {
    const sparks = getInitialSparks();
    const initialCount = sparks[0].sparksCount;

    const firedList = toggleSparkFire(sparks, 'spk-1');
    expect(firedList[0].isFired).toBe(true);
    expect(firedList[0].sparksCount).toBe(initialCount + 1);

    const unfiredList = toggleSparkFire(firedList, 'spk-1');
    expect(unfiredList[0].isFired).toBe(false);
    expect(unfiredList[0].sparksCount).toBe(initialCount);
  });

  it('toggles spark bookmark status', () => {
    const sparks = getInitialSparks();
    expect(sparks[0].isBookmarked).toBe(false);

    const bookmarkedList = toggleSparkBookmark(sparks, 'spk-1');
    expect(bookmarkedList[0].isBookmarked).toBe(true);
  });

  it('calculates stream telemetry metrics accurately', () => {
    const sparks = getInitialSparks();
    const metrics = calculateTelemetry(sparks);

    expect(metrics.sparksPerMinute).toBe(42);
    expect(metrics.throughputWpm).toBe(142);
    expect(metrics.consumedToday).toBe(84);
    expect(metrics.totalCount).toBe(5);
  });
});

describe('SparksView Svelte Component', () => {
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

  it('renders Sparks stream header, mode switcher, and stream feed', () => {
    component = mount(SparksView, { target });

    expect(target.textContent).toContain('SPARKS');
    expect(target.textContent).toContain('42 spk/m');
    expect(target.textContent).toContain('THROUGHPUT: 142 WPM');
    expect(target.textContent).toContain('@alexp');
    expect(target.textContent).toContain('Most software complexity isn\'t accidental or essential');
  });

  it('switches stream modes between HYPER and FOCUS', async () => {
    component = mount(SparksView, { target });

    const focusBtn = target.querySelector('#focusModeBtn') as HTMLButtonElement;
    expect(focusBtn).not.toBeNull();

    focusBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(focusBtn.getAttribute('aria-pressed')).toBe('true');
  });

  it('dispatches new spark thought through quick-spark floating composer', async () => {
    component = mount(SparksView, { target });

    const composerInput = target.querySelector('#spark-composer-input') as HTMLInputElement;
    const dispatchBtn = target.querySelector('#dispatch-spark-btn') as HTMLButtonElement;

    expect(composerInput).not.toBeNull();
    expect(dispatchBtn).not.toBeNull();

    composerInput.value = 'Architectural autonomy requires zero decision latency.';
    composerInput.dispatchEvent(new Event('input'));
    await new Promise(r => setTimeout(r, 0));

    dispatchBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('Architectural autonomy requires zero decision latency.');
  });
});
