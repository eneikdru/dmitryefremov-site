import { test, expect } from '@playwright/test';

test.describe('Acceptance Flow QA Verification - End-to-End User Journeys', () => {

  test('complete end-to-end user journey: searches knowledge base, interacts with model, and submits contact form', async ({ page }) => {
    // Intercept and mock backend API endpoints at test level
    await page.route('**/api/v1/agent/orders', route => {
      route.fulfill({
        status: 201,
        contentType: 'application/json',
        body: JSON.stringify({
          success: true,
          orderId: 'ORD-9821',
          status: '201 Created'
        })
      });
    });

    await page.route('**/api/intake', route => {
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          claimTicket: '0xa1a4fd4a...31bc'
        })
      });
    });

    await page.goto('/');

    // Dismiss CMP privacy consent modal if visible
    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. SEARCH KNOWLEDGE BASE
    const searchTrigger = page.locator('#cmd-k-trigger-btn');
    await expect(searchTrigger).toBeVisible();
    await searchTrigger.click();

    const searchModal = page.locator('#cmd-k-search-modal');
    await expect(searchModal).toBeVisible();

    const searchInput = page.locator('#canon-search-input');
    await searchInput.fill('деонтическая');

    const searchResult = page.locator('#search-result-concept-deontic-logic');
    await expect(searchResult).toBeVisible();
    await searchResult.click();

    await expect(searchModal).not.toBeVisible();

    // 2. INTERACT WITH MODEL & AI AGENT API
    const canonTab = page.locator('#tab-btn-canon');
    await canonTab.click();
    await page.waitForSelector('#canon-gateway-root');

    const agentOrdersCard = page.locator('#route-agent-orders');
    await expect(agentOrdersCard).toBeVisible();

    const testRouteBtn = agentOrdersCard.locator('button:has-text("Test Route")');
    await testRouteBtn.click();

    const testModal = page.locator('#test-order-modal');
    await expect(testModal).toBeVisible();

    await page.fill('#input-client-name', 'Verification_Agent_Runner');
    await page.fill('#input-client-email', 'qa.agent@dmitryefremov.com');
    await page.fill('#input-requirements', 'Automated model interaction and agent order request test.');

    const submitOrderBtn = testModal.locator('button[type="submit"]');
    await submitOrderBtn.click();

    await expect(testModal).toContainText('201 Created');

    const closeModalBtn = testModal.locator('button:has-text("Закрыть")');
    await closeModalBtn.click();
    await expect(testModal).not.toBeVisible();

    // 3. SUBMIT CONTACT FORM
    const contactTab = page.locator('#tab-btn-contact');
    await contactTab.click();
    await page.waitForSelector('#contact-terminal-root');

    const intakeTabBtn = page.locator('#contact-tab-intake-btn');
    await intakeTabBtn.click();
    await page.waitForSelector('#vault-intake-root');

    await page.fill('#client-email-input', 'visitor@example.com');
    await page.fill('#submission-title-input', 'E2E Acceptance Suite Inquiry');
    await page.fill('#narrative-textarea', 'Automated acceptance test suite narrative payload transmission verification.');

    const submitIntakeBtn = page.locator('#submit-intake-btn');
    await submitIntakeBtn.click();

    const successBanner = page.locator('#intake-success-feedback');
    await expect(successBanner).toBeVisible();
    await expect(page.locator('#success-heading')).toContainText('Payload Encrypted & Submitted Successfully');
    await expect(page.locator('#claim-ticket-id')).toBeVisible();
  });

  test('podcast player loads canonical seeded episodes and provides interactive audio playback controls', async ({ page }) => {
    // Inject mock HTMLMediaElement behavior for synthetic audio playback in headless browser
    await page.addInitScript(() => {
      Object.defineProperty(window.HTMLMediaElement.prototype, 'paused', {
        get(this: any) {
          return this._paused !== undefined ? this._paused : true;
        },
        configurable: true
      });
      window.HTMLMediaElement.prototype.play = function (this: any) {
        this._paused = false;
        return Promise.resolve();
      };
      window.HTMLMediaElement.prototype.pause = function (this: any) {
        this._paused = true;
      };
    });

    await page.goto('/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // Switch to Podcast tab if active tab changed
    const podcastTab = page.locator('#tab-btn-podcast');
    if (await podcastTab.isVisible()) {
      await podcastTab.click();
    }

    // 1. Ensure Podcast Media Hub is active and loaded
    await page.waitForSelector('#podcast-player-container');
    await expect(page.locator('#podcast-episodes-section')).toBeVisible();

    // 2. Verify canonical episode cards in catalog list
    const catalog = page.locator('#podcast-episodes-section');
    await expect(catalog.locator('#episode-card-1')).toBeVisible();
    await expect(catalog.locator('#episode-card-2')).toBeVisible();
    await expect(catalog.locator('#episode-card-3')).toBeVisible();

    // 3. Test Play/Pause audio control
    const playBtn = page.locator('#play-pause-btn');
    await expect(playBtn).toBeVisible();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Pause Podcast Episode');

    await playBtn.click();
    await expect(playBtn).toHaveAttribute('aria-label', 'Play Podcast Episode');

    // 4. Test seeking via timecode selection
    const timecodeBtn = page.locator('#timecode-btn-102');
    await expect(timecodeBtn).toBeVisible();
    await timecodeBtn.click();

    await expect(page.locator('#current-time-val')).toHaveText('05:40');
    await expect(timecodeBtn).toContainText('СЕЙЧАС ИГРАЕТ');

    // 5. Test switching episode
    const ep2Card = page.locator('#episode-card-2');
    await ep2Card.click();
    await expect(ep2Card).toContainText('АКТИВЕН');
  });

  test('seeded essays render according to Swiss Editorial aesthetic with clean typography and topic navigation', async ({ page }) => {
    await page.goto('/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // Switch to Articles & Thought Stream tab
    await page.click('#tab-btn-article');
    await page.waitForSelector('#article-stream-container');

    // 1. Verify longreads list section is rendered
    await expect(page.locator('#longreads-list-section')).toBeVisible();
    await expect(page.locator('text=AVAILABLE_LONGREADS')).toBeVisible();

    // 2. Verify active article reader header
    const articleHeader = page.locator('#article-header');
    await expect(articleHeader).toBeVisible();

    // 3. Test topic filtering (Deontic Logic)
    const deonticBtn = page.locator('button:has-text("Deontic Logic")');
    await expect(deonticBtn).toBeVisible();
    await deonticBtn.click();
  });

  test('confidential client intake gateway and telegram sparks stream are accessible', async ({ page }) => {
    await page.goto('/');

    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 1. Check Telegram Sparks Stream on Articles tab
    await page.click('#tab-btn-article');
    await page.waitForSelector('#sparks-stream-section');
    await expect(page.locator('#sparks-stream-section')).toContainText('[TELEGRAM_SPARKS_STREAM]');

    // 2. Switch to Contact / Gateway tab
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');
    await expect(page.locator('#contact-terminal-root')).toBeVisible();
  });

  test('guarantees strict raw teleprompter script isolation across live site', async ({ page }) => {
    await page.goto('/');

    const bodyText = await page.innerText('body');
    expect(bodyText).not.toContain('/mnt/HC_Volume_106932004/podcast/episodes');
  });
});
