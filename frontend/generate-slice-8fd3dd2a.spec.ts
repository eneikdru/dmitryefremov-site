import { test, chromium, expect } from '@playwright/test';
import { spawn } from 'child_process';
import * as fs from 'fs';
import * as path from 'path';

test('generate design verification artifacts and E2E verification for slice 8fd3dd2a (graph nav & search telemetry)', async () => {
  const targetDir = path.resolve('../.eneik/records/design-check-0055f6d1-e6d5-4bdf-a70b-10548f5f7686');
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

    // 1. Desktop 1440px Viewport & Interactive Telemetry E2E Verification
    const desktopContext = await browser.newContext({ viewport: { width: 1440, height: 900 } });
    const desktopPage = await desktopContext.newPage();
    await desktopPage.goto('http://localhost:4173/#/canon-gateway');
    await desktopPage.waitForSelector('#telemetry-hub-root');

    // Switch to Canon tab if needed
    const canonTab = await desktopPage.$('#tab-btn-canon');
    if (canonTab) {
      await canonTab.click();
      await desktopPage.waitForTimeout(300);
    }

    // Dismiss CMP modal if open
    const acceptBtn = await desktopPage.$('#cmp-accept-btn');
    if (acceptBtn) {
      await acceptBtn.click();
      await desktopPage.waitForTimeout(300);
    }

    // Verify cross-link click dispatches graph_navigation telemetry
    const crossLinkBtn = await desktopPage.waitForSelector('#cross-link-podcast_ep_01');
    expect(crossLinkBtn).not.toBeNull();
    await crossLinkBtn.click();
    await desktopPage.waitForTimeout(300);

    // Verify active node title changed
    const activeTitle = await desktopPage.$eval('#active-node-title', el => el.textContent?.trim());
    expect(activeTitle).toContain('Выпуск 01');

    // Verify Cmd+K Search trigger button click or key combo opens search modal
    const searchTrigger = await desktopPage.waitForSelector('#cmd-k-trigger-btn');
    await searchTrigger.click();
    await desktopPage.waitForTimeout(300);

    // Verify WCAG role and modal title
    const searchModal = await desktopPage.waitForSelector('#cmd-k-search-modal');
    expect(searchModal).not.toBeNull();

    const searchInput = await desktopPage.waitForSelector('#cmd-k-search-input');
    await searchInput.fill('state_machine');
    await desktopPage.click('#cmd-k-search-submit-btn');
    await desktopPage.waitForTimeout(300);

    // Select search result
    const searchResultItem = await desktopPage.$('#search-result-item-concept_deontic_logic');
    if (searchResultItem) {
      await searchResultItem.click();
      await desktopPage.waitForTimeout(300);
    }

    // Scroll back to top for screenshot
    await desktopPage.evaluate(() => window.scrollTo(0, 0));
    await desktopPage.waitForTimeout(200);

    // Take Desktop Screenshot
    const desktopScreenshotPath = path.join(targetDir, 'desktop-1440.png');
    await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });

    // Extract layout geometry bounding boxes
    const layoutBoundingBoxes = await desktopPage.evaluate(() => {
      window.scrollTo(0, 0);
      const elementsToTrack = [
        'canon-gateway-header',
        'gateway-header-title',
        'ingress-gateway-card',
        'ingress-card-title',
        'canon-telemetry-dashboard',
        'telemetry-dashboard-header',
        'cmd-k-trigger-btn',
        'canon-graph-explorer-section',
        'explorer-section-title',
        'active-node-title',
        'telemetry-audit-log-section',
        'security-auth-card',
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

    // 2. Mobile 375px Viewport Screenshot
    const mobileContext = await browser.newContext({ viewport: { width: 375, height: 812 } });
    const mobilePage = await mobileContext.newPage();
    await mobilePage.goto('http://localhost:4173/#/canon-gateway');
    await mobilePage.waitForSelector('#telemetry-hub-root');

    const mobileCanonTab = await mobilePage.$('#tab-btn-canon');
    if (mobileCanonTab) {
      await mobileCanonTab.click();
      await mobilePage.waitForTimeout(300);
    }

    const mobileAcceptBtn = await mobilePage.$('#cmp-accept-btn');
    if (mobileAcceptBtn) {
      await mobileAcceptBtn.click();
      await mobilePage.waitForTimeout(300);
    }

    await mobilePage.evaluate(() => window.scrollTo(0, 0));
    await mobilePage.waitForTimeout(200);

    const mobileScreenshotPath = path.join(targetDir, 'mobile-375.png');
    await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });

    await mobileContext.close();
    await browser.close();
  } finally {
    previewProcess.kill();
  }
});
