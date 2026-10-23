import { test, expect } from '@playwright/test';

test.describe('SEO and Bot Crawler QA Verification', () => {

  test('validates DOM presence of SEO tags, Open Graph, and Bot Crawlers directives', async ({ page }) => {
    await page.goto('http://localhost:4173');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    await expect(page).toHaveTitle('Дмитрий Ефремов — Архитектура систем, философия сознания и автономные процессы');
    await expect(page.locator('meta[name="description"]').first()).toHaveAttribute('content', 'Суверенный интеллектуальный узел Дмитрия Ефремова. 9 выпусков подкаста «Системы и реальность», онтология автономии, A2A-экономика и эссе по деонтической логике.');

    await expect(page.locator('meta[name="GPTBot"]').first()).toHaveAttribute('content', 'index, follow, max-snippet:-1');
    await expect(page.locator('meta[name="PerplexityBot"]').first()).toHaveAttribute('content', 'index, follow, max-snippet:-1');
    await expect(page.locator('meta[name="ClaudeBot"]').first()).toHaveAttribute('content', 'index, follow, max-snippet:-1');

    await expect(page.locator('meta[property="og:title"]').first()).toHaveAttribute('content', 'Дмитрий Ефремов — Архитектура систем, философия сознания и автономные процессы');
    await expect(page.locator('meta[property="og:type"]').first()).toHaveAttribute('content', 'website');

    const jsonLdLocator = page.locator('script[type="application/ld+json"]');
    await expect(jsonLdLocator).toBeAttached();

    const jsonLdContent = await jsonLdLocator.textContent();
    expect(jsonLdContent).not.toBeNull();
    const parsed = JSON.parse(jsonLdContent!);
    expect(parsed['@context']).toBe('https://schema.org');
    expect(parsed['@type']).toBe('WebSite');
  });
});
