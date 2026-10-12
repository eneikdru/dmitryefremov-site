// @vitest-environment jsdom
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import {
  INITIAL_CANON_ITEMS,
  filterCanonItems,
  fetchCanonItems,
  type CanonItem
} from './canon';

describe('Canon Library Unit Tests', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('contains seed canon items covering all 4 practical domains', () => {
    const domains = new Set(INITIAL_CANON_ITEMS.map(i => i.domain));
    expect(domains.has('AUTONOMOUS_ENGINEERING')).toBe(true);
    expect(domains.has('AGENTIC_ECONOMY')).toBe(true);
    expect(domains.has('PRAGMATIC_BUSINESS_STRATEGY')).toBe(true);
    expect(domains.has('PHILOSOPHY_OF_MIND')).toBe(true);
  });

  it('contains items of MANIFESTO, GLOSSARY_TERM, and ONTOLOGY types', () => {
    const types = new Set(INITIAL_CANON_ITEMS.map(i => i.type));
    expect(types.has('MANIFESTO')).toBe(true);
    expect(types.has('GLOSSARY_TERM')).toBe(true);
    expect(types.has('ONTOLOGY')).toBe(true);
  });

  it('correctly filters canon items by domain and type', () => {
    const autonomousManifestos = filterCanonItems(
      INITIAL_CANON_ITEMS,
      'AUTONOMOUS_ENGINEERING',
      'MANIFESTO'
    );
    expect(autonomousManifestos.length).toBeGreaterThan(0);
    expect(autonomousManifestos.every(i => i.domain === 'AUTONOMOUS_ENGINEERING' && i.type === 'MANIFESTO')).toBe(true);

    const mindItems = filterCanonItems(INITIAL_CANON_ITEMS, 'PHILOSOPHY_OF_MIND', 'ALL');
    expect(mindItems.every(i => i.domain === 'PHILOSOPHY_OF_MIND')).toBe(true);
  });

  it('fetches canon items from API endpoint when available', async () => {
    const mockApiItems: CanonItem[] = [
      {
        slug: 'api-canon-1',
        title: 'API Canon Item',
        type: 'MANIFESTO',
        domain: 'AUTONOMOUS_ENGINEERING',
        content: 'API content'
      }
    ];

    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({
        content: mockApiItems,
        page: 0,
        size: 20,
        totalElements: 1,
        totalPages: 1
      })
    } as any);

    const result = await fetchCanonItems('AUTONOMOUS_ENGINEERING');
    expect(result).toEqual(mockApiItems);
  });

  it('falls back to initial in-memory items when API fetch fails', async () => {
    global.fetch = vi.fn().mockRejectedValue(new Error('Network error'));

    const result = await fetchCanonItems('AUTONOMOUS_ENGINEERING');
    expect(result.length).toBeGreaterThan(0);
    expect(result.every(i => i.domain === 'AUTONOMOUS_ENGINEERING')).toBe(true);
  });
});
