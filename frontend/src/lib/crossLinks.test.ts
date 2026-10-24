// @vitest-environment jsdom
import { describe, it, expect, beforeEach, vi } from 'vitest';
import ContentCrossLinks from '../components/ContentCrossLinks.svelte';
import { mount, unmount } from 'svelte';

describe('ContentCrossLinks Component', () => {
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

  it('renders cross-links component with default incoming and outgoing references', () => {
    component = mount(ContentCrossLinks, {
      target,
      props: {
        nodeTitle: 'Leader Election & Term Logic',
        nodeType: 'concept',
        domain: 'AUTONOMOUS_ENGINEERING'
      }
    });

    expect(target.textContent).toContain('ГРАФ СВЯЗЕЙ');
    expect(target.textContent).toContain('Consensus Raft');
    expect(target.textContent).toContain('Raft Split Vote Recovery');
  });

  it('triggers onNavigate callback when link button is clicked', async () => {
    const onNavigateMock = vi.fn();
    component = mount(ContentCrossLinks, {
      target,
      props: {
        nodeTitle: 'Test Node',
        incomingReferences: [
          { sourceTitle: 'Target Node', sourceSlug: 'target-node', relationshipType: 'REFERENCES' }
        ],
        onNavigate: onNavigateMock
      }
    });

    const btns = Array.from(target.querySelectorAll('button'));
    const targetBtn = btns.find(b => b.textContent?.includes('Target Node'));
    expect(targetBtn).toBeTruthy();

    targetBtn?.click();
    await new Promise((r) => setTimeout(r, 20));

    expect(onNavigateMock).toHaveBeenCalledWith('target-node');
  });
});
