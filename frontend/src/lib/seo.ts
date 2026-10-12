export interface SeoMetaProps {
  title: string;
  description: string;
  url?: string;
  type?: 'article' | 'website';
  imageUrl?: string;
  imageAlt?: string;
  publishedAt?: string;
  authorName?: string;
  keywords?: string[];
  locale?: string;
  siteName?: string;
  robotsDirective?: string;
  aiCrawlerPolicy?: {
    gptBotAllowed?: boolean;
    perplexityBotAllowed?: boolean;
    claudeBotAllowed?: boolean;
  };
}

export function generateJsonLd(config: SeoMetaProps): Record<string, unknown> {
  const siteName = config.siteName || 'Dmitry Efremov';
  const authorName = config.authorName || 'Dmitry Efremov';
  const url = config.url || 'https://dmitryefremov.com';

  if (config.type === 'article') {
    return {
      '@context': 'https://schema.org',
      '@type': 'TechArticle',
      headline: config.title,
      description: config.description,
      mainEntityOfPage: {
        '@type': 'WebPage',
        '@id': url
      },
      url: url,
      image: config.imageUrl || 'https://dmitryefremov.com/assets/og-cover.png',
      datePublished: config.publishedAt || new Date().toISOString().split('T')[0],
      author: {
        '@type': 'Person',
        name: authorName,
        url: 'https://dmitryefremov.com'
      },
      publisher: {
        '@type': 'Organization',
        name: siteName,
        logo: {
          '@type': 'ImageObject',
          url: 'https://dmitryefremov.com/assets/logo.png'
        }
      },
      keywords: config.keywords && config.keywords.length > 0 ? config.keywords : ['Deontic Logic', 'Autonomous Systems', 'AI Crawlers', 'GPTBot', 'PerplexityBot']
    };
  }

  return {
    '@context': 'https://schema.org',
    '@type': 'WebSite',
    name: siteName,
    url: url,
    description: config.description,
    author: {
      '@type': 'Person',
      name: authorName
    }
  };
}

export function buildMetaTags(config: SeoMetaProps) {
  const title = config.title;
  const description = config.description;
  const url = config.url || 'https://dmitryefremov.com';
  const type = config.type || 'website';
  const imageUrl = config.imageUrl || 'https://dmitryefremov.com/assets/og-cover.png';
  const locale = config.locale || 'en_US';
  const siteName = config.siteName || 'Dmitry Efremov';
  const robots = config.robotsDirective || 'index, follow, max-snippet:-1, max-image-preview:large';

  const tags: Array<{ name?: string; property?: string; content: string }> = [
    { name: 'description', content: description },
    { name: 'robots', content: robots },
    { name: 'googlebot', content: 'index, follow, max-snippet:-1, max-image-preview:large' },
    { name: 'GPTBot', content: config.aiCrawlerPolicy?.gptBotAllowed === false ? 'noindex' : 'index, follow, max-snippet:-1' },
    { name: 'PerplexityBot', content: config.aiCrawlerPolicy?.perplexityBotAllowed === false ? 'noindex' : 'index, follow, max-snippet:-1' },
    { name: 'ClaudeBot', content: config.aiCrawlerPolicy?.claudeBotAllowed === false ? 'noindex' : 'index, follow, max-snippet:-1' },

    // Open Graph Tags
    { property: 'og:title', content: title },
    { property: 'og:description', content: description },
    { property: 'og:type', content: type },
    { property: 'og:url', content: url },
    { property: 'og:image', content: imageUrl },
    { property: 'og:locale', content: locale },
    { property: 'og:site_name', content: siteName },

    // Twitter Tags
    { name: 'twitter:card', content: 'summary_large_image' },
    { name: 'twitter:title', content: title },
    { name: 'twitter:description', content: description },
    { name: 'twitter:image', content: imageUrl }
  ];

  if (config.imageAlt) {
    tags.push({ property: 'og:image:alt', content: config.imageAlt });
    tags.push({ name: 'twitter:image:alt', content: config.imageAlt });
  }

  return tags;
}

export function updateHeadMeta(config: SeoMetaProps): void {
  if (typeof document === 'undefined') return;

  // Title
  document.title = config.title;

  const tags = buildMetaTags(config);

  tags.forEach(tag => {
    const selector = tag.name ? `meta[name="${tag.name}"]` : `meta[property="${tag.property}"]`;
    let el = document.querySelector(selector) as HTMLMetaElement | null;
    if (!el) {
      el = document.createElement('meta');
      if (tag.name) el.setAttribute('name', tag.name);
      if (tag.property) el.setAttribute('property', tag.property);
      document.head.appendChild(el);
    }
    el.setAttribute('content', tag.content);
  });

  // JSON-LD Script
  let scriptEl = document.querySelector('script[type="application/ld+json"]') as HTMLScriptElement | null;
  if (!scriptEl) {
    scriptEl = document.createElement('script');
    scriptEl.setAttribute('type', 'application/ld+json');
    document.head.appendChild(scriptEl);
  }
  scriptEl.textContent = JSON.stringify(generateJsonLd(config), null, 2);
}
