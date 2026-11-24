import { describe, expect, it } from 'vitest';
import { fetchEssaysFromApi } from './essays';

describe('essay catalog ordering', () => {
  it('shows a newly published API essay before older built-in essays', async () => {
    const customFetch = async () => ({
      ok: true,
      json: async () => ({
        content: [{
          id: 99,
          slug: 'new-public-essay',
          title: 'New public essay',
          summary: 'Visible in the storefront',
          content: '# New public essay',
          publishedAt: '2026-11-07T18:21:55Z'
        }]
      })
    }) as Response;

    const essays = await fetchEssaysFromApi(customFetch as typeof fetch);

    expect(essays[0].slug).toBe('new-public-essay');
    expect(essays[0].publishedAt).toBe('2026-11-07');
  });
});
