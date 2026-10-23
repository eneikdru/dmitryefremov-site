import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts for Canon Reader UI', async ({ page }) => {
  const targetDir = path.resolve(process.cwd(), '../.eneik/records/design-check-f65a9258-f19e-480c-927f-7b6930217978');
  if (!fs.existsSync(targetDir)) {
    fs.mkdirSync(targetDir, { recursive: true });
  }

  // Set desktop viewport
  await page.setViewportSize({ width: 1440, height: 900 });
  await page.goto('/');

  // Pre-set consent in localStorage and reload
  await page.evaluate(() => {
    localStorage.setItem('cmp_consent_status', 'accepted');
  });
  await page.reload();

  // Switch to Canon tab
  const canonTab = page.locator('#tab-btn-canon');
  await expect(canonTab).toBeVisible();
  await canonTab.click();

  // Verify Canon reader content is displayed
  const canonContainer = page.locator('#canon-reader-root');
  await expect(canonContainer).toBeVisible();

  // Capture Desktop Screenshot (1440px)
  const desktopPath = path.join(targetDir, 'desktop-1440.png');
  await page.screenshot({ path: desktopPath, fullPage: true });

  // Get bounding box layout metrics for key elements
  const layoutCheck = await page.evaluate(() => {
    const ids = [
      'telemetry-hub-root',
      'tab-btn-canon',
      'canon-reader-root',
      'canon-article',
      'canon-domain-nav',
      'canon-epigraph',
      'canon-scholial-card'
    ];

    const result: Array<{ id: string; left: number; top: number; width: number; height: number }> = [];

    for (const id of ids) {
      const el = document.getElementById(id);
      if (el) {
        const rect = el.getBoundingClientRect();
        result.push({
          id,
          left: Math.round(rect.left),
          top: Math.round(rect.top),
          width: Math.round(rect.width),
          height: Math.round(rect.height)
        });
      }
    }
    return result;
  });

  const layoutPath = path.join(targetDir, 'layout-check.json');
  fs.writeFileSync(layoutPath, JSON.stringify(layoutCheck, null, 2));

  // Switch to Mobile viewport (375px)
  await page.setViewportSize({ width: 375, height: 812 });

  // Click Canon tab again if needed
  const mobileCanonTab = page.locator('#tab-btn-canon');
  await expect(mobileCanonTab).toBeVisible();
  await mobileCanonTab.click();

  // Capture Mobile Screenshot (375px)
  const mobilePath = path.join(targetDir, 'mobile-375.png');
  await page.screenshot({ path: mobilePath, fullPage: true });

  expect(fs.existsSync(desktopPath)).toBe(true);
  expect(fs.existsSync(mobilePath)).toBe(true);
  expect(fs.existsSync(layoutPath)).toBe(true);
});
