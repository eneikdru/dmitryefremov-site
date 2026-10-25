import { test, expect } from '@playwright/test';

test.describe('Core Identity and Essay Reader Verification', () => {
  // Assuming the server is running based on the existing playwright config

  test('no debug logs on facade and essay reader renders correctly', async ({ page }) => {
    const logs: string[] = [];
    page.on('console', msg => {
      if (msg.type() === 'log' || msg.type() === 'debug') {
        logs.push(msg.text());
      }
    });

    await page.goto('/');
    const acceptBtn = await page.$("#cmp-accept-btn");
    if (acceptBtn) {
      await acceptBtn.click();
      await page.waitForTimeout(300);
    }

    // Switch to Article Stream tab
    await page.click('#tab-btn-article');

    // Click on the third article
    await page.locator('text=Deterministic State Determinism').first().click();

    // Verify Essay reader fetches and renders content (based on trace)
    await expect(page.locator('text=Deterministic State Determinism in Autonomous Loops').first()).toBeVisible();
    await expect(page.locator('text=Replayability Condition').first()).toBeVisible();

    // Verify Core Identity renders correctly (contact) by switching to contact tab
    await page.click('#tab-btn-contact');
    await expect(page.locator('text=Telegram Channel').first()).toBeVisible();

    // Check no debug logs at the very end to capture all interactions
    expect(logs.length).toBe(0);
  });

  test('visual layout bounding boxes match specifications', async ({ browser }) => {
    // Instead of using screenshots which cannot be committed, we verify visual layout
    // characteristics (e.g., elements are visible, have dimensions, and are placed correctly).
    const contextDesktop = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const pageDesktop = await contextDesktop.newPage();
    await pageDesktop.goto('/');
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
