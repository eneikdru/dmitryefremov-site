// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import IntakeDossierView from '../components/IntakeDossierView.svelte';
import { mount, unmount } from 'svelte';

describe('IntakeDossierView Component', () => {
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

  it('renders confidential intake chamber title and initial form fields', () => {
    component = mount(IntakeDossierView, { target });

    expect(target.textContent).toContain('Confidential Intake Chamber');
    expect(target.textContent).toContain('Bespoke Commission Dossier');
    expect(target.textContent).toContain('Mutual NDA Status: Active & Sealed');
    expect(target.textContent).toContain('Commission Moniker');
    expect(target.textContent).toContain('Creative Premise & Vision');
    expect(target.textContent).toContain('Aesthetic Intent & Archetype');
    expect(target.textContent).toContain('Materiality Curation');
  });

  it('allows interactive selection of archetypes and materials', async () => {
    component = mount(IntakeDossierView, { target });

    const metallurgyBtn = target.querySelector('#archetype-btn-metallurgy') as HTMLButtonElement;
    expect(metallurgyBtn).not.toBeNull();
    expect(metallurgyBtn.getAttribute('aria-pressed')).toBe('false');

    metallurgyBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(metallurgyBtn.getAttribute('aria-pressed')).toBe('true');

    const vantaBtn = target.querySelector('#material-btn-vanta') as HTMLButtonElement;
    expect(vantaBtn).not.toBeNull();
    expect(vantaBtn.getAttribute('aria-pressed')).toBe('false');

    vantaBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(vantaBtn.getAttribute('aria-pressed')).toBe('true');
  });

  it('opens and closes the bilateral covenant modal', async () => {
    component = mount(IntakeDossierView, { target });

    expect(target.querySelector('#covenant-modal')).toBeNull();

    const inspectBtn = target.querySelector('#inspect-covenant-btn') as HTMLButtonElement;
    inspectBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#covenant-modal')).not.toBeNull();
    expect(target.textContent).toContain('This Non-Disclosure Covenant ("Covenant") governs all communications');

    const closeBtn = target.querySelector('#covenant-close-btn') as HTMLButtonElement;
    closeBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#covenant-modal')).toBeNull();
  });

  it('submits dossier and shows sealed confirmation with zero internal factory mechanics leak', async () => {
    component = mount(IntakeDossierView, { target });

    const form = target.querySelector('#intake-form') as HTMLFormElement;
    expect(form).not.toBeNull();

    const submitEvent = new Event('submit', { cancelable: true, bubbles: true });
    form.dispatchEvent(submitEvent);

    // Wait for timeout submission
    await new Promise(r => setTimeout(r, 700));

    expect(target.textContent).toContain('Dossier Sealed & Transmitted');
    expect(target.textContent).toContain('dispatched directly to your assigned principal liaison');

    // Strict Black Box check: no internal factory mechanics leaked
    const htmlText = target.innerHTML;
    expect(htmlText.includes('Jules')).toBe(false);
    expect(htmlText.includes('TOC')).toBe(false);
    expect(htmlText.includes('Poka-Yoke')).toBe(false);
    expect(htmlText.includes('SixSigma')).toBe(false);
    expect(htmlText.includes('Eneik')).toBe(false);
    expect(htmlText.includes('TaskPlan')).toBe(false);
    expect(htmlText.includes('AutoMerge')).toBe(false);
  });
});
