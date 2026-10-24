import { test, chromium } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

test('generate design gate verification artifacts for slice F2410d38 (Agent Canon Access)', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-9899262f-9012-4e5e-a32c-99771b5a3271');
  fs.mkdirSync(targetDir, { recursive: true });

  const browser = await chromium.launch();

  // 1440px Desktop Screenshot
  const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
  const desktopPage = await desktopContext.newPage();
  await desktopPage.goto('http://localhost:4173');

  // Dismiss CMP modal if present
  const acceptBtn = await desktopPage.$('#cmp-accept-btn');
  if (acceptBtn) {
    await acceptBtn.click();
    await desktopPage.waitForTimeout(300);
  }

  // Click on Agent Canon Tab
  await desktopPage.waitForSelector('#tab-btn-canon');
  await desktopPage.click('#tab-btn-canon');
  await desktopPage.waitForSelector('#agent-canon-root');

  await desktopPage.evaluate(() => window.scrollTo(0, 0));
  await desktopPage.waitForTimeout(300);

  const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
  await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

  // Extract layout geometry bounding boxes
  const layoutBoundingBoxes = await desktopPage.evaluate(() => {
    window.scrollTo(0, 0);
    const elementsToTrack = [
      'agent-canon-header',
      'epic-header-card',
      'epic-title',
      'epic-description',
      'stats-ribbon',
      'format-delegation-card',
      'format-delegation-title',
      'format-select-path',
      'btn-fetch-format',
      'route-definitions-section',
      'route-definitions-title',
      'route-card-query',
      'route-card-entities',
      'route-card-stream',
      'security-guard-card',
      'security-guard-title',
      'sandbox-actions-grid',
      'btn-test-dispatch',
      'btn-export-openapi',
      'agent-bottom-nav'
    ];

    return elementsToTrack.map(id => {
      const el = document.getElementById(id);
      if (!el) return { id, left: 0, top: 0, width: 0, height: 0 };
      const rect = el.getBoundingClientRect();
      return {
        id,
        left: Math.round(rect.left + window.scrollX),
        top: Math.round(rect.top + window.scrollY),
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
  await mobilePage.goto('http://localhost:4173');

  // Dismiss CMP modal on mobile if present
  const mobileAcceptBtn = await mobilePage.$('#cmp-accept-btn');
  if (mobileAcceptBtn) {
    await mobileAcceptBtn.click();
    await mobilePage.waitForTimeout(300);
  }

  await mobilePage.waitForSelector('#tab-btn-canon');
  await mobilePage.click('#tab-btn-canon');
  await mobilePage.waitForSelector('#agent-canon-root');

  await mobilePage.evaluate(() => window.scrollTo(0, 0));
  await mobilePage.waitForTimeout(300);

  const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
  await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

  await mobileContext.close();
  await browser.close();
});
