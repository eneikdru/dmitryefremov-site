// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';

describe('TelemetryHub Component', () => {
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

  it('renders hero title, subtitle, and manifest correctly', () => {
    component = mount(TelemetryHub, { target });

    expect(target.textContent).toContain('Дмитрий Ефремов');
    expect(target.textContent).toContain('Системы, логика мышления и автономные процессы');
    expect(target.textContent).toContain('«Ваш код и ваши решения — это то, кто вы есть на самом деле.»');
  });

  it('switches palette theme when theme toggle button is clicked', async () => {
    component = mount(TelemetryHub, { target });
    const themeBtn = target.querySelector('#theme-toggle-btn') as HTMLButtonElement;
    const themeRoot = target.querySelector('#theme-root') as HTMLElement;

    expect(themeRoot.classList.contains('dark-void')).toBe(true);

    themeBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(themeRoot.classList.contains('light-monochrome')).toBe(true);

    themeBtn.click();
    await new Promise(r => setTimeout(r, 0));
    expect(themeRoot.classList.contains('dark-void')).toBe(true);
  });

  it('navigates seamlessly between Canon Reader, Article Stream, and Podcast Hub', async () => {
    component = mount(TelemetryHub, { target });

    // Initial default tab: Canon Reader
    expect(target.textContent).toContain('The Canon (Evergreen Manifestos & Ontology)');
    expect(target.textContent).toContain('Манифест суверенности автономных систем и процессов');

    // Navigate to Article Stream tab
    const articleTabBtn = target.querySelector('#tab-btn-article') as HTMLButtonElement;
    expect(articleTabBtn).not.toBeNull();
    articleTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('Articles & Thought Stream');
    expect(target.textContent).toContain('Эстетика инженерного молчания и редакционный минимализм');

    // Navigate to Podcast Media tab
    const podcastTabBtn = target.querySelector('#tab-btn-podcast') as HTMLButtonElement;
    expect(podcastTabBtn).not.toBeNull();
    podcastTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('«Системы и реальность» Podcast');
    expect(target.textContent).toContain('TIMECODES & TOPICS');

    // Navigate back to Canon Reader tab
    const canonTabBtn = target.querySelector('#tab-btn-canon') as HTMLButtonElement;
    expect(canonTabBtn).not.toBeNull();
    canonTabBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('The Canon (Evergreen Manifestos & Ontology)');
  });

  it('opens and closes Imprint legal modal when clicking footer Imprint trigger', async () => {
    component = mount(TelemetryHub, { target });

    expect(target.querySelector('#imprint-modal')).toBeNull();

    const imprintToggle = target.querySelector('#imprint-toggle-btn') as HTMLButtonElement;
    imprintToggle.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#imprint-modal')).not.toBeNull();
    expect(target.textContent).toContain('Site Operator:');

    const closeBtn = target.querySelector('#imprint-close-btn') as HTMLButtonElement;
    closeBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#imprint-modal')).toBeNull();
  });
});
