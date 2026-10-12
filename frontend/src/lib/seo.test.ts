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

  it('updates document head correctly in DOM environment', () => {
    document.head.innerHTML = '';
    updateHeadMeta(sampleArticleConfig);

    expect(document.title).toBe(sampleArticleConfig.title);

    const ogTitle = document.querySelector('meta[property="og:title"]');
    expect(ogTitle?.getAttribute('content')).toBe(sampleArticleConfig.title);

    const gptBot = document.querySelector('meta[name="GPTBot"]');
    expect(gptBot?.getAttribute('content')).toBe('index, follow, max-snippet:-1');

    const jsonLdScript = document.querySelector('script[type="application/ld+json"]');
    expect(jsonLdScript).not.toBeNull();
    const parsed = JSON.parse(jsonLdScript?.textContent || '{}');
    expect(parsed['@type']).toBe('TechArticle');
  });
});
