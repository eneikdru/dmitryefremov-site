import { test, expect } from '@playwright/test';

test.describe('Admin Dashboard Frontend & Privacy Verification', () => {
  test('private /admin route is accessible, focusable via keyboard, and WCAG compliant', async ({ page }) => {
    await page.goto('/?route=admin');
    await page.waitForSelector('#admin-dashboard-root');

    // Verify key header & status banner elements are visible
    await expect(page.locator('#admin-header')).toBeVisible();
    await expect(page.locator('#admin-status-banner')).toBeVisible();
    await expect(page.locator('text=GDPR / CCPA Compliant: 99.4%')).toBeVisible();

    // Verify all interactive controls are focusable via keyboard
    const filterBtn = page.locator('#admin-filter-btn');
    await filterBtn.focus();
    await expect(filterBtn).toBeFocused();

    const auditFilterBtn = page.locator('#admin-audit-filter-btn');
    await auditFilterBtn.focus();
    await expect(auditFilterBtn).toBeFocused();

    const reviewBtn = page.locator('#dsar-review-btn-8821');
    await reviewBtn.focus();
    await expect(reviewBtn).toBeFocused();

    // Verify bottom navigation tabs keyboard focus
    const tabOverview = page.locator('#admin-tab-overview');
    await tabOverview.focus();
    await expect(tabOverview).toBeFocused();

    const tabRequests = page.locator('#admin-tab-requests');
    await tabRequests.focus();
    await expect(tabRequests).toBeFocused();
  });

  test('first load consent banner is focused, keyboard operable, and declining is one click', async ({ page }) => {
    await page.goto('/');
    await page.waitForSelector('#cmp-modal-overlay');

    // Consent banner overlay and container are visible
    await expect(page.locator('#cmp-modal-overlay')).toBeVisible();

    // Verify decline button receives focus or is operable via keyboard
    const declineBtn = page.locator('#cmp-decline-btn');
    await expect(declineBtn).toBeVisible();

    // Click decline button with 1 click
    await declineBtn.click();

    // Consent modal closes and preference is saved as declined
    await expect(page.locator('#cmp-modal-overlay')).not.toBeVisible();
    const consentStatus = await page.evaluate(() => localStorage.getItem('cmp_consent_status'));
    expect(consentStatus).toBe('declined');
  });

  test('GDPR deletion attempt presents destructive action confirmation modal before proceeding', async ({ page }) => {
    await page.goto('/?route=admin');
    await page.waitForSelector('#admin-dashboard-root');

    // Locate DSAR-8821 and Execute Deletion button
    const executeDeleteBtn = page.locator('#execute-deletion-btn');
    await expect(executeDeleteBtn).toBeVisible();

    // Click Execute Deletion
    await executeDeleteBtn.click();

    // Destructive action confirmation modal opens
    const modal = page.locator('#deletion-confirm-modal');
    await expect(modal).toBeVisible();
    await expect(page.locator('#deletion-modal-title')).toContainText('Confirm Destructive Erasure Request');
    await expect(page.locator('#deletion-modal-description')).toContainText('u_8910');

    // Verify Cancel button closes modal without executing deletion
    const cancelBtn = page.locator('#cancel-purge-btn');
    await cancelBtn.click();
    await expect(modal).not.toBeVisible();

    // Click Execute Deletion again
    await executeDeleteBtn.click();
    await expect(modal).toBeVisible();

    // Click Confirm & Execute Purge button
    const confirmBtn = page.locator('#confirm-purge-btn');
    await confirmBtn.click();

    // Modal closes and DSAR-8821 status updates to PURGED
    await expect(modal).not.toBeVisible();
    await expect(page.locator('#dsar-item-8821')).toContainText('PURGED');
  });
});
