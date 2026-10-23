// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import { mount, unmount } from 'svelte';
import {
  CANON_MANIFESTOS,
  getManifestosByDomain,
  formatTimeSeconds
} from './canon';
import CanonReaderView from '../components/CanonReaderView.svelte';
import TelemetryHub from '../components/TelemetryHub.svelte';

describe('Canon Data Model & Helper Functions', () => {
  it('contains manifestos for all 4 practical domains', () => {
    const domains = [
      'Autonomous Engineering',
      'Agentic Economy (A2A)',
      'Pragmatic Business Strategy',
      'Philosophy of Mind'
    ] as const;

    for (const domain of domains) {
      const manifestos = getManifestosByDomain(domain);
      expect(manifestos.length).toBeGreaterThan(0);
      expect(manifestos[0].domain).toBe(domain);
    }
  });

  it('formats audio narration time correctly', () => {
    expect(formatTimeSeconds(252)).toBe('04:12');
    expect(formatTimeSeconds(0)).toBe('00:00');
    expect(formatTimeSeconds(60)).toBe('01:00');
    expect(formatTimeSeconds(1080)).toBe('18:00');
  });

  it('contains valid chapter frontispieces, epigraphs, drop caps, and annotations', () => {
    const manifesto = CANON_MANIFESTOS[0];
    expect(manifesto.chapterNumber).toBe('CHAPTER IV');
    expect(manifesto.chapterTitle).toBe('On the Soul & Transitory Nature');
    expect(manifesto.epigraph.quote).toContain('Omnia fluxit');
    expect(manifesto.sections[0].dropCapText).toBeDefined();
    expect(manifesto.sections[0].highlightedPassage?.scholium?.title).toBe('SCHOLIA · THE INNER CITADEL');
    expect(manifesto.sections[1].annotation?.term).toBe('Hēgemonikon');
  });
});

describe('CanonReaderView Svelte Component', () => {
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

  it('renders Canon Reader header, title, and classical epigraph', () => {
    component = mount(CanonReaderView, { target });

    expect(target.textContent).toContain('THE CANON');
    expect(target.textContent).toContain('CHAPTER IV: ON THE SOUL & TRANSITORY NATURE');
    expect(target.textContent).toContain('Omnia fluxit: nihil in perpetuum manet');
  });

  it('allows domain switching between the 4 practical domains', async () => {
    component = mount(CanonReaderView, { target });

    const domainTabs = target.querySelectorAll('#canon-domain-nav button[role="tab"]');
    expect(domainTabs.length).toBe(4);

    // Switch to Autonomous Engineering domain
    const engineeringBtn = Array.from(domainTabs).find(
      btn => btn.textContent?.includes('Autonomous Engineering')
    ) as HTMLButtonElement;
    expect(engineeringBtn).not.toBeNull();

    engineeringBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('SUPERVENIENCE OF STATE SPACE');
    expect(target.textContent).toContain('CHAPTER I: DETERMINISM & FORMAL INVARIANTS');
  });

  it('toggles audio narration play and pause states in floating HUD', async () => {
    component = mount(CanonReaderView, { target });

    const playBtn = target.querySelector('#canon-play-pause-btn') as HTMLButtonElement;
    expect(playBtn).not.toBeNull();
    expect(playBtn.getAttribute('aria-label')).toBe('Play audio narration');

    playBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(playBtn.getAttribute('aria-label')).toBe('Pause audio narration');

    playBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(playBtn.getAttribute('aria-label')).toBe('Play audio narration');
  });

  it('toggles terminology annotation card expansion', async () => {
    component = mount(CanonReaderView, { target });

    const toggleBtn = target.querySelector('#canon-annotation-toggle-btn') as HTMLButtonElement;
    expect(toggleBtn).not.toBeNull();
    expect(toggleBtn.getAttribute('aria-expanded')).toBe('true');
    expect(target.textContent).toContain('The Ruling Center');

    toggleBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(toggleBtn.getAttribute('aria-expanded')).toBe('false');

    toggleBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(toggleBtn.getAttribute('aria-expanded')).toBe('true');
  });

  it('integrates seamlessly with TelemetryHub when selecting canon tab', async () => {
    component = mount(TelemetryHub, { target, props: { initialTab: 'canon' } });

    expect(target.textContent).toContain('THE CANON');
    expect(target.textContent).toContain('CHAPTER IV: ON THE SOUL & TRANSITORY NATURE');
  });
});
