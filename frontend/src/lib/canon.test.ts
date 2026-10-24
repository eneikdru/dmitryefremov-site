// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import {
  DEFAULT_CANON_ITEMS,
  DOMAIN_LABELS,
  TYPE_LABELS,
  filterLocalCanonItems,
  fetchCanonItems
} from './canon';
import CanonView from '../components/CanonView.svelte';
import { mount, unmount } from 'svelte';

describe('Canon Data Model & Helper Logic', () => {
  it('contains default Canon items covering all 4 practical domains', () => {
    expect(DEFAULT_CANON_ITEMS.length).toBeGreaterThanOrEqual(5);

    const domainsInCanon = new Set(DEFAULT_CANON_ITEMS.map((item) => item.domain));
    expect(domainsInCanon.has('AUTONOMOUS_ENGINEERING')).toBe(true);
    expect(domainsInCanon.has('AGENTIC_ECONOMY')).toBe(true);
    expect(domainsInCanon.has('PRAGMATIC_BUSINESS_STRATEGY')).toBe(true);
    expect(domainsInCanon.has('PHILOSOPHY_OF_MIND')).toBe(true);
  });

  it('filters local Canon items by domain', () => {
    const autoEngItems = filterLocalCanonItems('AUTONOMOUS_ENGINEERING');
    expect(autoEngItems.length).toBeGreaterThan(0);
    expect(autoEngItems.every((item) => item.domain === 'AUTONOMOUS_ENGINEERING')).toBe(true);

    const agenticEconItems = filterLocalCanonItems('AGENTIC_ECONOMY');
    expect(agenticEconItems.length).toBeGreaterThan(0);
    expect(agenticEconItems.every((item) => item.domain === 'AGENTIC_ECONOMY')).toBe(true);
  });

  it('filters local Canon items by type', () => {
    const manifestos = filterLocalCanonItems(undefined, 'MANIFESTO');
    expect(manifestos.length).toBeGreaterThan(0);
    expect(manifestos.every((item) => item.type === 'MANIFESTO')).toBe(true);

    const glossaryTerms = filterLocalCanonItems(undefined, 'GLOSSARY_TERM');
    expect(glossaryTerms.length).toBeGreaterThan(0);
    expect(glossaryTerms.every((item) => item.type === 'GLOSSARY_TERM')).toBe(true);
  });

  it('falls back to default Canon items if API call fails or is unreachable', async () => {
    const items = await fetchCanonItems();
    expect(items.length).toBeGreaterThan(0);
    expect(items[0].slug).toBe('architecture-of-autonomous-systems');
  });
});

describe('CanonView Svelte Component UI & WCAG Operability', () => {
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

  it('renders Canon header, domain filter buttons, and manifesto reader', () => {
    component = mount(CanonView, { target });

    expect(target.textContent).toContain('The Canon: Autonomous Systems & Extended Mind');
    expect(target.textContent).toContain('Autonomous Engineering');
    expect(target.textContent).toContain('Agentic Economy (A2A)');
    expect(target.textContent).toContain('Pragmatic Business Strategy');
    expect(target.textContent).toContain('Philosophy of Mind');
    expect(target.textContent).toContain('The Architecture of Autonomous Systems');
  });

  it('filters items when domain button is clicked', async () => {
    component = mount(CanonView, { target });

    const agenticEconBtn = target.querySelector('#canon-filter-domain-agentic-econ') as HTMLButtonElement;
    expect(agenticEconBtn).not.toBeNull();

    agenticEconBtn.click();
    await new Promise((r) => setTimeout(r, 10));

    expect(agenticEconBtn.getAttribute('aria-pressed')).toBe('true');
    expect(target.textContent).toContain('Agent-to-Agent Economic Protocols');
  });

  it('navigates to referenced Canon item when cross-reference link is clicked', async () => {
    component = mount(CanonView, { target });

    const xrefSection = target.querySelector('#canon-cross-references');
    expect(xrefSection).not.toBeNull();

    const xrefBtn = xrefSection?.querySelector('button') as HTMLButtonElement;
    expect(xrefBtn).not.toBeNull();

    xrefBtn.click();
    await new Promise((r) => setTimeout(r, 10));

    expect(target.textContent).toContain('Extended Mind Infrastructure');
  });

  it('complies with WCAG 2.1 AA ARIA roles and keyboard focus attributes', () => {
    component = mount(CanonView, { target });

    const toolbars = target.querySelectorAll('[role="toolbar"]');
    expect(toolbars.length).toBeGreaterThanOrEqual(2);

    const activeArticle = target.querySelector('#canon-reader-article');
    expect(activeArticle).not.toBeNull();

    const buttons = target.querySelectorAll('button');
    buttons.forEach((btn) => {
      expect(btn.classList.toString()).toContain('focus:ring-2');
    });
  });
});
