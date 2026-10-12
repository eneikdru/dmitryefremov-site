import { describe, it, expect, beforeEach } from 'vitest';
import {
  DEFAULT_SEO_STATE,
  injectOpenGraphMeta,
  injectJsonLdSchema,
  removeOpenGraphMeta,
  removeJsonLdSchema,
  validateMetaTags,
  simulateBotCrawl
} from './seo';

describe('Semantic SEO & Open Graph Meta Utilities', () => {
  beforeEach(() => {
    document.head.innerHTML = '';
  });

  it('should inject Open Graph meta tags into document head', () => {
    injectOpenGraphMeta(DEFAULT_SEO_STATE.ogMeta);

    const titleMeta = document.head.querySelector('meta[property="og:title"]');
    expect(titleMeta).not.toBeNull();
    expect(titleMeta?.getAttribute('content')).toBe(DEFAULT_SEO_STATE.ogMeta.title);

    const descMeta = document.head.querySelector('meta[property="og:description"]');
    expect(descMeta?.getAttribute('content')).toBe(DEFAULT_SEO_STATE.ogMeta.description);

    const articleTags = document.head.querySelectorAll('meta[property="article:tag"]');
    expect(articleTags.length).toBe(DEFAULT_SEO_STATE.ogMeta.tags.length);
  });

  it('should inject JSON-LD schema into document head', () => {
    injectJsonLdSchema(DEFAULT_SEO_STATE.jsonLd);

    const script = document.head.querySelector('script[type="application/ld+json"]');
    expect(script).not.toBeNull();
    const content = JSON.parse(script?.textContent || '{}');
    expect(content['@type']).toBe('TechArticle');
    expect(content.headline).toBe(DEFAULT_SEO_STATE.jsonLd.headline);
  });

  it('should cleanly remove Open Graph and JSON-LD meta tags', () => {
    injectOpenGraphMeta(DEFAULT_SEO_STATE.ogMeta);
    injectJsonLdSchema(DEFAULT_SEO_STATE.jsonLd);

    expect(document.head.querySelectorAll('meta[property^="og:"]').length).toBeGreaterThan(0);
    expect(document.head.querySelector('script[type="application/ld+json"]')).not.toBeNull();

    removeOpenGraphMeta();
    removeJsonLdSchema();

    expect(document.head.querySelectorAll('meta[property^="og:"]').length).toBe(0);
    expect(document.head.querySelector('script[type="application/ld+json"]')).toBeNull();
  });

  it('should validate complete meta tags and return valid result', () => {
    injectOpenGraphMeta(DEFAULT_SEO_STATE.ogMeta);
    injectJsonLdSchema(DEFAULT_SEO_STATE.jsonLd);

    const validation = validateMetaTags();
    expect(validation.valid).toBe(true);
    expect(validation.errors.length).toBe(0);
  });

  it('should return errors for missing meta tags during validation', () => {
    const validation = validateMetaTags();
    expect(validation.valid).toBe(false);
    expect(validation.errors.length).toBeGreaterThan(0);
  });

  it('should produce deterministic bot crawl simulation with fixed seed', () => {
    const sim1 = simulateBotCrawl('GPTBot', 12345);
    const sim2 = simulateBotCrawl('GPTBot', 12345);

    expect(sim1.latencyMs).toBe(sim2.latencyMs);
    expect(sim1.httpStatus).toBe(200);
    expect(sim1.status).toBe('Verified 200 OK');
  });
});
