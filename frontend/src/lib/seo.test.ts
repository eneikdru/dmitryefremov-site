import { describe, it, expect, beforeEach } from 'vitest';
import { generateJsonLd, buildMetaTags, updateHeadMeta, type SeoMetaProps } from './seo';

describe('SEO & Open Graph Metadata Utility', () => {
  const sampleArticleConfig: SeoMetaProps = {
    title: 'Deontic Logic in Autonomous Process Architecture',
    description: 'Exploring normative modal logic as a foundational model for autonomous agent safety.',
    url: 'https://dmitryefremov.com/article/art-01-deontic-logic',
    type: 'article',
    imageUrl: 'https://dmitryefremov.com/assets/deontic-cover.png',
    imageAlt: 'Deontic Logic Cover Image',
    publishedAt: '2026-03-15',
    authorName: 'Dmitry Efremov',
    keywords: ['deontic', 'autonomous', 'logic'],
    aiCrawlerPolicy: {
      gptBotAllowed: true,
      perplexityBotAllowed: true,
      claudeBotAllowed: true
    }
  };

  beforeEach(() => {
    if (typeof document !== 'undefined') {
      document.head.innerHTML = '';
      document.title = '';
    }
  });

  it('generates valid JSON-LD structured data for TechArticle', () => {
    const jsonLd = generateJsonLd(sampleArticleConfig) as any;

    expect(jsonLd['@context']).toBe('https://schema.org');
    expect(jsonLd['@type']).toBe('TechArticle');
    expect(jsonLd.headline).toBe(sampleArticleConfig.title);
    expect(jsonLd.description).toBe(sampleArticleConfig.description);
    expect(jsonLd.author.name).toBe('Dmitry Efremov');
    expect(jsonLd.datePublished).toBe('2026-03-15');
    expect(jsonLd.keywords).toEqual(['deontic', 'autonomous', 'logic']);
  });

  it('generates valid JSON-LD structured data for WebSite', () => {
    const websiteConfig: SeoMetaProps = {
      title: 'Dmitry Efremov Terminal',
      description: 'Autonomous Systems & Telemetry',
      type: 'website'
    };

    const jsonLd = generateJsonLd(websiteConfig) as any;

    expect(jsonLd['@context']).toBe('https://schema.org');
    expect(jsonLd['@type']).toBe('WebSite');
    expect(jsonLd.name).toBe('Dmitry Efremov');
  });

  it('builds comprehensive meta tags including Open Graph, Twitter, and AI Crawler directives', () => {
    const tags = buildMetaTags(sampleArticleConfig);

    const getMeta = (key: string, propAttr: 'name' | 'property' = 'property') =>
      tags.find(t => t[propAttr] === key)?.content;

    expect(getMeta('og:title')).toBe('Deontic Logic in Autonomous Process Architecture');
    expect(getMeta('og:description')).toBe(sampleArticleConfig.description);
    expect(getMeta('og:type')).toBe('article');
    expect(getMeta('og:url')).toBe('https://dmitryefremov.com/article/art-01-deontic-logic');
    expect(getMeta('og:image')).toBe('https://dmitryefremov.com/assets/deontic-cover.png');
    expect(getMeta('og:image:alt')).toBe('Deontic Logic Cover Image');

    expect(getMeta('twitter:card', 'name')).toBe('summary_large_image');
    expect(getMeta('twitter:title', 'name')).toBe('Deontic Logic in Autonomous Process Architecture');

    expect(getMeta('GPTBot', 'name')).toBe('index, follow, max-snippet:-1');
    expect(getMeta('PerplexityBot', 'name')).toBe('index, follow, max-snippet:-1');
    expect(getMeta('ClaudeBot', 'name')).toBe('index, follow, max-snippet:-1');
  });

  it('updates document head correctly in DOM environment and verifies presence of all SEO tags', () => {
    updateHeadMeta(sampleArticleConfig);

    expect(document.title).toBe(sampleArticleConfig.title);

    // Meta Description & Robots
    expect(document.querySelector('meta[name="description"]')?.getAttribute('content')).toBe(sampleArticleConfig.description);
    expect(document.querySelector('meta[name="robots"]')?.getAttribute('content')).toBe('index, follow, max-snippet:-1, max-image-preview:large');
    expect(document.querySelector('meta[name="googlebot"]')?.getAttribute('content')).toBe('index, follow, max-snippet:-1, max-image-preview:large');

    // AI Crawlers Policy
    expect(document.querySelector('meta[name="GPTBot"]')?.getAttribute('content')).toBe('index, follow, max-snippet:-1');
    expect(document.querySelector('meta[name="PerplexityBot"]')?.getAttribute('content')).toBe('index, follow, max-snippet:-1');
    expect(document.querySelector('meta[name="ClaudeBot"]')?.getAttribute('content')).toBe('index, follow, max-snippet:-1');

    // Open Graph
    expect(document.querySelector('meta[property="og:title"]')?.getAttribute('content')).toBe(sampleArticleConfig.title);
    expect(document.querySelector('meta[property="og:description"]')?.getAttribute('content')).toBe(sampleArticleConfig.description);
    expect(document.querySelector('meta[property="og:type"]')?.getAttribute('content')).toBe('article');
    expect(document.querySelector('meta[property="og:url"]')?.getAttribute('content')).toBe(sampleArticleConfig.url);
    expect(document.querySelector('meta[property="og:image"]')?.getAttribute('content')).toBe(sampleArticleConfig.imageUrl);
    expect(document.querySelector('meta[property="og:image:alt"]')?.getAttribute('content')).toBe(sampleArticleConfig.imageAlt);
    expect(document.querySelector('meta[property="og:locale"]')?.getAttribute('content')).toBe('en_US');

    // Twitter
    expect(document.querySelector('meta[name="twitter:card"]')?.getAttribute('content')).toBe('summary_large_image');
    expect(document.querySelector('meta[name="twitter:title"]')?.getAttribute('content')).toBe(sampleArticleConfig.title);
    expect(document.querySelector('meta[name="twitter:description"]')?.getAttribute('content')).toBe(sampleArticleConfig.description);
    expect(document.querySelector('meta[name="twitter:image"]')?.getAttribute('content')).toBe(sampleArticleConfig.imageUrl);
    expect(document.querySelector('meta[name="twitter:image:alt"]')?.getAttribute('content')).toBe(sampleArticleConfig.imageAlt);

    // JSON-LD Script
    const jsonLdScript = document.querySelector('script[type="application/ld+json"]');
    expect(jsonLdScript).not.toBeNull();
    const parsed = JSON.parse(jsonLdScript?.textContent || '{}');
    expect(parsed['@type']).toBe('TechArticle');
    expect(parsed.headline).toBe(sampleArticleConfig.title);
  });

  it('handles restricted crawler policy directives in DOM head', () => {
    const restrictedConfig: SeoMetaProps = {
      ...sampleArticleConfig,
      aiCrawlerPolicy: {
        gptBotAllowed: false,
        perplexityBotAllowed: false,
        claudeBotAllowed: false
      }
    };

    updateHeadMeta(restrictedConfig);

    expect(document.querySelector('meta[name="GPTBot"]')?.getAttribute('content')).toBe('noindex');
    expect(document.querySelector('meta[name="PerplexityBot"]')?.getAttribute('content')).toBe('noindex');
    expect(document.querySelector('meta[name="ClaudeBot"]')?.getAttribute('content')).toBe('noindex');
  });

  it('updates existing DOM meta tags idempotently without duplicating tags', () => {
    updateHeadMeta(sampleArticleConfig);
    const initialMetaCount = document.head.querySelectorAll('meta').length;

    const updatedConfig: SeoMetaProps = {
      ...sampleArticleConfig,
      title: 'Updated Article Title'
    };

    updateHeadMeta(updatedConfig);
    const newMetaCount = document.head.querySelectorAll('meta').length;

    expect(newMetaCount).toBe(initialMetaCount);
    expect(document.title).toBe('Updated Article Title');
    expect(document.querySelector('meta[property="og:title"]')?.getAttribute('content')).toBe('Updated Article Title');
  });
});
