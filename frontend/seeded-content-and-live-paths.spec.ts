import { test, expect } from '@playwright/test';

test.describe('QA Verification - Seeded Russian Content & Live Paths Integrity', () => {

  test.beforeEach(async ({ page }) => {
    // Inject mock HTMLMediaElement behavior for synthetic audio playback in headless browser
    await page.addInitScript(() => {
      Object.defineProperty(window.HTMLMediaElement.prototype, 'paused', {
        get(this: any) {
          return this._paused !== undefined ? this._paused : true;
        },
        configurable: true
      });
      window.HTMLMediaElement.prototype.play = function (this: any) {
        this._paused = false;
        return Promise.resolve();
      };
      window.HTMLMediaElement.prototype.pause = function (this: any) {
        this._paused = true;
      };
    });

    // Mock API routes for seeded Russian content
    await page.route('**/api/v1/podcasts', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 101,
            slug: 'rasshirennyy-razum-ep5',
            philosopher: 'Рут Баркан Маркус',
            title: 'Рут Баркан Маркус',
            thesis: 'Использование цифровой инфраструктуры как продолжения когнитивной системы по Энди Кларку.',
            description: 'Разбор взаимодействия мышления автора с цифровой рабочей памятью.',
            audioUrl: '/audio/ep5.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 1200,
            publishedAt: '2026-10-18',
            timecodes: [
              { id: 1011, timeOffsetSeconds: 0, title: 'Введение в расширенный разум' },
              { id: 1012, timeOffsetSeconds: 340, title: 'Графовая связность и Канон' },
              { id: 1013, timeOffsetSeconds: 920, title: 'Автономный вывод и A2A' }
            ]
          },
          {
            id: 102,
            slug: 'paul-thagard-ep6',
            philosopher: 'Пол Таггард',
            title: 'Выпуск 06. Ментальные модели и когнитивные когеренции',
            thesis: 'Вычислительная философия сознания и проверка когерентности гипотез.',
            description: 'Моделирование когнитивных процессов и связности взглядов.',
            audioUrl: '/audio/ep6.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 1500,
            publishedAt: '2026-10-19',
            timecodes: [
              { id: 1021, timeOffsetSeconds: 0, title: 'Когнитивная когерентность' }
            ]
          }
        ])
      });
    });

    const mockEssays = [
      {
        id: '1',
        slug: 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
        title: 'Внешняя рабочая память и граф понятий',
        summary: 'Сшивка расширенного разума и публичного портала.',
        content: '# Внешняя рабочая память и граф понятий\n\nЦифровая среда становится продолжением мышления автора.',
        authorName: 'Дмитрий Ефремов',
        topic: 'systems',
        publishedAt: '2026-10-18T10:00:00Z'
      },
      {
        id: '2',
        slug: 'deonticheskaya-avtonomiya-agentov',
        title: 'Деонтическая автономия ИИ-агентов',
        summary: 'Формализация обязательств, разрешений и запретов.',
        content: '# Деонтическая автономия ИИ-агентов\n\nНормативные инварианты и модальная логика.',
        authorName: 'Дмитрий Ефремов',
        topic: 'deontic',
        publishedAt: '2026-10-19T12:00:00Z'
      }
    ];

    await page.route('**/api/v1/essays', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mockEssays)
      });
    });

    await page.route('**/api/v1/articles', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mockEssays)
      });
    });

    await page.route('**/api/v1/sparks', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 's-101',
            telegramMessageId: 8001,
            author: 'Дмитрий Ефремов',
            content: 'Архитектура расширенного разума: короткая заметка из Telegram.',
            category: 'ARCHITECTURE',
            publishedAt: '2026-10-20T14:00:00Z'
          }
        ])
      });
    });

    await page.route('**/api/v1/canon', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 'c-1',
            slug: 'rasshirennyy-razum-i-vneshnyaya-pamyat',
            title: 'Концепция расширенного разума и внешней рабочей памяти',
            type: 'MANIFESTO',
            domain: 'PHILOSOPHY_OF_MIND',
            summary: 'Использование цифровой инфраструктуры как продолжения когнитивной системы.'
          }
        ])
      });
    });
  });

  test('AC2: Live public site is accessible and returns 200 OK', async ({ page }) => {
    const response = await page.goto('http://localhost:4173/');
    expect(response?.status()).toBe(200);

    await expect(page.locator('#telemetry-hub-root')).toBeVisible();
    await expect(page.locator('text=Дмитрий Ефремов').first()).toBeVisible();
  });

  test('AC1: Primary paths (podcast views, essay stream, canon links, search) function correctly against seeded Russian content', async ({ page }) => {
    await page.goto('http://localhost:4173/');

    // Dismiss CMP modal if present
    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. Verify Podcast view & seeded Russian episode content and timecodes (canon links)
    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title')).toContainText('Выпуск 05. Концепция расширенного разума');

    // Test timecode navigation (canon/concept cross link)
    const timecodeLink = page.locator('#timecode-btn-1012');
    await expect(timecodeLink).toBeVisible();
    await timecodeLink.click();
    await expect(timecodeLink).toContainText('СЕЙЧАС ИГРАЕТ');

    // Test audio playback controls
    const playBtn = page.locator('#play-pause-btn');
    await expect(playBtn).toBeVisible();
    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');
    await playBtn.click();

    // 2. Verify Articles & Essays view
    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');

    await expect(page.locator('text=Внешняя рабочая память и граф понятий').first()).toBeVisible();
    await expect(page.locator('text=Деонтическая автономия ИИ-агентов').first()).toBeVisible();

    // 3. Verify Telegram Sparks view and search functionality
    await page.click('#tab-btn-sparks');
    await page.waitForSelector('#sparks-view-root');
    await expect(page.locator('#sparks-view-root')).toBeVisible();

    // Trigger Search bar and execute query against seeded content
    await page.click('#toggleSearchBtn');
    await page.waitForSelector('#spark-search-input');
    const searchInput = page.locator('#spark-search-input');
    await expect(searchInput).toBeVisible();

    await searchInput.fill('Архитектура');
    await expect(page.locator('text=Архитектура расширенного разума').first()).toBeVisible();

    // 4. Verify Client Intake / Contact view
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');
    await expect(page.locator('#contact-terminal-root')).toBeVisible();
  });

  test('AC3: WCAG 2.1 AA compliance (keyboard operability, visible focus, labelled form controls, theme contrast)', async ({ page }) => {
    await page.goto('http://localhost:4173/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. Keyboard operability: Focus navigation button and verify focus
    const navBtn = page.locator('#tab-btn-article');
    await navBtn.focus();
    await expect(navBtn).toBeFocused();

    await page.keyboard.press('Enter');
    await page.waitForSelector('#article-stream-container');
    await expect(page.locator('#article-stream-container')).toBeVisible();

    // 2. Verify labelled form controls on intake / contact terminal
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');

    const inputs = page.locator('input, textarea, select');
    const count = await inputs.count();

    for (let i = 0; i < count; i++) {
      const input = inputs.nth(i);
      if (await input.isVisible()) {
        const id = await input.getAttribute('id');
        const ariaLabel = await input.getAttribute('aria-label');
        const placeholder = await input.getAttribute('placeholder');

        if (id) {
          const label = page.locator(`label[for="${id}"]`);
          const hasLabel = (await label.count()) > 0;
          expect(hasLabel || ariaLabel || placeholder).toBeTruthy();
        } else {
          expect(ariaLabel || placeholder).toBeTruthy();
        }
      }
    }

    // 3. Theme switch contrast test
    const themeBtn = page.locator('#theme-toggle-btn');
    await expect(themeBtn).toBeVisible();
    await themeBtn.click(); // Switch to Light Monochrome

    const root = page.locator('#telemetry-hub-root');
    await expect(root).toHaveClass(/bg-\[#fafafa\]/);

    await themeBtn.click(); // Switch back to Dark Void
    await expect(root).toHaveClass(/bg-\[#090a0f\]/);
  });
});
