const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

async function runVerification() {
  console.log('Starting frontend UI verification with Playwright...');

  const htmlPath = path.resolve(__dirname, 'podcast-preview.html');
  const fileUrl = `file://${htmlPath}`;

  const recordDir = path.resolve(__dirname, '../.eneik/records/design-check-67c6d186-4f25-4d6b-87a6-393a9d5e67d9');
  fs.mkdirSync(recordDir, { recursive: true });

  const browser = await chromium.launch();

  // Test 1: Desktop Viewport (1440px)
  console.log('Testing Desktop 1440px...');
  const desktopContext = await browser.newContext({
    viewport: { width: 1440, height: 900 },
    deviceScaleFactor: 1
  });
  const desktopPage = await desktopContext.newPage();
  await desktopPage.goto(fileUrl);
  await desktopPage.waitForLoadState('networkidle');

  // Verify Audio Player is displayed
  const isPlayerVisible = await desktopPage.isVisible('[data-testid="podcast-player"]');
  console.log(' - Podcast player visible:', isPlayerVisible);
  if (!isPlayerVisible) throw new Error('Podcast player element not visible');

  // Test clicking play/pause button
  await desktopPage.click('[data-testid="play-pause-button"]');
  const statusText = await desktopPage.innerText('#status-indicator');
  console.log(' - Status after play toggle:', statusText);
  if (statusText !== 'PLAYING') throw new Error('Play toggle failed to update status to PLAYING');

  // Test clicking a timecode (e.g., timecode item 3 - 540s / 09:00)
  console.log(' - Clicking timecode item 3 (540s)...');
  await desktopPage.click('[data-testid="timecode-item-3"]');
  const timeTextAfterTc = await desktopPage.innerText('#current-time-display');
  console.log(' - Time display after timecode click:', timeTextAfterTc);
  if (timeTextAfterTc !== '09:00') throw new Error(`Timecode seeking failed! Expected 09:00, got ${timeTextAfterTc}`);

  // Test clicking waveform bar (e.g. bar 30)
  console.log(' - Clicking waveform bar 30...');
  await desktopPage.click('[data-testid="wave-bar-30"]');
  const timeTextAfterWave = await desktopPage.innerText('#current-time-display');
  console.log(' - Time display after wave bar click:', timeTextAfterWave);
  if (timeTextAfterWave !== '22:30') throw new Error(`Wavebar seeking failed! Expected 22:30, got ${timeTextAfterWave}`);

  // Take Desktop Screenshot
  const desktopScreenshotPath = path.join(recordDir, 'desktop-1440.png');
  await desktopPage.screenshot({ path: desktopScreenshotPath, fullPage: true });
  console.log(' - Saved Desktop Screenshot to:', desktopScreenshotPath);

  await desktopContext.close();

  // Test 2: Mobile Viewport (375px)
  console.log('Testing Mobile 375px...');
  const mobileContext = await browser.newContext({
    viewport: { width: 375, height: 812 },
    deviceScaleFactor: 1
  });
  const mobilePage = await mobileContext.newPage();
  await mobilePage.goto(fileUrl);
  await mobilePage.waitForLoadState('networkidle');

  // Take Mobile Screenshot
  const mobileScreenshotPath = path.join(recordDir, 'mobile-375.png');
  await mobilePage.screenshot({ path: mobileScreenshotPath, fullPage: true });
  console.log(' - Saved Mobile Screenshot to:', mobileScreenshotPath);

  // Extract layout bounding boxes for key elements
  const layoutElementIds = ['header', 'player-controls', 'waveform', 'timecodes-list'];
  const layoutBoxes = [];

  for (const id of layoutElementIds) {
    const box = await mobilePage.locator(`#${id}`).boundingBox();
    if (box) {
      layoutBoxes.push({
        id,
        left: Math.round(box.x),
        top: Math.round(box.y),
        width: Math.round(box.width),
        height: Math.round(box.height)
      });
    }
  }

  const layoutJsonPath = path.join(recordDir, 'layout-check.json');
  fs.writeFileSync(layoutJsonPath, JSON.stringify(layoutBoxes, null, 2));
  console.log(' - Saved Layout Check JSON to:', layoutJsonPath);

  await mobileContext.close();
  await browser.close();

  console.log('Frontend UI verification successfully completed!');
}

runVerification().catch((err) => {
  console.error('Verification failed:', err);
  process.exit(1);
});
