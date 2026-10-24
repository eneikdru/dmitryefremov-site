import { test, expect } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

test.describe('End-to-End Verification of Live Data Integration (BARCAN-TAG-06)', () => {

  test('AC1: Live environment with empty database displays correct Russian empty states without placeholder content', async ({ page }) => {
    // Intercept backend API routes to return empty arrays (empty database)
    await page.route('**/api/v1/podcasts', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([])
      });
    });

    await page.route('**/api/v1/essays', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([])
      });
    });

    await page.route('**/api/v1/articles', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([])
      });
    });

    await page.route('**/api/v1/sparks', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([])
      });
    });

    await page.route('**/api/v1/canon', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([])
      });
    });

    await page.goto('/');

    // Dismiss CMP banner if present
    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. Podcast View Empty State
    await page.waitForSelector('#podcast-player-container');
    const podcastEmptyState = page.locator('#podcast-empty-state');
    await expect(podcastEmptyState).toBeVisible();
    await expect(podcastEmptyState).toContainText('Выпуски подкаста отсутствуют');
    await expect(podcastEmptyState).toContainText('На данный момент в системе нет опубликованных выпусков подкаста.');

    // Verify system badge / podcast count badge displays 0
    await expect(page.locator('#nav-tabs')).toContainText('0 выпусков');

    // 2. Essays & Articles View Empty State
    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');

    const articlesEmptyState = page.locator('#articles-empty-state');
    await expect(articlesEmptyState).toBeVisible();
    await expect(articlesEmptyState).toContainText('Эссе и публикации отсутствуют');

    const sparksStreamEmptyState = page.locator('#sparks-stream-empty-state');
    await expect(sparksStreamEmptyState).toBeVisible();
    await expect(sparksStreamEmptyState).toContainText('Записи Telegram отсутствуют');

    const articleReaderEmptyState = page.locator('#article-reader-empty');
    await expect(articleReaderEmptyState).toBeVisible();
    await expect(articleReaderEmptyState).toContainText('Выберите эссе из списка для чтения');

    // 3. Sparks View Empty State
    await page.click('#tab-btn-sparks');
    await page.waitForSelector('#sparks-view-root');

    const sparksFeedEmptyState = page.locator('#sparks-feed-empty-state');
    await expect(sparksFeedEmptyState).toBeVisible();
    await expect(sparksFeedEmptyState).toContainText('Искры и записи отсутствуют');

    // 4. Assert no mock data leaks / foreign mock constants
    const bodyText = await page.locator('body').innerText();
    expect(bodyText).not.toContain('@alexp');
    expect(bodyText).not.toContain('Deontic Logic in Autonomous Process Architecture');
  });

  test('AC2: Live environment with published and draft content isolates drafts and displays only published items', async ({ page }) => {
    // Intercept backend API routes to return mix of published items and draft/raw items
    await page.route('**/api/v1/podcasts', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 201,
            slug: 'published-episode-ep1',
            philosopher: 'Дмитрий Ефремов',
            title: 'Опубликованный выпуск 01: Архитектура систем',
            thesis: 'Изучение системных патернов и автономии.',
            description: 'Полное описание опубликованного выпуска.',
            audioUrl: '/audio/ep1.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 900,
            publishedAt: '2026-10-20',
            timecodes: []
          },
          {
            id: 202,
            slug: 'raw_teleprompter_draft_ep2',
            philosopher: 'Черновик',
            title: 'Сырой суфлёр подкаста teleprompter_raw',
            thesis: 'Тестовый сырой материал.',
            description: 'Сырой суфлёр не должен попадать на сайт.',
            audioUrl: '/audio/raw2.mp3',
            durationSeconds: 300,
            publishedAt: '2026-10-21',
            timecodes: []
          }
        ])
      });
    });

    await page.route('**/api/v1/essays', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 'essay-pub-1',
            slug: 'vneshnyaya-rabochaya-pamyat',
            title: 'Отредактированное эссе: Внешняя рабочая память',
            summary: 'Публичная статья автора.',
            content: '# Внешняя рабочая память\n\nОтредактированный текст.',
            topic: 'systems',
            publishedAt: '2026-10-22T10:00:00Z'
          },
          {
            id: 'essay-raw-2',
            slug: 'teleprompter_draft_essay',
            title: 'Черновой суфлёр эссе teleprompter',
            summary: 'Сырой неотредактированный суфлёр.',
            content: '# Raw Teleprompter Content',
            topic: 'deontic',
            publishedAt: '2026-10-23T10:00:00Z'
          }
        ])
      });
    });

    await page.route('**/api/v1/sparks', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 'spk-pub-1',
            telegramMessageId: 9001,
            author: 'Дмитрий Ефремов',
            content: 'Опубликованная искра из Telegram-канала.',
            tags: 'heuristics',
            publishedAt: '2026-10-24T12:00:00Z'
          }
        ])
      });
    });

    await page.goto('/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. Verify Podcast view shows published episode and isolates raw teleprompter/draft
    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title')).toContainText('Опубликованный выпуск 01: Архитектура систем');
    await expect(page.locator('text=Сырой суфлёр подкаста teleprompter_raw')).toHaveCount(0);

    // Verify count badge shows 1 published episode
    await expect(page.locator('#nav-tabs')).toContainText('1 выпуск');

    // 2. Verify Essay view shows published essay and isolates teleprompter/draft
    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');

    await expect(page.locator('text=Отредактированное эссе: Внешняя рабочая память').first()).toBeVisible();
    await expect(page.locator('text=Черновой суфлёр эссе teleprompter')).toHaveCount(0);
  });

  test('AC3: Automated accessibility checks meeting WCAG 2.1 AA standards across all public views', async ({ page }) => {
    // Mock backend responses
    await page.route('**/api/v1/podcasts', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 1,
            slug: 'ep1',
            philosopher: 'Дмитрий Ефремов',
            title: 'Выпуск 01. Онтология автономии',
            thesis: 'Введение в архитектуру автономных систем.',
            description: 'Подробный разбор концепта.',
            audioUrl: '/audio/ep1.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 600,
            publishedAt: '2026-10-20',
            timecodes: []
          }
        ])
      });
    });

    await page.route('**/api/v1/essays', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 'essay-1',
            slug: 'ontology-essay',
            title: 'Онтология автономии ИИ-агентов',
            summary: 'Формализация обязательств.',
            content: '# Онтология автономии ИИ-агентов',
            topic: 'systems',
            publishedAt: '2026-10-21T10:00:00Z'
          }
        ])
      });
    });

    await page.route('**/api/v1/sparks', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 'spark-1',
            telegramMessageId: 101,
            author: 'Дмитрий Ефремов',
            content: 'Архитектурный инвариант.',
            publishedAt: '2026-10-22T12:00:00Z'
          }
        ])
      });
    });

    await page.goto('/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. Accessibility check on Podcast View
    await page.waitForSelector('#podcast-player-container');
    const podcastAxeResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();
    expect(podcastAxeResults.violations).toEqual([]);

    // 2. Accessibility check on Article Stream View
    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');
    const articleAxeResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();
    expect(articleAxeResults.violations).toEqual([]);

    // 3. Accessibility check on Sparks View
    await page.click('#tab-btn-sparks');
    await page.waitForSelector('#sparks-view-root');
    const sparksAxeResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();
    expect(sparksAxeResults.violations).toEqual([]);

    // 4. Accessibility check on Contact Terminal View
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');
    const contactAxeResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();
    expect(contactAxeResults.violations).toEqual([]);

    // 5. Accessibility check on Canon Gateway View
    await page.click('#tab-btn-canon');
    await page.waitForSelector('#canon-gateway-root');
    const canonAxeResults = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();
    expect(canonAxeResults.violations).toEqual([]);

    // 6. Keyboard Operability & Focus Navigation Test
    const podcastTabBtn = page.locator('#tab-btn-podcast');
    await podcastTabBtn.focus();
    await expect(podcastTabBtn).toBeFocused();

    await page.keyboard.press('Enter');
    await expect(page.locator('#podcast-player-container')).toBeVisible();
  });
});
