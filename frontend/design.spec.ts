import { test, expect } from '@playwright/test';
import * as fs from 'fs';
import * as path from 'path';

test('capture design screenshots and layout', async ({ page }) => {
  const recordDir = path.resolve(process.cwd(), '../.eneik/records/design-check-b14eb987-2a05-46aa-8c44-c7a9076a46ac');

  if (!fs.existsSync(recordDir)) {
    fs.mkdirSync(recordDir, { recursive: true });
  }

  // Set viewport to 1440px
  await page.setViewportSize({ width: 1440, height: 900 });
  await page.goto('http://localhost:4173');

  // Wait for the hero section to render
  await expect(page.locator('h1')).toContainText('Дмитрий Ефремов');

  // Capture 1440px screenshot
  await page.screenshot({ path: path.join(recordDir, 'desktop-1440.png'), fullPage: true });

  // Get layout geometry
  const layout = await page.evaluate(() => {
    const elements = document.querySelectorAll('header, main, h1, h2, footer, button');
    return Array.from(elements).map((el, index) => {
      const rect = el.getBoundingClientRect();
      let id = el.tagName.toLowerCase();
      if (id === 'button') {
        id = el.textContent?.trim() === 'Toggle theme' ? 'theme-toggle' : 'imprint-toggle';
      }
      return {
        id: `${id}-${index}`,
        left: rect.left,
        top: rect.top,
        width: rect.width,
        height: rect.height
      };
    });
  });

  fs.writeFileSync(
    path.join(recordDir, 'layout-check.json'),
    JSON.stringify(layout, null, 2)
  );

  // Set viewport to 375px
  await page.setViewportSize({ width: 375, height: 667 });

  // Capture 375px screenshot
  await page.screenshot({ path: path.join(recordDir, 'mobile-375.png'), fullPage: true });
});
