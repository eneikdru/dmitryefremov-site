import { test, expect } from '@playwright/test';

const MOCK_ESSAYS = [
  {
    id: 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
    slug: 'vneshnyaya-rabochaya-pamyat-i-graf-ponyatiy',
    title: 'Сайт как внешняя рабочая память: от плоских заметок к графу понятий',
    topic: 'systems',
    readTime: '7 min read',
    publishedAt: '2026-10-22',
    summary: 'Как превратить персональный сайт в динамический граф знаний.',
    content: '# Сайт как внешняя рабочая память\n\nВ концепции расширенного разума сайт становится внешней рабочей памятью.'
  }
];

test.describe('Core Identity and Essay Reader Verification', () => {
  test.beforeEach(async ({ page }) => {
    await page.route('/api/v1/essays', route => route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(MOCK_ESSAYS) }));
  });

  test('no debug logs on facade and essay reader renders correctly', async ({ page }) => {
    const logs: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'log' || msg.type() === 'debug') {
        logs.push(msg.text());
      }
    });

    await page.goto('/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // Switch to Article Stream tab
    await page.click('#tab-btn-article');

    // Click on the first article
    await page.locator('.article-card').first().click();

    // Verify Essay reader fetches and renders content
    await expect(page.locator('#article-reader-section')).toBeVisible();

    // Verify Core Identity renders correctly (contact) by switching to contact tab
    await page.click('#tab-btn-contact');
    await expect(page.locator('text=Telegram Channel').first()).toBeVisible();

    // Check no debug logs at the very end to capture all interactions
    expect(logs.length).toBe(0);
  });

  test('visual layout bounding boxes match specifications', async ({ browser }) => {
    const contextDesktop = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const pageDesktop = await contextDesktop.newPage();
    await pageDesktop.goto('/');

    if (await pageDesktop.locator('#cmp-accept-btn').isVisible()) {
      await pageDesktop.click('#cmp-accept-btn');
    }

    await expect(pageDesktop.locator('text=Дмитрий Ефремов').first()).toBeVisible();
    await expect(pageDesktop.locator('#hero-avatar-img')).toBeVisible();

    const desktopAvatarBox = await pageDesktop.locator('#hero-avatar-img').boundingBox();
    expect(desktopAvatarBox).not.toBeNull();
    if(desktopAvatarBox) {
      expect(desktopAvatarBox.width).toBeGreaterThan(100);
      expect(desktopAvatarBox.height).toBeGreaterThan(100);
    }

    await contextDesktop.close();

    const contextMobile = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const pageMobile = await contextMobile.newPage();
    await pageMobile.goto('/');

    if (await pageMobile.locator('#cmp-accept-btn').isVisible()) {
      await pageMobile.click('#cmp-accept-btn');
    }

    await expect(pageMobile.locator('text=Дмитрий Ефремов').first()).toBeVisible();
    await expect(pageMobile.locator('#hero-avatar-img')).toBeVisible();

    const mobileAvatarBox = await pageMobile.locator('#hero-avatar-img').boundingBox();
    expect(mobileAvatarBox).not.toBeNull();
    if(mobileAvatarBox) {
      expect(mobileAvatarBox.width).toBeGreaterThan(80);
      expect(mobileAvatarBox.height).toBeGreaterThan(80);
    }

    await contextMobile.close();
  });
});
