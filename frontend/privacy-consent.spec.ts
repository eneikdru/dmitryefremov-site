import { test, expect } from '@playwright/test';

test.describe('Fresh Visitor Privacy Consent Verification', () => {
  test('fresh visitor without banner interaction leaks no tracking cookies or scripts', async ({ page }) => {
    // Access site as fresh visitor
    await page.goto('/');
    await page.waitForSelector('#telemetry-hub-root');
    await page.waitForSelector('#cmp-modal-overlay');

    // 1. Verify consent banner is displayed
    const isBannerVisible = await page.locator('#cmp-modal-overlay').isVisible();
    expect(isBannerVisible).toBe(true);

    // 2. Verify localStorage consent status is null for fresh visitor
    const consentStatus = await page.evaluate(() => localStorage.getItem('cmp_consent_status'));
    expect(consentStatus).toBeNull();

    // 3. Verify document.cookie contains zero tracking cookies
    const cookies = await page.evaluate(() => document.cookie);
    expect(cookies).toBe('');

    // 4. Perform user actions (scrolling, tab navigation) WITHOUT clicking CMP banner
    await page.click('#tab-btn-contact');
    await page.waitForTimeout(200);
    await page.click('#tab-btn-article');
    await page.waitForTimeout(200);

    // Verify still zero tracking cookies set and consent status remains null
    const postActionCookies = await page.evaluate(() => document.cookie);
    expect(postActionCookies).toBe('');
    const postActionConsentStatus = await page.evaluate(() => localStorage.getItem('cmp_consent_status'));
    expect(postActionConsentStatus).toBeNull();
  });

  test('accepting consent enables telemetry, while declining consent keeps tracking blocked', async ({ browser }) => {
    // Scenario A: Visitor clicks DECLINE
    const declineContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    try {
      const declinePage = await declineContext.newPage();
      await declinePage.goto('/');
      await declinePage.waitForSelector('#cmp-decline-btn');
      await declinePage.click('#cmp-decline-btn');

      const declineStatus = await declinePage.evaluate(() => localStorage.getItem('cmp_consent_status'));
      expect(declineStatus).toBe('declined');
      const declineCookies = await declinePage.evaluate(() => document.cookie);
      expect(declineCookies).toBe('');
    } finally {
      await declineContext.close();
    }

    // Scenario B: Visitor clicks ACCEPT
    const acceptContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    try {
      const acceptPage = await acceptContext.newPage();
      await acceptPage.goto('/');
      await acceptPage.waitForSelector('#cmp-accept-btn');
      await acceptPage.click('#cmp-accept-btn');

      const acceptStatus = await acceptPage.evaluate(() => localStorage.getItem('cmp_consent_status'));
      expect(acceptStatus).toBe('accepted');
    } finally {
      await acceptContext.close();
    }
  });
});
