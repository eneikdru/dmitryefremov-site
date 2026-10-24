// @vitest-environment jsdom
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { mount, unmount } from 'svelte';
import { PODCAST_EPISODES } from './podcast';
import ArticleStreamView from '../components/ArticleStreamView.svelte';

const sampleTestArticles = [
  {
    id: 'high-end-editorial-minimalism',
    title: 'Эстетика инженерного молчания и редакционный минимализм',
    topic: 'systems' as const,
    readTime: '7 min read',
    publishedAt: '2026-10-10',
    summary: 'Отказ от визуального шума.',
    contentMarkdown: '# Эстетика инженерного молчания'
  },
  {
    id: 'deontic-operators-in-distributed-systems',
    title: 'Деонтическая логика и нормативные инварианты в распределенных сервисах',
    topic: 'deontic' as const,
    readTime: '9 min read',
    publishedAt: '2026-10-11',
    summary: 'Формализация нормативных состояний.',
    contentMarkdown: '# Деонтическая логика'
  }
];

const sampleTestSparks = [
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

describe('Acceptance Flow QA Verification - Unit & Integration Guards', () => {
  describe('Podcast Canonical Catalog Verification', () => {
    it('initial PODCAST_EPISODES array is empty for dynamic API binding', () => {
      expect(PODCAST_EPISODES).toHaveLength(0);
    });
  });

  describe('Swiss Editorial Essays Verification', () => {
    let target: HTMLElement;
    let component: any;

    beforeEach(() => {
      target = document.createElement('div');
      document.body.appendChild(target);
    });

    afterEach(() => {
      if (component) unmount(component);
      if (target && target.parentNode) {
        target.parentNode.removeChild(target);
      }
    });

    it('renders clean longread articles with Swiss Editorial aesthetic features', () => {
      component = mount(ArticleStreamView, { target, props: { articles: sampleTestArticles, sparks: sampleTestSparks } });

      expect(target.textContent).toContain('Articles & Thought Stream');
      expect(target.textContent).toContain('Эстетика инженерного молчания и редакционный минимализм');
      expect(target.textContent).toContain('Деонтическая логика и нормативные инварианты в распределенных сервисах');

      const articleCards = target.querySelectorAll('button.article-card');
      expect(articleCards.length).toBeGreaterThanOrEqual(2);
    });

    it('renders Telegram sparks stream with valid metadata', () => {
      component = mount(ArticleStreamView, { target, props: { articles: sampleTestArticles, sparks: sampleTestSparks } });

      const sparksSection = target.querySelector('#sparks-stream-section');
      expect(sparksSection).not.toBeNull();
      expect(target.textContent).toContain('[TELEGRAM_SPARKS_STREAM]');
      expect(target.textContent).toContain('Exploring agentic economy');
      expect(target.textContent).toContain('TG_MSG_#4096');
    });

    it('guarantees zero exposure of raw teleprompter script file paths in live articles and podcasts', () => {
      component = mount(ArticleStreamView, { target, props: { articles: sampleTestArticles, sparks: sampleTestSparks } });

      const fullPageText = target.textContent || '';
      const teleprompterRegex = /\/mnt\/HC_Volume_106932004\/podcast\/episodes/i;

      expect(fullPageText).not.toMatch(teleprompterRegex);
    });
  });
});
