// @vitest-environment jsdom
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { mount, unmount } from 'svelte';
import TelemetryContactTerminalView from './TelemetryContactTerminalView.svelte';

describe('TelemetryContactTerminalView', () => {
  let container: HTMLElement;

  beforeEach(() => {
    container = document.createElement('div');
    document.body.appendChild(container);
    vi.restoreAllMocks();
  });

  afterEach(() => {
    document.body.innerHTML = '';
  });

  it('renders loading state when request is pending', () => {
    const app = mount(TelemetryContactTerminalView, {
      target: container,
      props: { autoFetch: false, loading: true, documents: [] }
    });

    const loadingEl = container.querySelector('#materials-loading-state');
    expect(loadingEl).not.toBeNull();
    expect(loadingEl?.textContent).toContain('Fetching materials from server');
    unmount(app);
  });

  it('renders distinct empty state when search endpoint returns 0 rows', () => {
    const app = mount(TelemetryContactTerminalView, {
      target: container,
      props: { autoFetch: false, loading: false, error: null, documents: [] }
    });

    const emptyEl = container.querySelector('#materials-empty-state');
    expect(emptyEl).not.toBeNull();
    expect(emptyEl?.textContent).toContain('No materials available from server');
    expect(container.querySelector('#materials-list')).toBeNull();
    unmount(app);
  });

  it('renders clear error state when request fails', () => {
    const app = mount(TelemetryContactTerminalView, {
      target: container,
      props: { autoFetch: false, loading: false, error: 'Connection refused', documents: [] }
    });

    const errorEl = container.querySelector('#materials-error-state');
    expect(errorEl).not.toBeNull();
    expect(errorEl?.textContent).toContain('[SYS_ERROR] Request Failed');
    expect(errorEl?.textContent).toContain('Connection refused');
    unmount(app);
  });

  it('renders document list with title, author, date, and download button when documents exist', () => {
    const sampleDocs = [
      {
        id: 'doc-1',
        title: 'Deontic Specification Standard',
        author: 'Dmitry Efremov',
        date: '2026-10-09',
        downloadUrl: '/files/spec.pdf'
      }
    ];

    const app = mount(TelemetryContactTerminalView, {
      target: container,
      props: { autoFetch: false, loading: false, error: null, documents: sampleDocs }
    });

    const docEl = container.querySelector('#doc-doc-1');
    expect(docEl).not.toBeNull();
    expect(docEl?.textContent).toContain('Deontic Specification Standard');
    expect(docEl?.textContent).toContain('Author: Dmitry Efremov');
    expect(docEl?.textContent).toContain('Date: 2026-10-09');

    const downloadBtn = container.querySelector('#download-btn-doc-1') as HTMLAnchorElement;
    expect(downloadBtn).not.toBeNull();
    expect(downloadBtn.getAttribute('href')).toBe('/files/spec.pdf');
    expect(downloadBtn.textContent?.trim()).toBe('Download');

    unmount(app);
  });

  it('fetches documents from endpoint on mount', async () => {
    const mockData = [
      { id: '101', title: 'Fetched Spec', author: 'Efremov', date: '2026-10-01', downloadUrl: 'http://example.com/101.pdf' }
    ];

    globalThis.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => mockData
    } as Response);

    const app = mount(TelemetryContactTerminalView, {
      target: container,
      props: { endpointUrl: '/api/v1/documents', autoFetch: true }
    });

    // Wait for fetch async resolution
    await new Promise((r) => setTimeout(r, 50));

    expect(globalThis.fetch).toHaveBeenCalledWith('/api/v1/documents');
    const docEl = container.querySelector('#doc-101');
    expect(docEl).not.toBeNull();
    expect(docEl?.textContent).toContain('Fetched Spec');

    unmount(app);
  });
});
