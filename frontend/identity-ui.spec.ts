import { test, expect } from '@playwright/test';

test.describe('Core Identity and Essay Reader Verification', () => {
  test.beforeEach(async ({ page }) => {
    const mockEssays = [
      {
        id: '1',
        slug: 'deterministic-state-determinism',
        title: 'Deterministic State Determinism in Autonomous Loops',
        summary: 'Replayability condition in autonomous state loops.',
        contentMarkdown: '# Replayability Condition\n\nState determinism across transitions.',
        authorName: 'Дмитрий Ефремов',
        topic: 'deontic',
        readTime: '6 мин',
        publishedAt: '2026-10-18'
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
  });

  test('no debug logs on facade and essay reader renders correctly', async ({ page }) => {
    const logs: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'log' || msg.type() === 'debug') {
        logs.push(msg.text());
      }
    });

    await page.goto('/');

    if (await page.locator('#cmp-decline-btn').isVisible()) {
      await page.click('#cmp-decline-btn');
    }

    // Switch to Article Stream tab
    await page.click('#tab-btn-article');

    // Click on the article
    await page.locator('text=Deterministic State Determinism').first().click();

    // Verify Essay reader fetches and renders content
    await expect(page.locator('text=Deterministic State Determinism in Autonomous Loops').first()).toBeVisible();
    await expect(page.locator('text=Replayability Condition').first()).toBeVisible();

    // Verify Core Identity renders correctly (contact) by switching to contact tab
    await page.click('#tab-btn-contact');
    await expect(page.locator('text=Telegram Channel').first()).toBeVisible();

    // Check no debug logs
    expect(logs.length).toBe(0);
  });

  test('visual layout bounding boxes match specifications', async ({ browser }) => {
    const contextDesktop = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const pageDesktop = await contextDesktop.newPage();
    await pageDesktop.goto('/');
    await expect(pageDesktop.locator('text=Дмитрий Ефремов').first()).toBeVisible();
    await expect(pageDesktop.locator('#hero-avatar-img')).toBeVisible();

    const desktopAvatarBox = await pageDesktop.locator('#hero-avatar-img').boundingBox();
    expect(desktopAvatarBox).not.toBeNull();
    if (desktopAvatarBox) {
      expect(desktopAvatarBox.width).toBeGreaterThan(100);
      expect(desktopAvatarBox.height).toBeGreaterThan(100);
    }

    await contextDesktop.close();

    const contextMobile = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const pageMobile = await contextMobile.newPage();
    await pageMobile.goto('/');
    await expect(pageMobile.locator('text=Дмитрий Ефремов').first()).toBeVisible();
    await expect(pageMobile.locator('#hero-avatar-img')).toBeVisible();

    const mobileAvatarBox = await pageMobile.locator('#hero-avatar-img').boundingBox();
    expect(mobileAvatarBox).not.toBeNull();
    if (mobileAvatarBox) {
      expect(mobileAvatarBox.width).toBeGreaterThan(80);
      expect(mobileAvatarBox.height).toBeGreaterThan(80);
    }

    await contextMobile.close();
  });
});
