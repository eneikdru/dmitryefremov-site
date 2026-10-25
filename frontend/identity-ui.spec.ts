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

    await page.route('**/api/v1/podcasts', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: 101, slug: 'rasshirennyy-razum-ep5', philosopher: 'Рут Баркан Маркус', title: 'Выпуск 05. Концепция расширенного разума и внешней памяти', durationSeconds: 1200, timecodes: [ { id: 1011, timeOffsetSeconds: 0, title: 'Введение' }, { id: 102, timeOffsetSeconds: 340, title: 'Topic 2' }, { id: 103, timeOffsetSeconds: 920, title: 'Topic 3' } ] }, { id: 102, slug: 'paul-thagard-ep6', philosopher: 'Пол Таггард', title: 'Пол Таггард', durationSeconds: 1200, timecodes: [ { id: 1021, timeOffsetSeconds: 0, title: 'Введение' }, { id: 1022, timeOffsetSeconds: 340, title: 'Topic 2' } ] }, { id: 103, slug: 'susan-hurley-ep7', philosopher: 'Сюзан Хёрли', title: 'Сюзан Хёрли', durationSeconds: 1200, timecodes: [] }]) }); });    await page.route('**/api/v1/essays', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: '1', slug: 'high-end-editorial-minimalism', title: 'Эстетика инженерного молчания и редакционный минимализм', topic: 'systems', summary: 'Summary', content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"' }, { id: '2', slug: 'deontic-process-autonomy', title: 'Деонтическая логика и нормативные инварианты', topic: 'deontic', summary: 'Summary', content: 'Content' }, { id: '3', slug: 'deterministic-state', title: 'Deterministic State Determinism in Autonomous Loops', topic: 'autonomous', summary: 'Summary', content: 'Replayability Condition' }, { id: '4', slug: 'deontic-logic-autonomous-process', title: 'Deontic Logic in Autonomous Process Architecture', topic: 'deontic', summary: 'Summary', content: 'Content' }]) }); });    await page.route('**/api/v1/articles', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: '1', slug: 'high-end-editorial-minimalism', title: 'Эстетика инженерного молчания и редакционный минимализм', topic: 'systems', summary: 'Summary', content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"' }, { id: '2', slug: 'deontic-process-autonomy', title: 'Деонтическая логика и нормативные инварианты', topic: 'deontic', summary: 'Summary', content: 'Content' }, { id: '3', slug: 'deterministic-state', title: 'Deterministic State Determinism in Autonomous Loops', topic: 'autonomous', summary: 'Summary', content: 'Replayability Condition' }, { id: '4', slug: 'deontic-logic-autonomous-process', title: 'Deontic Logic in Autonomous Process Architecture', topic: 'deontic', summary: 'Summary', content: 'Content' }]) }); });    await page.route('**/api/v1/sparks', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: 'spk-1', content: 'Telegram spark content', domain: 'Systems', tags: '#systems' }]) }); });
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
    await page.route('**/api/v1/podcasts', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: 101, slug: 'rasshirennyy-razum-ep5', philosopher: 'Рут Баркан Маркус', title: 'Выпуск 05. Концепция расширенного разума и внешней памяти', durationSeconds: 1200, timecodes: [ { id: 1011, timeOffsetSeconds: 0, title: 'Введение' }, { id: 102, timeOffsetSeconds: 340, title: 'Topic 2' }, { id: 103, timeOffsetSeconds: 920, title: 'Topic 3' } ] }, { id: 102, slug: 'paul-thagard-ep6', philosopher: 'Пол Таггард', title: 'Пол Таггард', durationSeconds: 1200, timecodes: [ { id: 1021, timeOffsetSeconds: 0, title: 'Введение' }, { id: 1022, timeOffsetSeconds: 340, title: 'Topic 2' } ] }, { id: 103, slug: 'susan-hurley-ep7', philosopher: 'Сюзан Хёрли', title: 'Сюзан Хёрли', durationSeconds: 1200, timecodes: [] }]) }); });    await page.route('**/api/v1/essays', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: '1', slug: 'high-end-editorial-minimalism', title: 'Эстетика инженерного молчания и редакционный минимализм', topic: 'systems', summary: 'Summary', content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"' }, { id: '2', slug: 'deontic-process-autonomy', title: 'Деонтическая логика и нормативные инварианты', topic: 'deontic', summary: 'Summary', content: 'Content' }, { id: '3', slug: 'deterministic-state', title: 'Deterministic State Determinism in Autonomous Loops', topic: 'autonomous', summary: 'Summary', content: 'Replayability Condition' }, { id: '4', slug: 'deontic-logic-autonomous-process', title: 'Deontic Logic in Autonomous Process Architecture', topic: 'deontic', summary: 'Summary', content: 'Content' }]) }); });    await page.route('**/api/v1/articles', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: '1', slug: 'high-end-editorial-minimalism', title: 'Эстетика инженерного молчания и редакционный минимализм', topic: 'systems', summary: 'Summary', content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"' }, { id: '2', slug: 'deontic-process-autonomy', title: 'Деонтическая логика и нормативные инварианты', topic: 'deontic', summary: 'Summary', content: 'Content' }, { id: '3', slug: 'deterministic-state', title: 'Deterministic State Determinism in Autonomous Loops', topic: 'autonomous', summary: 'Summary', content: 'Replayability Condition' }, { id: '4', slug: 'deontic-logic-autonomous-process', title: 'Deontic Logic in Autonomous Process Architecture', topic: 'deontic', summary: 'Summary', content: 'Content' }]) }); });    await page.route('**/api/v1/sparks', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: 'spk-1', content: 'Telegram spark content', domain: 'Systems', tags: '#systems' }]) }); });
    await pageMobile.route('**/api/v1/podcasts', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: 101, slug: 'rasshirennyy-razum-ep5', philosopher: 'Рут Баркан Маркус', title: 'Выпуск 05. Концепция расширенного разума и внешней памяти', durationSeconds: 1200, timecodes: [ { id: 1011, timeOffsetSeconds: 0, title: 'Введение' }, { id: 102, timeOffsetSeconds: 340, title: 'Topic 2' }, { id: 103, timeOffsetSeconds: 920, title: 'Topic 3' } ] }, { id: 102, slug: 'paul-thagard-ep6', philosopher: 'Пол Таггард', title: 'Пол Таггард', durationSeconds: 1200, timecodes: [ { id: 1021, timeOffsetSeconds: 0, title: 'Введение' }, { id: 1022, timeOffsetSeconds: 340, title: 'Topic 2' } ] }, { id: 103, slug: 'susan-hurley-ep7', philosopher: 'Сюзан Хёрли', title: 'Сюзан Хёрли', durationSeconds: 1200, timecodes: [] }]) }); });    await pageMobile.route('**/api/v1/essays', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: '1', slug: 'high-end-editorial-minimalism', title: 'Эстетика инженерного молчания и редакционный минимализм', topic: 'systems', summary: 'Summary', content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"' }, { id: '2', slug: 'deontic-process-autonomy', title: 'Деонтическая логика и нормативные инварианты', topic: 'deontic', summary: 'Summary', content: 'Content' }, { id: '3', slug: 'deterministic-state', title: 'Deterministic State Determinism in Autonomous Loops', topic: 'autonomous', summary: 'Summary', content: 'Replayability Condition' }, { id: '4', slug: 'deontic-logic-autonomous-process', title: 'Deontic Logic in Autonomous Process Architecture', topic: 'deontic', summary: 'Summary', content: 'Content' }]) }); });    await pageMobile.route('**/api/v1/articles', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: '1', slug: 'high-end-editorial-minimalism', title: 'Эстетика инженерного молчания и редакционный минимализм', topic: 'systems', summary: 'Summary', content: '# Эстетика инженерного молчания и редакционный минимализм\n\n## Введение\n\nНастоящая архитектура не заявляет о себе громкими терминальными логами.\n\n> "Настоящая архитектура не заявляет о себе громкими терминальными логами"' }, { id: '2', slug: 'deontic-process-autonomy', title: 'Деонтическая логика и нормативные инварианты', topic: 'deontic', summary: 'Summary', content: 'Content' }, { id: '3', slug: 'deterministic-state', title: 'Deterministic State Determinism in Autonomous Loops', topic: 'autonomous', summary: 'Summary', content: 'Replayability Condition' }, { id: '4', slug: 'deontic-logic-autonomous-process', title: 'Deontic Logic in Autonomous Process Architecture', topic: 'deontic', summary: 'Summary', content: 'Content' }]) }); });    await pageMobile.route('**/api/v1/sparks', async route => { await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify([{ id: 'spk-1', content: 'Telegram spark content', domain: 'Systems', tags: '#systems' }]) }); });
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
