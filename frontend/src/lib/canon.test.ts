// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import { mount, unmount, tick } from 'svelte';
import {
  fetchCanonItems,
  fetchCanonItemBySlug,
  SEEDED_CANON_ITEMS,
  CANON_DOMAINS
} from './canon';
import CanonReaderView from '../components/CanonReaderView.svelte';

describe('Canon Data & API Client (canon.ts)', () => {
  it('fetches seeded canon items when API is not present', async () => {
    const page = await fetchCanonItems();
    expect(page.content.length).toBeGreaterThan(0);
    expect(page.totalElements).toBe(SEEDED_CANON_ITEMS.length);
  });

  it('filters canon items by domain accurately', async () => {
    const page = await fetchCanonItems('AUTONOMOUS_ENGINEERING');
    expect(page.content.length).toBeGreaterThan(0);
    expect(page.content.every(i => i.domain === 'AUTONOMOUS_ENGINEERING')).toBe(true);
  });

  it('filters canon items by type accurately', async () => {
    const page = await fetchCanonItems(undefined, 'MANIFESTO');
    expect(page.content.length).toBeGreaterThan(0);
    expect(page.content.every(i => i.type === 'MANIFESTO')).toBe(true);
  });

  it('retrieves single canon item by slug including cross-references', async () => {
    const item = await fetchCanonItemBySlug('autonomy-manifesto');
    expect(item).not.toBeNull();
    expect(item?.slug).toBe('autonomy-manifesto');
    expect(item?.outgoingReferences.length).toBeGreaterThan(0);
    expect(item?.outgoingReferences[0].targetSlug).toBe('deterministic-execution');
  });

  it('returns null for unknown slug', async () => {
    const item = await fetchCanonItemBySlug('non-existent-slug');
    expect(item).toBeNull();
  });
});

describe('CanonReaderView Component', () => {
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

  it('renders top header, domain navigation tabs, and default manifesto', async () => {
    component = mount(CanonReaderView, { target });
    await tick();

    expect(target.textContent).toContain('The Canon (Канон Автономии)');
    expect(target.textContent).toContain('[TIER_1_CANON]');
    expect(target.textContent).toContain('Manifesto for Autonomous Process Architecture');
    expect(target.textContent).toContain('WCAG 2.1 AA Compliant');

    for (const dom of CANON_DOMAINS) {
      expect(target.textContent).toContain(dom.label);
    }
  });

  it('filters canon catalog when domain navigation tab is clicked', async () => {
    component = mount(CanonReaderView, { target });
    await tick();

    const btnAgentic = target.querySelector('#domain-tab-agentic_economy') as HTMLButtonElement;
    expect(btnAgentic).not.toBeNull();

    btnAgentic.click();
    await tick();
    await new Promise(r => setTimeout(r, 20));

    expect(target.textContent).toContain('Manifesto for Agent-to-Agent Economic Protocols');
    expect(target.textContent).not.toContain('Theory of Constraints Bottleneck Elimination');
  });

  it('filters catalog items when search query is entered', async () => {
    component = mount(CanonReaderView, { target });
    await tick();

    const searchInput = target.querySelector('#canon-search-input') as HTMLInputElement;
    expect(searchInput).not.toBeNull();

    searchInput.value = 'Extended Mind';
    searchInput.dispatchEvent(new Event('input'));
    await tick();
    await new Promise(r => setTimeout(r, 20));

    expect(target.textContent).toContain('Manifesto for Extended Mind & Cognitive Scaffolding');
    expect(target.textContent).not.toContain('Autonomous Machine Settlement');
  });

  it('switches active manifesto when item from catalog list is clicked', async () => {
    component = mount(CanonReaderView, { target });
    await tick();
    await new Promise(r => setTimeout(r, 20));

    const itemBtn = target.querySelector('#canon-item-btn-deterministic-execution') as HTMLButtonElement;
    expect(itemBtn).not.toBeNull();

    itemBtn.click();
    await tick();
    await new Promise(r => setTimeout(r, 20));

    const articleTitle = target.querySelector('#canon-article-title');
    expect(articleTitle?.textContent).toBe('Deterministic State Machines');
  });

  it('navigates to target canon item when cross-reference deep link is clicked', async () => {
    component = mount(CanonReaderView, { target, props: { initialSlug: 'autonomy-manifesto' } });
    await tick();
    await new Promise(r => setTimeout(r, 20));

    const xrefBtn = target.querySelector('#xref-out-deterministic-execution') as HTMLButtonElement;
    expect(xrefBtn).not.toBeNull();

    xrefBtn.click();
    await tick();
    await new Promise(r => setTimeout(r, 20));

    const articleTitle = target.querySelector('#canon-article-title');
    expect(articleTitle?.textContent).toBe('Deterministic State Machines');
  });

  it('complies with WCAG 2.1 AA accessibility guidelines (form label, focusable controls, semantic elements)', async () => {
    component = mount(CanonReaderView, { target });
    await tick();

    const searchLabel = target.querySelector('label[for="canon-search-input"]');
    expect(searchLabel).not.toBeNull();

    const articleTag = target.querySelector('article#canon-reader-article');
    expect(articleTag).not.toBeNull();

    const buttons = target.querySelectorAll('button');
    expect(buttons.length).toBeGreaterThan(5);
    buttons.forEach(btn => {
      expect(btn.getAttribute('type')).toBe('button');
    });
  });
});
