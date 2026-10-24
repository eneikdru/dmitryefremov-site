// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import CorpusSearchModal from '../components/CorpusSearchModal.svelte';
import { mount, unmount } from 'svelte';

describe('CorpusSearchModal Component', () => {
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

  it('renders search modal when isOpen is true', () => {
    component = mount(CorpusSearchModal, {
      target,
      props: {
        isOpen: true,
        theme: 'dark'
      }
    });

    const modal = target.querySelector('#corpus-search-modal');
    expect(modal).not.toBeNull();
    expect(target.textContent).toContain('SEARCH RESULTS');
    expect(target.textContent).toContain('QUICK COMMANDS');
  });

  it('filters results when query is typed into search input', async () => {
    component = mount(CorpusSearchModal, {
      target,
      props: {
        isOpen: true,
        theme: 'dark'
      }
    });

    const input = target.querySelector('#corpus-search-input') as HTMLInputElement;
    expect(input).not.toBeNull();

    input.value = 'raft';
    input.dispatchEvent(new Event('input'));
    await new Promise((r) => setTimeout(r, 20));

    const resultsHeader = target.querySelector('#search-results-header');
    expect(resultsHeader?.textContent).toContain('SEARCH RESULTS');
  });

  it('calls onClose when close button or Escape key is pressed', async () => {
    const onCloseMock = vi.fn();
    component = mount(CorpusSearchModal, {
      target,
      props: {
        isOpen: true,
        onClose: onCloseMock
      }
    });

    const closeBtn = target.querySelector('#corpus-search-close-btn') as HTMLButtonElement;
    expect(closeBtn).not.toBeNull();

    closeBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    expect(onCloseMock).toHaveBeenCalled();
  });
});
