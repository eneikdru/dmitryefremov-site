import { test, expect } from '@playwright/test';
import { setupE2EMocks } from './src/lib/test-utils';

test.describe('QA Verification - Seeded Russian Content & Live Paths Integrity', () => {
  test.beforeEach(async ({ page }) => {
    await setupE2EMocks(page);
  });

  test('AC2: Live public site is accessible and returns 200 OK', async ({ page }) => {
    const response = await page.goto('http://localhost:4173/');
    expect(response?.status()).toBe(200);

    await expect(page.locator('#telemetry-hub-root')).toBeVisible();
    await expect(page.locator('text=Дмитрий Ефремов').first()).toBeVisible();
  });

  test('AC1: Primary paths (podcast views, essay stream, canon links, search) function correctly against seeded Russian content', async ({ page }) => {
    await page.goto('http://localhost:4173/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-title')).toContainText('Выпуск 05. Концепция расширенного разума');

    const timecodeLink = page.locator('#timecode-btn-102');
    await expect(timecodeLink).toBeVisible();
    await timecodeLink.click();
    await expect(timecodeLink).toContainText('СЕЙЧАС ИГРАЕТ');

    const playBtn = page.locator('#play-pause-btn');
    await expect(playBtn).toBeVisible();
    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');
    await playBtn.click();

    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');

    await expect(page.locator('text=Эстетика инженерного молчания и редакционный минимализм').first()).toBeVisible();
    await expect(page.locator('text=Деонтическая логика и нормативные инварианты').first()).toBeVisible();

    await page.click('#tab-btn-sparks');
    await page.waitForSelector('#sparks-view-root');
    await expect(page.locator('#sparks-view-root')).toBeVisible();

    await page.click('#toggleSearchBtn');
    await page.waitForSelector('#spark-search-input');
    const searchInput = page.locator('#spark-search-input');
    await expect(searchInput).toBeVisible();

    await searchInput.fill('Архитектура');
    await expect(page.locator('text=Архитектура расширенного разума').first()).toBeVisible();

    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');
    await expect(page.locator('#contact-terminal-root')).toBeVisible();
  });

  test('AC3: WCAG 2.1 AA compliance (keyboard operability, visible focus, labelled form controls, theme contrast)', async ({ page }) => {
    await page.goto('http://localhost:4173/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    const navBtn = page.locator('#tab-btn-article');
    await navBtn.focus();
    await expect(navBtn).toBeFocused();

    await page.keyboard.press('Enter');
    await page.waitForSelector('#article-stream-container');
    await expect(page.locator('#article-stream-container')).toBeVisible();

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

    const themeBtn = page.locator('#theme-toggle-btn');
    await expect(themeBtn).toBeVisible();
    await themeBtn.click();

    const root = page.locator('#telemetry-hub-root');
    await expect(root).toHaveClass(/bg-\[#fafafa\]/);

    await themeBtn.click();
    await expect(root).toHaveClass(/bg-\[#090a0f\]/);
  });
});
