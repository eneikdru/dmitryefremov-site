import { describe, it, expect } from 'vitest';
import { DEFAULT_CANON_CHAPTER, calculateReadProgress } from './canonReader';

describe('canonReader lib', () => {
  it('provides default canon chapter metadata', () => {
    expect(DEFAULT_CANON_CHAPTER.title).toContain('MEDITATIONS');
    expect(DEFAULT_CANON_CHAPTER.footnotes[1].term).toContain('Hēgemonikon');
  });

  it('calculates read progress accurately', () => {
    expect(calculateReadProgress(0, 1000, 500)).toBe(0);
    expect(calculateReadProgress(250, 1000, 500)).toBe(50);
    expect(calculateReadProgress(500, 1000, 500)).toBe(100);
    expect(calculateReadProgress(600, 1000, 500)).toBe(100);
  });
});
