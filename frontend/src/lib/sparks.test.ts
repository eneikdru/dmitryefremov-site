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

const sampleSparksList = [
  {
    id: 'spk-1',
    author: '@efremov_mvp',
    authorInitial: 'E',
    timestamp: '14m ago',
    tag: '#architecture',
    type: 'insight' as const,
    sparksCount: 14,
    readTime: '3s',
    body: '"Most software complexity isn\'t accidental or essential; it\'s social structure fossilized in code."',
    isFired: false,
    isBookmarked: false
  },
  {
    id: 'spk-2',
    author: '@efremov_mvp',
    authorInitial: 'E',
    timestamp: '48m ago',
    tag: '#psychology',
    type: 'insight' as const,
    sparksCount: 8,
    body: '"Remove step 2. Collapse decision latencies into zero."',
    isFired: false,
    isBookmarked: false
  }
];

describe('Sparks Domain Model & Logic', () => {
  it('loads initial sparks dataset correctly as empty', () => {
    const sparks = getInitialSparks();
    expect(sparks).toHaveLength(0);
  });

  it('filters sparks by category', () => {
    const filteredModels = filterSparks(sampleSparksList, '#psychology');
    expect(filteredModels.length).toBe(1);
    expect(filteredModels[0].id).toBe('spk-2');

    const allSparks = filterSparks(sampleSparksList, 'All Sparks');
    expect(allSparks.length).toBe(sampleSparksList.length);
  });

  it('filters sparks by search query', () => {
    const searchResult = filterSparks(sampleSparksList, 'All Sparks', 'complexity');
    expect(searchResult.length).toBe(1);
    expect(searchResult[0].id).toBe('spk-1');
  });

  it('creates new spark correctly with trimmed length and default tag', () => {
    const newSpark = createSpark('Fast-stream layout for quantized thoughts');
    expect(newSpark.author).toBe('@dmitryefremov');
    expect(newSpark.tag).toBe('#heuristics');
    expect(newSpark.body).toBe('"Fast-stream layout for quantized thoughts"');
    expect(newSpark.isFired).toBe(true);
  });

  it('toggles spark fire status and increments/decrements spark count', () => {
    const initialCount = sampleSparksList[0].sparksCount;

    const firedList = toggleSparkFire(sampleSparksList, 'spk-1');
    expect(firedList[0].isFired).toBe(true);
    expect(firedList[0].sparksCount).toBe(initialCount + 1);

    const unfiredList = toggleSparkFire(firedList, 'spk-1');
    expect(unfiredList[0].isFired).toBe(false);
    expect(unfiredList[0].sparksCount).toBe(initialCount);
  });

  it('toggles spark bookmark status', () => {
    expect(sampleSparksList[0].isBookmarked).toBe(false);

    const bookmarkedList = toggleSparkBookmark(sampleSparksList, 'spk-1');
    expect(bookmarkedList[0].isBookmarked).toBe(true);
  });

  it('calculates stream telemetry metrics accurately', () => {
    const metrics = calculateTelemetry(sampleSparksList);

    expect(metrics.totalCount).toBe(2);
    expect(metrics.consumedToday).toBe(2);
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

  it('renders Sparks stream header and localized Russian empty state when empty', () => {
    component = mount(SparksView, { target, props: { sparks: [] } });

    expect(target.textContent).toContain('SPARKS');
    expect(target.textContent).toContain('Искры и записи отсутствуют');
  });

  it('renders Sparks stream header, mode switcher, and stream feed with sample sparks', () => {
    component = mount(SparksView, { target, props: { sparks: sampleSparksList } });

    expect(target.textContent).toContain('SPARKS');
    expect(target.textContent).toContain('@efremov_mvp');
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
