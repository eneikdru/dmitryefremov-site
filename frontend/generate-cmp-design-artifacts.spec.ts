import { test, chromium, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts for consent platform integration', async ({ browser }) => {
  const targetDir = path.resolve('../.eneik/records/design-check-f551359a-6f0e-47c1-92a7-cc616ba59ef2');
  fs.mkdirSync(targetDir, { recursive: true });

  // 1440px Desktop Screenshot
  const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const desktopPage = await desktopContext.newPage();
  await desktopPage.goto('/');
  await desktopPage.waitForSelector('#telemetry-hub-root');
  await desktopPage.waitForSelector('#cmp-modal-overlay');
  await desktopPage.waitForTimeout(300);

  const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
  await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

  // Bounding box extraction
  const layoutBoundingBoxes = await desktopPage.evaluate(() => {
    const elementsToTrack = [
      'main-header',
      'hero-section',
      'content-view',
      'cmp-modal-overlay',
      'cmp-container',
      'cmp-badge',
      'cmp-title',
      'cmp-description',
      'cmp-actions',
      'cmp-decline-btn',
      'cmp-accept-btn',
      'cmp-save-btn',
      'main-footer'
    ];

    return elementsToTrack.map(id => {
      const el = document.getElementById(id);
      if (!el) return { id, left: 0, top: 0, width: 0, height: 0 };
      const rect = el.getBoundingClientRect();
      return {
        id,
        left: Math.round(rect.left),
        top: Math.round(rect.top),
        width: Math.round(rect.width),
        height: Math.round(rect.height)
      };
    });
  });

  const layoutCheckPath = path.join(targetDir, 'layout-check.json');
  fs.writeFileSync(layoutCheckPath, JSON.stringify(layoutBoundingBoxes, null, 2));

  await desktopContext.close();

  // 375px Mobile Screenshot
  const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
  const mobilePage = await mobileContext.newPage();
  await mobilePage.goto('/');
  await mobilePage.waitForSelector('#telemetry-hub-root');
  await mobilePage.waitForSelector('#cmp-modal-overlay');
  await mobilePage.waitForTimeout(300);

  const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
  await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

  await mobileContext.close();
});
