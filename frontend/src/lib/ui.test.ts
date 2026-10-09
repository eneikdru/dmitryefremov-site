// @vitest-environment jsdom
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import TelemetryHub from '../components/TelemetryHub.svelte';
import TelemetryContactTerminalView from '../components/TelemetryContactTerminalView.svelte';
import { mount, unmount } from 'svelte';

describe('TelemetryHub Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
  });

  afterEach(() => {
    if (component) {
      unmount(component);
      component = null;
    }
    if (target && target.parentNode) {
      document.body.removeChild(target);
    }
    vi.restoreAllMocks();
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

describe('TelemetryContactTerminalView Component', () => {
  let target: HTMLElement;
  let component: any;

  beforeEach(() => {
    target = document.createElement('div');
    document.body.appendChild(target);
  });

  afterEach(() => {
    if (component) {
      unmount(component);
      component = null;
    }
    if (target && target.parentNode) {
      document.body.removeChild(target);
    }
    vi.restoreAllMocks();
  });

  it('displays loading state while data request is pending', async () => {
    vi.spyOn(globalThis, 'fetch').mockImplementation(() => new Promise(() => {}));

    component = mount(TelemetryContactTerminalView, { target });
    await new Promise(r => setTimeout(r, 0));

    expect(target.querySelector('#materials-loading')).not.toBeNull();
    expect(target.textContent).toContain('Loading materials from server...');
  });

  it('displays distinct empty state when search endpoint returns 0 items', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: async () => []
    } as Response);

    component = mount(TelemetryContactTerminalView, { target });
    await new Promise(r => setTimeout(r, 20));

    expect(target.querySelector('#materials-empty')).not.toBeNull();
    expect(target.textContent).toContain('Showing 0 materials');
    expect(target.textContent).toContain('Catalogue is empty');
    expect(target.querySelector('#materials-list')).toBeNull();
  });

  it('displays error message when request fails or backend is stopped', async () => {
    vi.spyOn(globalThis, 'fetch').mockRejectedValue(new Error('Network error: backend unreachable'));

    component = mount(TelemetryContactTerminalView, { target });
    await new Promise(r => setTimeout(r, 20));

    expect(target.querySelector('#materials-error')).not.toBeNull();
    expect(target.textContent).toContain('[SERVER_ERROR]');
    expect(target.textContent).toContain('Network error: backend unreachable');
  });

  it('renders documents with title, author, date, and download button when items returned', async () => {
    const mockDocs = [
      {
        id: 'doc-101',
        title: 'Deontic Constraints in Distributed Systems',
        author: 'Dmitry Efremov',
        date: '2026-10-09',
        downloadUrl: '/files/deontic-constraints.pdf'
      }
    ];

    vi.spyOn(globalThis, 'fetch').mockResolvedValue({
      ok: true,
      json: async () => ({ items: mockDocs, total: 1 })
    } as Response);

    component = mount(TelemetryContactTerminalView, { target });
    await new Promise(r => setTimeout(r, 20));

    expect(target.querySelector('#materials-list')).not.toBeNull();
    expect(target.textContent).toContain('Showing 1-1 of 1 materials');
    expect(target.textContent).toContain('Deontic Constraints in Distributed Systems');
    expect(target.textContent).toContain('Author: Dmitry Efremov');
    expect(target.textContent).toContain('Date: 2026-10-09');

    const downloadBtn = target.querySelector('a[download]') as HTMLAnchorElement;
    expect(downloadBtn).not.toBeNull();
    expect(downloadBtn.getAttribute('href')).toBe('/files/deontic-constraints.pdf');
    expect(downloadBtn.textContent?.trim()).toBe('Download');
  });
});
