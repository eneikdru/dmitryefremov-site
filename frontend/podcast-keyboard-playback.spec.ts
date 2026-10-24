import { test, expect } from '@playwright/test';

test.describe('Podcast Media Hub Audio Playback Lifecycle & Keyboard Operability', () => {
  test.beforeEach(async ({ page }) => {
    // Inject HTMLMediaElement mock to allow synthetic audio playback in headless Chromium
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

    // Mock API route for seeded podcast episodes
    await page.route('**/api/v1/podcasts', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify([
          {
            id: 1,
            slug: 'ruth-barcan-marcus',
            philosopher: 'Рут Баркан Маркус',
            title: 'Рут Баркан Маркус — Формула Баркан и деонтическая модальная логика',
            thesis: 'Инвариантность квантификации в модальных контекстах.',
            description: 'Разбор модальной логики и формулы Баркан.',
            audioUrl: '/audio/ep1.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 1200,
            publishedAt: '2026-10-18',
            timecodes: [
              { id: 101, timeOffsetSeconds: 0, title: 'Введение в деонтическую логику' },
              { id: 102, timeOffsetSeconds: 340, title: 'Формула Баркан и квантификация' },
              { id: 103, timeOffsetSeconds: 920, title: 'Модальные инварианты' }
            ]
          },
          {
            id: 2,
            slug: 'paul-thagard',
            philosopher: 'Пол Таггард',
            title: 'Пол Таггард — Когнитивная когерентность',
            thesis: 'Вычислительная теория сознания и когерентность гипотез.',
            description: 'Разбор когнитивных архитектур.',
            audioUrl: '/audio/ep2.mp3',
            youtubeUrl: 'https://youtube.com/@dmitrii.efremov',
            telegramUrl: 'https://t.me/efremov_mvp',
            durationSeconds: 1500,
            publishedAt: '2026-10-19',
            timecodes: [
              { id: 201, timeOffsetSeconds: 0, title: 'Когерентность гипотез' }
            ]
          }
        ])
      });
    });
  });

  test('seeded podcast catalog is accessible and controls are fully keyboard operable', async ({ page }) => {
    // 1. Navigate to main page (Podcast Media Hub tab is active by default)
    await page.goto('/');

    if (await page.locator('#cmp-decline-btn').isVisible()) {
      await page.click('#cmp-decline-btn');
    }

    // Ensure Podcast player container is rendered
    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title').first()).toContainText('Рут Баркан Маркус');

    // 2. Test Play / Pause button via mouse click and keyboard Focus & Space
    const playBtn = page.locator('#play-pause-btn');
    await expect(playBtn).toBeVisible();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    // Click play button to start playback
    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    // Click play button again to pause playback
    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    // Focus play button and press Space to toggle
    await playBtn.focus();
    await page.keyboard.press('Space');
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    // Press Space again to Pause
    await page.keyboard.press('Space');
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    // 3. Test Waveform visualizer slider keyboard operability (ArrowRight / ArrowLeft / Space)
    const waveform = page.locator('#waveform-container');
    await expect(waveform).toBeVisible();

    await waveform.focus();
    // Press ArrowRight to seek forward (+5 seconds)
    await page.keyboard.press('ArrowRight');
    await expect(page.locator('#current-time-val')).toHaveText('00:05');

    // Press ArrowRight again (+5 seconds -> 10s)
    await page.keyboard.press('ArrowRight');
    await expect(page.locator('#current-time-val')).toHaveText('00:10');

    // Press ArrowLeft to seek backward (-5 seconds -> 5s)
    await page.keyboard.press('ArrowLeft');
    await expect(page.locator('#current-time-val')).toHaveText('00:05');

    // 4. Test Timecode / topic navigation selection via mouse click and keyboard
    const secondTimecode = page.locator('#timecode-btn-102');
    await expect(secondTimecode).toBeVisible();

    // Click topic timecode button directly
    await secondTimecode.click();

    // Timecode 102 offset is 340 seconds = 05:40
    await expect(page.locator('#current-time-val')).toHaveText('05:40');
    await expect(secondTimecode).toContainText('СЕЙЧАС ИГРАЕТ');

    // Click third topic timecode button
    const thirdTimecode = page.locator('#timecode-btn-103');
    await expect(thirdTimecode).toBeVisible();

    await thirdTimecode.focus();
    await page.keyboard.press('Enter');

    // Timecode 103 offset is 920 seconds = 15:20
    await expect(page.locator('#current-time-val')).toHaveText('15:20');
    await expect(thirdTimecode).toContainText('СЕЙЧАС ИГРАЕТ');

    // 5. Test switching episodes in catalog via keyboard focus & Space
    const ep2Card = page.locator('#episode-card-2');
    await expect(ep2Card).toBeVisible();

    await ep2Card.focus();
    await page.keyboard.press('Enter');

    // Verify active episode switched to Episode 2 (Пол Таггард)
    await expect(page.locator('#podcast-title')).toContainText('Пол Таггард');
    await expect(ep2Card).toContainText('АКТИВЕН');
  });
});
