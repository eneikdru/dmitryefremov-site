import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/svelte';
import ModelOpsPlaygroundView from '../components/ModelOpsPlaygroundView.svelte';

describe('ModelOpsPlaygroundView Component', () => {
  let container: HTMLElement;

  beforeEach(() => {
    const rendered = render(ModelOpsPlaygroundView);
    container = rendered.container;
  });

  afterEach(() => {
    container.remove();
  });

  it('renders hyperparameter controls and updates state', async () => {
    // Initial max tokens is 512
    const tokensVal = container.querySelector('#tokens-val');
    expect(tokensVal?.textContent).toBe('512');

    // Drawer should have attributes correctly toggled
    const drawerBtn = screen.getByRole('button', { name: /Hyperparameters & Inference Controls/i });
    expect(drawerBtn.getAttribute('aria-expanded')).toBe('true');

    await fireEvent.click(drawerBtn);
    expect(drawerBtn.getAttribute('aria-expanded')).toBe('false');
    await fireEvent.click(drawerBtn); // toggle back
  });

  it('switches between tabs and sets correct ARIA states', async () => {
    const jsonTab = container.querySelector('#tab-json') as HTMLElement;
    const tokensTab = container.querySelector('#tab-tokens') as HTMLElement;

    // Initial state
    expect(jsonTab.getAttribute('aria-selected')).toBe('true');
    expect(tokensTab.getAttribute('aria-selected')).toBe('false');

    // Click on Tokens tab
    await fireEvent.click(tokensTab);
    expect(jsonTab.getAttribute('aria-selected')).toBe('false');
    expect(tokensTab.getAttribute('aria-selected')).toBe('true');

    // Tab content visibility checks
    const viewJson = container.querySelector('#tab-view-json');
    const viewTokens = container.querySelector('#tab-view-tokens');
    expect(viewJson?.classList.contains('hidden')).toBe(true);
    expect(viewTokens?.classList.contains('hidden')).toBe(false);
  });

  it('updates model state when pill is clicked', async () => {
    const pills = container.querySelectorAll('.model-pill') as NodeListOf<HTMLElement>;
    const textExtractPill = pills[1]; // Text-Extract-XL is second
    const audioTranscribePill = pills[2]; // Audio-Transcribe is third

    expect(textExtractPill.getAttribute('aria-pressed')).toBe('true');
    expect(audioTranscribePill.getAttribute('aria-pressed')).toBe('false');

    await fireEvent.click(audioTranscribePill);
    expect(textExtractPill.getAttribute('aria-pressed')).toBe('false');
    expect(audioTranscribePill.getAttribute('aria-pressed')).toBe('true');

    // Meta bar should update
    const modelProvider = container.querySelector('#model-provider');
    const modelArch = container.querySelector('#model-arch');
    expect(modelProvider?.textContent).toBe('AWS Inferentia');
    expect(modelArch?.textContent).toBe('Whisper-L');
  });
});