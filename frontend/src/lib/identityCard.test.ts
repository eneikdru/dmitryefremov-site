// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import CoreSvelteIdentityCard from '../components/CoreSvelteIdentityCard.svelte';
import { mount, unmount } from 'svelte';

describe('CoreSvelteIdentityCard Component', () => {
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

  it('renders author identity header and channels correctly', () => {
    component = mount(CoreSvelteIdentityCard, { target });

    expect(target.textContent).toContain('Дмитрий Ефремов');
    expect(target.textContent).toContain('Systems Architect & Deontic Logic Researcher');
    expect(target.textContent).toContain('Telegram Channel');
    expect(target.textContent).toContain('YouTube Channel');
    expect(target.textContent).toContain('GitHub Repository');
  });

  it('provides visual feedback on copy button click', async () => {
    component = mount(CoreSvelteIdentityCard, { target });
    const copyBtn = target.querySelector('#copy-btn-tg') as HTMLButtonElement;

    expect(copyBtn).not.toBeNull();
    expect(copyBtn.textContent).toContain('COPY');

    copyBtn.click();
    await new Promise(r => setTimeout(r, 0));

    expect(copyBtn.textContent).toContain('COPIED ✓');
  });

  it('provides opening feedback on outbound channel click', async () => {
    component = mount(CoreSvelteIdentityCard, { target });
    const link = target.querySelector('#link-tg') as HTMLAnchorElement;

    expect(link).not.toBeNull();

    link.click();
    await new Promise(r => setTimeout(r, 0));

    expect(target.textContent).toContain('[OPENING...]');
  });
});
