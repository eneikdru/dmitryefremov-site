// @vitest-environment jsdom
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import AppliedModelsView from '../components/AppliedModelsView.svelte';
import { mount, unmount } from 'svelte';

describe('AppliedModelsView Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
  });

  afterEach(() => {
    if (component) {
      unmount(component);
    }
    document.body.removeChild(target);
  });

  it('renders playground header correctly', () => {
    component = mount(AppliedModelsView, { target });
    expect(target.textContent).toContain('ACTIVE INFERENCE ENGINE');
  });

  it('updates text area when preset is clicked', async () => {
    component = mount(AppliedModelsView, { target });
    const textarea = target.querySelector('textarea') as HTMLTextAreaElement;
    const initialValue = textarea.value;

    const btn = Array.from(target.querySelectorAll('button')).find(
      (b) => b.textContent?.includes('Customer Support QA')
    );

    if (btn) {
       btn.dispatchEvent(new MouseEvent('click', { bubbles: true }));
       // svelte 5 state is synchronous, but dom update may not be.
       await new Promise(r => setTimeout(r, 0));
       expect(textarea.value).toEqual(initialValue); // should be same since we clicked the default
    }

    const btn2 = Array.from(target.querySelectorAll('button')).find(
      (b) => b.textContent?.includes('Receipt OCR Parse')
    );

    if(btn2) {
       btn2.dispatchEvent(new MouseEvent('click', { bubbles: true }));
       await new Promise(r => setTimeout(r, 0));
       expect(textarea.value).toContain('WALMART SUPERCENTER');
    }
  });

  it('switches tabs correctly', async () => {
      component = mount(AppliedModelsView, { target });

      const rawTokensTab = Array.from(target.querySelectorAll('button.output-tab')).find(
        (b) => b.textContent?.includes('Raw Tokens & Confidence')
      );

      expect(rawTokensTab).toBeDefined();

      if (rawTokensTab) {
          rawTokensTab.dispatchEvent(new MouseEvent('click', { bubbles: true }));
          await new Promise(r => setTimeout(r, 0));
          const tabViewTokens = target.querySelector('#tab-view-tokens') as HTMLElement;
          expect(tabViewTokens.className).not.toContain('hidden');
          const tabViewJson = target.querySelector('#tab-view-json') as HTMLElement;
          expect(tabViewJson.className).toContain('hidden');
      }
  });

  it('has accessible buttons with valid roles/labels', () => {
       component = mount(AppliedModelsView, { target });
       const streamingSwitch = target.querySelector('[role="switch"]');
       expect(streamingSwitch).not.toBeNull();
  });
});
