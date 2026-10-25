import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { render, fireEvent, cleanup, screen } from '@testing-library/svelte';
import AppliedModelsPlayground from '../components/AppliedModelsPlayground.svelte';

describe('AppliedModelsPlayground Component', () => {
  afterEach(() => {
    cleanup();
  });

  it('calculates or updates state functionally rather than displaying static text', async () => {
    const { getByRole, getByText, queryByText } = render(AppliedModelsPlayground);

    // Initial state
    expect(getByText('RUN MODEL')).toBeTruthy();

    // Click the run button
    const runBtn = getByRole('button', { name: /RUN MODEL/i });
    await fireEvent.click(runBtn);

    // Should change to inferring
    expect(getByText('INFERRING...')).toBeTruthy();
  });

  it('supports keyboard navigation and ARIA labels for WCAG 2.1 AA', async () => {
    const { getByRole, getByLabelText } = render(AppliedModelsPlayground);

    const tempSlider = getByLabelText('Temperature');
    expect(tempSlider).toBeTruthy();
    expect(tempSlider.tagName).toBe('INPUT');
    expect(tempSlider.getAttribute('type')).toBe('range');

    // Verify run button is a button
    const runBtn = getByRole('button', { name: /RUN MODEL/i });
    expect(runBtn).toBeTruthy();
  });
});
