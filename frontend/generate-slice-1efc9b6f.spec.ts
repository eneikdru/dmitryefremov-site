import { test, expect, chromium } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design gate verification artifacts for slice 1efc9b6f', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-4c98639d-6487-4aaf-9fa7-d52081143d53');
  fs.mkdirSync(targetDir, { recursive: true });

  // Build frontend
  const buildProcess = spawn('npx', ['vite', 'build'], {
    cwd: path.resolve('.'),
    stdio: 'inherit'
  });

  await new Promise<void>((resolve, reject) => {
    buildProcess.on('exit', (code) => {
      if (code === 0) resolve();
      else reject(new Error(`vite build failed with code ${code}`));
    });
  });

  // Start preview server
  const previewProcess = spawn('npx', ['vite', 'preview', '--port', '4173'], {
    cwd: path.resolve('.'),
    stdio: 'ignore'
  });

  await new Promise(resolve => setTimeout(resolve, 2500));

  try {
    const browser = await chromium.launch();

    // -------------------------------------------------------------
    // 1440px Desktop Test & Artifact Generation
    // -------------------------------------------------------------
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Dismiss CMP modal if open
    const acceptBtn = await desktopPage.$('#cmp-accept-btn');
    if (acceptBtn) {
      await acceptBtn.click();
      await desktopPage.waitForTimeout(300);
    }

    // Switch to Conceptual Navigator tab
    const navigatorTabBtn = await desktopPage.$('#tab-btn-navigator');
    expect(navigatorTabBtn).not.toBeNull();
    await navigatorTabBtn!.click();
    await desktopPage.waitForTimeout(300);

    // Verify Conceptual Navigator elements are visible
    await expect(desktopPage.locator('#conceptual-navigator-root')).toBeVisible();
    await expect(desktopPage.locator('#concept-search-input')).toBeVisible();

    // Acceptance Criteria 1: Dynamic Search
    await desktopPage.fill('#concept-search-input', 'Hacker');
    await desktopPage.waitForTimeout(200);
    await expect(desktopPage.locator('#manifesto-hacker')).toBeVisible();
    await expect(desktopPage.locator('#manifesto-speculative-realism')).not.toBeVisible();

    // Acceptance Criteria 2: Empty State & Reset Option
    await desktopPage.fill('#concept-search-input', 'nonexistenttermxyz');
    await desktopPage.waitForTimeout(200);
    await expect(desktopPage.locator('#conceptual-navigator-empty-state')).toBeVisible();
    await expect(desktopPage.locator('#reset-search-filters-btn')).toBeVisible();

    // Reset filters
    await desktopPage.click('#reset-search-filters-btn');
    await desktopPage.waitForTimeout(200);
    await expect(desktopPage.locator('#manifesto-hacker')).toBeVisible();
    await expect(desktopPage.locator('#manifesto-speculative-realism')).toBeVisible();

    await desktopPage.evaluate(() => window.scrollTo(0, 0));
    await desktopPage.waitForTimeout(200);

    // Capture Desktop Screenshot (1440px)
    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      window.scrollTo(0, 0);
      const elementsToTrack = [
        'sys-core-badge',
        'cmd-k-trigger-btn',
        'theme-toggle-btn',
        'hero-section',
        'nav-tabs',
        'tab-btn-navigator',
        'conceptual-navigator-root',
        'navigator-header',
        'concept-query-section',
        'concept-tags-section',
        'concept-radar-card',
        'manifestos-list',
        'main-footer'
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

    // -------------------------------------------------------------
    // 375px Mobile Viewport Test & Artifact Generation
    // -------------------------------------------------------------
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173');
    await mobilePage.waitForSelector('#telemetry-hub-root');

    // Dismiss CMP modal on mobile
    const mobileAcceptBtn = await mobilePage.$('#cmp-accept-btn');
    if (mobileAcceptBtn) {
      await mobileAcceptBtn.click();
      await mobilePage.waitForTimeout(300);
    }

    const mobileNavigatorTabBtn = await mobilePage.$('#tab-btn-navigator');
    if (mobileNavigatorTabBtn) {
      await mobileNavigatorTabBtn.click();
      await mobilePage.waitForTimeout(300);
    }

    // Verify layout adapts without horizontal scrollbar
    const hasHorizontalOverflow = await mobilePage.evaluate(() => {
      return document.documentElement.scrollWidth > document.documentElement.clientWidth;
    });
    expect(hasHorizontalOverflow).toBe(false);

    await mobilePage.evaluate(() => window.scrollTo(0, 0));
    await mobilePage.waitForTimeout(200);

    // Capture Mobile Screenshot (375px)
    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
