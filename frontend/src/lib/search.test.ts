import { describe, it, expect, vi } from 'vitest';
import {
  parseWikiLinks,
  highlightMatches,
  searchCorpus,
  fetchCorpusData,
  DEMO_CORPUS_FALLBACK
} from './search';

describe('search module', () => {
  it('parses wiki-links correctly from text', () => {
    const text = 'Here is a reference to [[Leader Election]] and another to [[Deontic Logic]].';
    const links = parseWikiLinks(text);
    expect(links).toEqual(['Leader Election', 'Deontic Logic']);
  });

  it('returns empty array when no wiki-links exist', () => {
    expect(parseWikiLinks('Plain text with no brackets')).toEqual([]);
    expect(parseWikiLinks('')).toEqual([]);
  });

  it('highlights search query matches with mark tags preserving case', () => {
    const highlighted = highlightMatches('Leader Election & Term Logic', 'election');
    expect(highlighted).toContain('<mark class=');
    expect(highlighted).toContain('Election</mark>');
  });

  it('escapes HTML special characters in highlightMatches', () => {
    const highlighted = highlightMatches('<script>alert("xss")</script>', 'script');
    expect(highlighted).not.toContain('<script>');
    expect(highlighted).toContain('&lt;');
  });

  it('searches corpus and ranks results by relevance', () => {
    const results = searchCorpus('election', 'All', DEMO_CORPUS_FALLBACK);
    expect(results.length).toBeGreaterThan(0);
    expect(results[0].title.toLowerCase()).toContain('election');
    expect(results[0].score).toBeGreaterThan(0);
  });

  it('filters search results by category', () => {
    const conceptResults = searchCorpus('', 'Concepts', DEMO_CORPUS_FALLBACK);
    const podcastResults = searchCorpus('', 'Podcasts', DEMO_CORPUS_FALLBACK);

    expect(conceptResults.every(r => r.type === 'concept' || r.type === 'essay')).toBe(true);
    expect(podcastResults.every(r => r.type === 'podcast')).toBe(true);
  });

  it('fetches corpus data with graceful fallback when API fails or returns empty', async () => {
    const customFetch = vi.fn().mockRejectedValue(new Error('Network error'));
    const corpus = await fetchCorpusData(customFetch);
    expect(corpus.canonItems.length).toBeGreaterThan(0);
    expect(corpus.podcasts.length).toBeGreaterThan(0);
  });
});
