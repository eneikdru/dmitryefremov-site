// @vitest-environment jsdom
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { mount, unmount } from 'svelte';
import { PODCAST_EPISODES } from './podcast';
import ArticleStreamView from '../components/ArticleStreamView.svelte';

describe('Acceptance Flow QA Verification - Unit & Integration Guards', () => {
  describe('Podcast Canonical Catalog Verification', () => {
    it('contains all canonical released episodes (Ruth Barcan Marcus, Paul Thagard, Susan Hurley)', () => {
      const episodePhilosophers = PODCAST_EPISODES.map(ep => ep.philosopher);
      expect(episodePhilosophers).toContain('Рут Баркан Маркус');
      expect(episodePhilosophers).toContain('Пол Таггард');
      expect(episodePhilosophers).toContain('Сюзан Хёрли');
    });

    it('ensures each episode has valid audio stream URL and duration', () => {
      PODCAST_EPISODES.forEach(ep => {
        expect(ep.audioUrl).toMatch(/^\/audio\/0[1-9]-[\w-]+\.m4a$/);
        expect(ep.durationSeconds).toBeGreaterThan(0);
        expect(ep.timecodes.length).toBeGreaterThan(0);
        ep.timecodes.forEach(tc => {
          expect(tc.timeOffsetSeconds).toBeGreaterThanOrEqual(0);
          expect(tc.title).toBeTruthy();
        });
      });
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
      component = mount(ArticleStreamView, { target });

      expect(target.textContent).toContain('Articles & Thought Stream');
      expect(target.textContent).toContain('Эстетика инженерного молчания и редакционный минимализм');
      expect(target.textContent).toContain('Деонтическая логика и нормативные инварианты в распределенных сервисах');
      expect(target.textContent).toContain('Deontic Logic in Autonomous Process Architecture');

      const articleCards = target.querySelectorAll('button.article-card');
      expect(articleCards.length).toBeGreaterThanOrEqual(3);
    });

    it('renders Telegram sparks stream with valid metadata', () => {
      component = mount(ArticleStreamView, { target });

      const sparksSection = target.querySelector('#sparks-stream-section');
      expect(sparksSection).not.toBeNull();
      expect(target.textContent).toContain('[TELEGRAM_SPARKS_STREAM]');
      expect(target.textContent).toContain('Exploring agentic economy');
      expect(target.textContent).toContain('TG_MSG_#4096');
    });

    it('guarantees zero exposure of raw teleprompter script file paths in live articles and podcasts', () => {
      component = mount(ArticleStreamView, { target });

      const fullPageText = target.textContent || '';
      const teleprompterRegex = /\/mnt\/HC_Volume_106932004\/podcast\/episodes/i;

      expect(fullPageText).not.toMatch(teleprompterRegex);

      PODCAST_EPISODES.forEach(ep => {
        expect(ep.description).not.toMatch(teleprompterRegex);
        expect(ep.thesis).not.toMatch(teleprompterRegex);
        expect(ep.title).not.toMatch(teleprompterRegex);
      });
    });

    it('verifies absence of placeholder lorem ipsum text across rendered content components', () => {
      component = mount(ArticleStreamView, { target });

      const contentText = (target.textContent || '').toLowerCase();
      expect(contentText).not.toContain('lorem');
      expect(contentText).not.toContain('ipsum');

      PODCAST_EPISODES.forEach(ep => {
        const epText = `${ep.title} ${ep.description} ${ep.thesis}`.toLowerCase();
        expect(epText).not.toContain('lorem');
        expect(epText).not.toContain('ipsum');
      });
    });
  });
});
