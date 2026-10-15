// @vitest-environment jsdom
import { describe, it, expect, beforeEach } from 'vitest';
import SwissEditorialView from '../components/SwissEditorialView.svelte';
import { mount, unmount } from 'svelte';

describe('SwissEditorialView Component', () => {
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

  it('renders black-box security console header and status elements', () => {
    component = mount(SwissEditorialView, { target });

    expect(target.textContent).toContain('ENCLAVE://SEC-0');
    expect(target.textContent).toContain('NETWORK: SEVERED [AIR-GAP: ACTIVE]');
    expect(target.textContent).toContain('BLACK-BOX INVARIANT STATUS: VERIFIED');
    expect(target.textContent).toContain('TELEMETRY QUARANTINE MONITOR');
    expect(target.textContent).toContain('TACTICAL CONTROLS // DESTRUCTIVE & ROTATION');
    expect(target.textContent).toContain('ENCLAVE_KERNEL_AUDIT_LOG');
  });

  it('handles kill beacon action correctly', async () => {
    component = mount(SwissEditorialView, { target });

    const killBtn = target.querySelector('#killBtn') as HTMLButtonElement;
    expect(killBtn).not.toBeNull();
    expect(killBtn.textContent?.trim()).toBe('KILL_BEACON');

    killBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    expect(killBtn.textContent?.trim()).toBe('BEACON_DEAD');
    const logStream = target.querySelector('#logStream') as HTMLElement;
    expect(logStream.textContent).toContain('CMD_RECEIVED: KILL_BEACON. All RF carrier signals truncated to ground.');
  });

  it('executes tactical buffer purge, key rotation, and ID scrambling', async () => {
    component = mount(SwissEditorialView, { target });

    const purgeBtn = target.querySelector('#btnPurgeBuffer') as HTMLButtonElement;
    const rotateBtn = target.querySelector('#btnRotateKey') as HTMLButtonElement;
    const scrambleBtn = target.querySelector('#btnScrambleId') as HTMLButtonElement;

    expect(purgeBtn).not.toBeNull();
    expect(rotateBtn).not.toBeNull();
    expect(scrambleBtn).not.toBeNull();

    purgeBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    rotateBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    scrambleBtn.click();
    await new Promise((r) => setTimeout(r, 20));

    const logStream = target.querySelector('#logStream') as HTMLElement;
    expect(logStream.textContent).toContain('SYS_ACTION: Local volatility ring cleared');
    expect(logStream.textContent).toContain('TRNG_REKEY: Hardware entropy drawn');
    expect(logStream.textContent).toContain('MASK_GEN: Enclave public measurement rotated');
  });

  it('toggles data bus gating mode', async () => {
    component = mount(SwissEditorialView, { target });

    const btnRoute = target.querySelector('#btnGatingRoute') as HTMLButtonElement;
    const btnIsolate = target.querySelector('#btnGatingIsolate') as HTMLButtonElement;

    expect(btnRoute).not.toBeNull();

    btnRoute.click();
    await new Promise((r) => setTimeout(r, 20));

    const logStream = target.querySelector('#logStream') as HTMLElement;
    expect(logStream.textContent).toContain('DATA_BUS_MODE: Gating switch toggled to [ ROUTE ].');

    btnIsolate.click();
    await new Promise((r) => setTimeout(r, 20));

    expect(logStream.textContent).toContain('DATA_BUS_MODE: Gating switch toggled to [ ISOLATE ].');
  });

  it('switches bottom nav tabs', async () => {
    component = mount(SwissEditorialView, { target });

    const memoryTab = target.querySelector('#nav-tab-memory') as HTMLButtonElement;
    const keysTab = target.querySelector('#nav-tab-keys') as HTMLButtonElement;

    expect(memoryTab).not.toBeNull();
    expect(keysTab).not.toBeNull();

    memoryTab.click();
    await new Promise((r) => setTimeout(r, 20));

    expect(memoryTab.classList.contains('border-t-2')).toBe(true);

    keysTab.click();
    await new Promise((r) => setTimeout(r, 20));

    expect(keysTab.classList.contains('border-t-2')).toBe(true);
  });
});
