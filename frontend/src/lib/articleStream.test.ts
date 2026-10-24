// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import { parseMarkdown } from './markdown';
import ArticleStreamView from '../components/ArticleStreamView.svelte';
import { telemetry } from './telemetry';
import { setConsentStatus, resetConsentStatus } from './consent';
import { mount, unmount } from 'svelte';

describe('Markdown & Formula Parser (parseMarkdown)', () => {
  it('converts block formulas ($$) to visually distinct formula blocks', () => {
    const raw = 'Header\n$$\n\\mathcal{O}(\\varphi) \\iff \\neg \\mathcal{P}(\\neg \\varphi)\n$$';
    const result = parseMarkdown(raw);

    expect(result.hasFormulas).toBe(true);
    expect(result.html).toContain('class="formula-block');
    expect(result.html).toContain('[FORMULA_MATH]');
    expect(result.html).toContain('\\mathcal{O}(\\varphi)');
  });

  it('converts inline formulas ($) to inline code elements', () => {
    const raw = 'Formula $x + y = z$ is inline.';
    const result = parseMarkdown(raw);

    expect(result.hasFormulas).toBe(true);
    expect(result.html).toContain('class="formula-inline');
    expect(result.html).toContain('x + y = z');
  });

  it('converts blockquotes (>) to styled blockquote HTML elements', () => {
    const raw = '> "Your code and your decisions are who you truly are."';
    const result = parseMarkdown(raw);

    expect(result.hasQuotes).toBe(true);
    expect(result.html).toContain('class="article-blockquote');
    expect(result.html).toContain('Your code and your decisions');
  });

  it('handles empty or plain text correctly', () => {
    expect(parseMarkdown('')).toEqual({ html: '', hasFormulas: false, hasQuotes: false });

    const plain = 'Simple paragraph text';
    const result = parseMarkdown(plain);
    expect(result.hasFormulas).toBe(false);
    expect(result.hasQuotes).toBe(false);
    expect(result.html).toContain('<p class="my-3 leading-relaxed text-[#cbd5e1] font-serif text-base">Simple paragraph text</p>');
  });

  it('renders complex Markdown containing headers, quotes, formulas, code blocks, and lists accurately', () => {
    const complexMarkdown = `# Main Title Header

## Section Subtitle

### Subsection Title

> "Your code and your decisions are who you truly are."

Inline math $E = mc^2$ and block math formula:
$$
\\mathcal{O}(\\varphi) \\iff \\neg \\mathcal{P}(\\neg \\varphi)
$$

\`\`\`typescript
const status = "VERIFIED";
\`\`\`

- First list item
- Second list item`;

    const result = parseMarkdown(complexMarkdown);

    expect(result.hasFormulas).toBe(true);
    expect(result.hasQuotes).toBe(true);
    expect(result.html).toContain('<h1 class="text-2xl font-extrabold text-white mt-10 mb-4 tracking-tight">Main Title Header</h1>');
    expect(result.html).toContain('<h2 class="text-xl font-bold text-white mt-8 mb-3 tracking-tight border-b border-[#1f2433] pb-1">Section Subtitle</h2>');
    expect(result.html).toContain('<h3 class="text-lg font-bold text-white mt-6 mb-2 tracking-tight">Subsection Title</h3>');
    expect(result.html).toContain('class="article-blockquote');
    expect(result.html).toContain('Your code and your decisions are who you truly are.');
    expect(result.html).toContain('class="formula-inline');
    expect(result.html).toContain('E = mc^2');
    expect(result.html).toContain('class="formula-block');
    expect(result.html).toContain('\\mathcal{O}(\\varphi)');
    expect(result.html).toContain('class="code-block');
    expect(result.html).toContain('const status = &quot;VERIFIED&quot;;');
    expect(result.html).toContain('<ul class="list-disc list-inside my-2 space-y-1 text-[#cbd5e1]"><li class="ml-2">First list item</li></ul>');
  });
});

const sampleArticles = [
  {
    id: 'high-end-editorial-minimalism',
    title: 'Эстетика инженерного молчания и редакционный минимализм',
    topic: 'systems' as const,
    readTime: '7 min read',
    publishedAt: '2026-10-10',
    summary: 'Отказ от визуального шума и отладочных элементов.',
    contentMarkdown: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"'
  },
  {
    id: 'deontic-operators-in-distributed-systems',
    title: 'Деонтическая логика и нормативные инварианты в распределенных сервисах',
    topic: 'deontic' as const,
    readTime: '9 min read',
    publishedAt: '2026-10-11',
    summary: 'Формализация нормативных состояний системы.',
    contentMarkdown: '# Деонтическая логика и нормативные инварианты\n\n$$\n\\mathcal{O}(A) \\iff \\neg \\mathcal{P}(\\neg A)\n$$'
  },
  {
    id: 'art-02-theory-of-constraints',
    title: 'Theory of Constraints in Distributed Pipeline Throughput',
    topic: 'toc' as const,
    readTime: '12 min read',
    publishedAt: '2026-02-28',
    summary: 'Applying Goldratt Theory of Constraints.',
    contentMarkdown: '# Theory of Constraints\n\nSystem capacity is determined by the bottleneck.'
  }
];

const sampleSparks = [
  {
    id: 'spark-4096',
    telegramMessageId: 4096,
    content: 'Exploring agentic economy and A2A protocols for autonomous agents.',
    domain: 'Agentic Economy (A2A)',
    tags: 'a2a,strategy',
    author: 'Dmitry Efremov',
    publishedAt: '2026-10-12 18:00'
  }
];

describe('ArticleStreamView Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    resetConsentStatus();
    setConsentStatus('accepted');
    telemetry.clearEvents();
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('renders localized Russian empty states when articles and sparks lists are empty', () => {
    component = mount(ArticleStreamView, { target, props: { articles: [], sparks: [] } });

    expect(target.textContent).toContain('Эссе и публикации отсутствуют');
    expect(target.textContent).toContain('Записи Telegram отсутствуют');
  });

  it('dispatches essay_view telemetry event on load and on article selection', async () => {
    component = mount(ArticleStreamView, { target, props: { articles: sampleArticles, sparks: sampleSparks, selectedArticleId: sampleArticles[0].id } });
    await new Promise(r => setTimeout(r, 0));

    const initialEvents = telemetry.getEvents().filter(e => e.type === 'essay_view');
    expect(initialEvents).toHaveLength(1);
    expect(initialEvents[0].payload.articleId).toBe('high-end-editorial-minimalism');

    const buttons = target.querySelectorAll('button.article-card');
    const secondCard = Array.from(buttons).find(b =>
      b.textContent?.includes('Деонтическая логика и нормативные инварианты')
    );
    secondCard?.click();
    await new Promise(r => setTimeout(r, 0));

    const updatedEvents = telemetry.getEvents().filter(e => e.type === 'essay_view');
    expect(updatedEvents).toHaveLength(2);
    expect(updatedEvents[1].payload.articleId).toBe('deontic-operators-in-distributed-systems');
  });

  it('renders list of available longreads and active article reader with initial realistic Russian essay', () => {
    component = mount(ArticleStreamView, { target, props: { articles: sampleArticles, sparks: sampleSparks, selectedArticleId: sampleArticles[0].id } });

    expect(target.textContent).toContain('Articles & Thought Stream');
    expect(target.textContent).toContain('AVAILABLE_LONGREADS');
    expect(target.textContent).toContain('Эстетика инженерного молчания и редакционный минимализм');
    expect(target.textContent).toContain('Деонтическая логика и нормативные инварианты в распределенных сервисах');
  });

  it('renders realistic Russian essay content, headings, blockquotes, and lists successfully when navigated to', async () => {
    component = mount(ArticleStreamView, { target, props: { articles: sampleArticles, sparks: sampleSparks, selectedArticleId: sampleArticles[0].id } });

    const h1 = target.querySelector('#article-header h1');
    expect(h1).not.toBeNull();
    expect(h1?.textContent).toBe('Эстетика инженерного молчания и редакционный минимализм');

    const articleContainer = target.querySelector('#article-scroll-container');
    expect(articleContainer).not.toBeNull();

    const h2 = articleContainer?.querySelector('h2');
    expect(h2).not.toBeNull();
    expect(h2?.textContent).toBe('Введение');

    const quoteBlock = articleContainer?.querySelector('.article-blockquote');
    expect(quoteBlock).not.toBeNull();
    expect(quoteBlock?.textContent).toContain('Настоящая архитектура не заявляет о себе громкими терминальными логами');

    const buttons = target.querySelectorAll('button.article-card');
    const secondArticleCard = Array.from(buttons).find(b =>
      b.textContent?.includes('Деонтическая логика и нормативные инварианты')
    );
    expect(secondArticleCard).toBeDefined();

    secondArticleCard?.click();
    await new Promise(r => setTimeout(r, 0));

    const updatedH1 = target.querySelector('#article-header h1');
    expect(updatedH1?.textContent).toBe('Деонтическая логика и нормативные инварианты в распределенных сервисах');

    const formulaBlock = target.querySelector('.formula-block');
    expect(formulaBlock).not.toBeNull();
  });

  it('filters articles when topic filter button is clicked', async () => {
    component = mount(ArticleStreamView, { target, props: { articles: sampleArticles, sparks: sampleSparks, selectedArticleId: sampleArticles[0].id } });

    const buttons = target.querySelectorAll('button');
    const tocBtn = Array.from(buttons).find(b => b.textContent?.includes('Theory of Constraints'));
    expect(tocBtn).toBeDefined();

    tocBtn?.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('AVAILABLE_LONGREADS (1)');
    expect(target.textContent).toContain('Theory of Constraints in Distributed Pipeline Throughput');
  });

  it('renders Telegram sparks stream in the UI stream', () => {
    component = mount(ArticleStreamView, { target, props: { articles: sampleArticles, sparks: sampleSparks } });

    const sparksSection = target.querySelector('#sparks-stream-section');
    expect(sparksSection).not.toBeNull();
    expect(target.textContent).toContain('[TELEGRAM_SPARKS_STREAM]');
    expect(target.textContent).toContain('Exploring agentic economy');
    expect(target.textContent).toContain('TG_MSG_#4096');
  });

  it('surfaces custom simulated Telegram spark payload in the UI stream', () => {
    const simulatedSpark = {
      id: 'spark-9999',
      telegramMessageId: 9999,
      content: 'Simulated real-time insight from Telegram webhook stream testing',
      domain: 'Pragmatic Business Strategy',
      tags: 'business,strategy',
      author: '@efremov_mvp',
      publishedAt: '2026-10-12 21:30'
    };

    component = mount(ArticleStreamView, {
      target,
      props: { articles: sampleArticles, sparks: [simulatedSpark] }
    });

    expect(target.textContent).toContain('Simulated real-time insight from Telegram webhook stream testing');
    expect(target.textContent).toContain('TG_MSG_#9999');
    expect(target.textContent).toContain('Pragmatic Business Strategy');
  });
});
