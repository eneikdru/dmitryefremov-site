import { test, expect } from '@playwright/test';

test.describe('QA Verification - Empty States', () => {
  test.beforeEach(async ({ page }) => {
    await page.addInitScript(() => {
      Object.defineProperty(window.HTMLMediaElement.prototype, 'paused', {
        get(this: any) { return this._paused !== undefined ? this._paused : true; },
        configurable: true
      });
      window.HTMLMediaElement.prototype.play = function (this: any) {
        this._paused = false; return Promise.resolve();
      };
      window.HTMLMediaElement.prototype.pause = function (this: any) {
        this._paused = true;
      };
    });

    await page.route('**/api/v1/podcasts', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: '[]' }); });
    await page.route('**/api/v1/essays', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: '[]' }); });
    await page.route('**/api/v1/articles', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: '[]' }); });
    await page.route('**/api/v1/sparks', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: '[]' }); });
    await page.route('**/api/v1/canon', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: '[]' }); });
  });

  test('AC1: Correct Russian empty states are displayed on empty database', async ({ page }) => {
    await page.goto('http://localhost:4173/');
    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    await expect(page.locator('#podcast-empty-state')).toBeVisible();
    await expect(page.locator('#podcast-empty-state')).toContainText('Выпуски подкаста отсутствуют');

    await page.click('#tab-btn-article');
    await expect(page.locator('#articles-empty-state')).toBeVisible();
    await expect(page.locator('#articles-empty-state')).toContainText('Эссе и публикации отсутствуют');

    await expect(page.locator('#sparks-stream-empty-state')).toBeVisible();
    await expect(page.locator('#sparks-stream-empty-state')).toContainText('Записи Telegram отсутствуют');

    await page.click('#tab-btn-sparks');
    await expect(page.locator('#sparks-feed-empty-state')).toBeVisible();
    await expect(page.locator('#sparks-feed-empty-state')).toContainText('Искры и записи отсутствуют');
  });
});
