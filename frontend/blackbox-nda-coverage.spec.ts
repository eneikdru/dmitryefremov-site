import { test, expect } from '@playwright/test';

test.describe('Black Box Architecture & Absolute NDA Verification Suite', () => {
  // Encoded black-box non-leakage check signatures (decoded at runtime to enforce Tarski demarcation)
  const ENCODED_BLACK_BOX_SIGNATURES = [
    'QXV0b01lcmdlU2VydmljZQ==', // AutoMergeService
    'U2l4U2lnbWFBdWRpdA==',     // SixSigmaAudit
    'SnVsZXM=',                 // Jules
    'VGFza1BsYW4=',             // TaskPlan
    'UGxhbm5lZFdvcms=',         // PlannedWork
    'RW5laWtTeXM=',             // EneikSys
    'UG9rYS1Zb2tl'              // Poka-Yoke
  ];

  function getBlackBoxForbiddenTerms(): string[] {
    return ENCODED_BLACK_BOX_SIGNATURES.map((b64) => Buffer.from(b64, 'base64').toString('utf-8'));
  }

  test('Given full client submission flow, When DOM and network are inspected, Then zero internal terms leak and storage is secure', async ({ page }) => {
    const interceptedNetworkBodies: string[] = [];
    const interceptedUrls: string[] = [];
    const forbiddenTerms = getBlackBoxForbiddenTerms();

    // Mock API intake endpoint to return successful turnkey claim ticket
    await page.route('**/api/intake', async (route) => {
      const request = route.request();
      interceptedUrls.push(request.url());
      const postData = request.postData();
      if (postData) {
        interceptedNetworkBodies.push(postData);
      }

      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          status: 'success',
          claimTicket: '0x8f3c1a29b4e7031bc'
        })
      });
    });

    // Capture outgoing network requests
    page.on('request', (req) => {
      interceptedUrls.push(req.url());
      const body = req.postData();
      if (body) {
        interceptedNetworkBodies.push(body);
      }
    });

    // 1. Navigate to client platform
    await page.goto('/');

    // Dismiss CMP privacy consent modal if visible
    if (await page.locator('#cmp-accept-btn').isVisible()) {
      await page.click('#cmp-accept-btn');
    }

    // 2. Navigate to Contact & Secure Intake tab
    await page.click('#tab-btn-contact');
    await page.waitForSelector('#contact-terminal-root');

    // Switch to Secure Intake Form tab
    await page.click('#contact-tab-intake-btn');
    await page.waitForSelector('#vault-intake-root');

    // 3. Fill out confidential intake form
    await page.fill('#client-email-input', 'client.nda@organization.com');
    await page.fill('#submission-title-input', 'Turnkey Client Idea and Wishlist Submission');
    await page.fill(
      '#narrative-textarea',
      'Turnkey specification for sovereign digital node. Complete black box requirement with zero exposed internal mechanics.'
    );

    // Verify local storage draft was created while typing
    const savedDraftRaw = await page.evaluate(() => localStorage.getItem('vault_intake_draft'));
    expect(savedDraftRaw).not.toBeNull();
    const savedDraft = JSON.parse(savedDraftRaw!);
    expect(savedDraft.email).toBe('client.nda@organization.com');
    expect(savedDraft.title).toContain('Turnkey Client Idea');

    // Verify draft contents do not contain forbidden internal mechanics terms
    for (const term of forbiddenTerms) {
      expect(savedDraftRaw).not.toContain(term);
    }

    // 4. Submit intake payload
    await page.click('#submit-intake-btn');

    // 5. Verify success feedback and turnkey claim ticket
    await page.waitForSelector('#intake-success-feedback');
    await expect(page.locator('#claim-ticket-id')).toHaveText('0x8f3c1a29b4e7031bc');
    await expect(page.locator('#email-acknowledgement-notice')).toContainText('client.nda@organization.com');

    // 6. Verify local storage draft was cleared upon successful submission
    const draftAfterSubmit = await page.evaluate(() => localStorage.getItem('vault_intake_draft'));
    expect(draftAfterSubmit).toBeNull();

    // 7. Inspect DOM (innerText and full outerHTML) for zero internal mechanics leaks
    const bodyText = await page.innerText('body');
    const fullHtml = await page.content();

    for (const term of forbiddenTerms) {
      expect(bodyText).not.toContain(term);
      expect(fullHtml).not.toContain(term);
    }

    // 8. Inspect all intercepted network payloads and URLs for zero internal mechanics leaks
    for (const url of interceptedUrls) {
      for (const term of forbiddenTerms) {
        expect(url).not.toContain(term);
      }
    }

    for (const body of interceptedNetworkBodies) {
      for (const term of forbiddenTerms) {
        expect(body).not.toContain(term);
      }
    }
  });
});
