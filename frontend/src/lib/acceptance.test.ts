// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import { mount, unmount } from 'svelte';

describe('Acceptance Verification: Reachability & Seeded Content', () => {
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

  it('AC1: Given the public URL, When accessed, Then the site loads successfully with core branding', () => {
    component = mount(TelemetryHub, { target });

    // Verify main app root and header elements
    expect(target.querySelector('#app-root')).not.toBeNull();
    expect(target.querySelector('#main-header')).not.toBeNull();
    expect(target.querySelector('#sys-domain')?.textContent).toBe('dmitryefremov.com');
    expect(target.querySelector('#hero-title')?.textContent?.trim()).toBe('Дмитрий Ефремов');
    expect(target.querySelector('#hero-subtitle')?.textContent?.trim()).toBe('Системы, логика мышления и автономные процессы');
    expect(target.querySelector('#hero-manifest')?.textContent).toContain('«Ваш код и ваши решения — это то, кто вы есть на самом деле.»');
  });

  it('AC2: Given the site, When exploring, Then realistic seeded content is visible in every section', async () => {
    component = mount(TelemetryHub, { target });

    // 1. Verify Article Stream Section (Default active tab)
    const articleTab = target.querySelector('#tab-btn-article') as HTMLButtonElement;
    articleTab.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#article-stream-container')).not.toBeNull();
    expect(target.textContent).toContain('Deontic Logic in Autonomous Process Architecture');
    expect(target.textContent).toContain('Theory of Constraints in Distributed Pipeline Throughput');
    expect(target.textContent).toContain('Deterministic State Determinism in Autonomous Loops');

    // 2. Verify Podcast Media Section
    const podcastTab = target.querySelector('#tab-btn-podcast') as HTMLButtonElement;
    podcastTab.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#podcast-player-container')).not.toBeNull();
    expect(target.textContent).toContain('«Системы и реальность» Podcast');
    expect(target.textContent).toContain('TIMECODES & TOPICS');
    expect(target.textContent).toContain('Введение и контекст выпуска');

    // 3. Verify Contact Terminal Section
    const contactTab = target.querySelector('#tab-btn-contact') as HTMLButtonElement;
    contactTab.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#contact-terminal-root')).not.toBeNull();
    expect(target.textContent).toContain('Contact & Communications');
    expect(target.querySelector('#link-tg')).not.toBeNull();
    expect(target.querySelector('#link-yt')).not.toBeNull();
    expect(target.querySelector('#link-gh')).not.toBeNull();
    expect(target.querySelector('#link-li')).not.toBeNull();
    expect(target.querySelector('#link-em')).not.toBeNull();
  });
});
