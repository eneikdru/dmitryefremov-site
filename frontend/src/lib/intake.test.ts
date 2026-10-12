// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import BlackBoxIntakeView from '../components/BlackBoxIntakeView.svelte';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';

describe('BlackBoxIntakeView Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
    return () => {
      if (component) {
        unmount(component);
      }
      document.body.removeChild(target);
    };
  });

  it('renders intake protocol form with default values and accessibility attributes', () => {
    component = mount(BlackBoxIntakeView, { target });

    expect(target.textContent).toContain('VAULT // INTAKE PROTOCOL');
    expect(target.textContent).toContain('256-BIT MUTUAL NDA ACTIVE');
    expect(target.textContent).toContain('Bilateral Non-Disclosure Agreement');

    const codenameInput = target.querySelector('#codename-input') as HTMLInputElement;
    expect(codenameInput).not.toBeNull();
    expect(codenameInput.value).toBe('Project Chimera // Series VII');

    const label = target.querySelector('label[for="codename-input"]');
    expect(label).not.toBeNull();
    expect(label?.textContent).toContain('INITIATIVE CODENAME');

    const submitBtn = target.querySelector('#submit-mandate-btn') as HTMLButtonElement;
    expect(submitBtn).not.toBeNull();
    expect(submitBtn.disabled).toBe(false);
  });

  it('allows changing confidentiality tier and engagement scope', async () => {
    component = mount(BlackBoxIntakeView, { target });

    const standardTierBtn = target.querySelector('#tier-standard-btn') as HTMLButtonElement;
    const airgappedTierBtn = target.querySelector('#tier-airgapped-btn') as HTMLButtonElement;

    expect(airgappedTierBtn.getAttribute('aria-pressed')).toBe('true');
    expect(standardTierBtn.getAttribute('aria-pressed')).toBe('false');

    standardTierBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(standardTierBtn.getAttribute('aria-pressed')).toBe('true');
    expect(airgappedTierBtn.getAttribute('aria-pressed')).toBe('false');
  });

  it('handles attached file removal', async () => {
    component = mount(BlackBoxIntakeView, { target });

    expect(target.textContent).toContain('chimera_core_assembly_v7.3.enc');

    const removeBtn = target.querySelector('button[aria-label="Remove attached file chimera_core_assembly_v7.3.enc"]') as HTMLButtonElement;
    expect(removeBtn).not.toBeNull();

    removeBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).not.toContain('chimera_core_assembly_v7.3.enc');
  });

  it('submits mandate and transitions to turnkey confirmation screen with zero factory leaks', async () => {
    component = mount(BlackBoxIntakeView, { target });

    const submitBtn = target.querySelector('#submit-mandate-btn') as HTMLButtonElement;
    submitBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('MANDATE RATIFIED & TRANSMITTED');
    expect(target.textContent).toContain('RECEIPT HASH: ESCROW-NDA-');
    expect(target.textContent).toContain('CONFIDENTIAL MANDATE SUMMARY');

    // Strict Black-Box invariant verification
    const renderedText = target.textContent || '';
    const forbiddenTerms = ['Jules', 'Theory of Constraints', 'Poka-Yoke', 'AutoMergeService', 'TaskPlan', 'EneikSys'];
    for (const term of forbiddenTerms) {
      expect(renderedText).not.toContain(term);
    }
    expect(renderedText).not.toMatch(/\bTOC\b/);

    // Reset form test
    const resetBtn = target.querySelector('#reset-mandate-btn') as HTMLButtonElement;
    expect(resetBtn).not.toBeNull();
    resetBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('VAULT // INTAKE PROTOCOL');
    expect(target.querySelector('#intake-form')).not.toBeNull();
  });

  it('integrates seamlessly with TelemetryHub tab navigation', async () => {
    component = mount(TelemetryHub, { target, props: { activeTab: 'intake' } });

    expect(target.textContent).toContain('VAULT // INTAKE PROTOCOL');

    const articleTabBtn = target.querySelector('#tab-btn-article') as HTMLButtonElement;
    articleTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('Articles & Thought Stream');

    const intakeTabBtn = target.querySelector('#tab-btn-intake') as HTMLButtonElement;
    intakeTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('VAULT // INTAKE PROTOCOL');
  });
});
