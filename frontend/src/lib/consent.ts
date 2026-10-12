import { writable } from 'svelte/store';

export type ConsentStatus = 'accepted' | 'declined' | null;

const STORAGE_KEY = 'cmp_consent_status';

function loadConsentStatus(): ConsentStatus {
  if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
    return null;
  }
  try {
    const val = localStorage.getItem(STORAGE_KEY);
    if (val === 'accepted' || val === 'declined') {
      return val;
    }
  } catch {
    // Fallback if localStorage is inaccessible
  }
  return null;
}

const statusStore = writable<ConsentStatus>(loadConsentStatus());

export const consentStore = {
  subscribe: statusStore.subscribe,
  setConsent: (status: 'accepted' | 'declined') => {
    if (typeof localStorage !== 'undefined') {
      try {
        localStorage.setItem(STORAGE_KEY, status);
      } catch {
        // Fallback
      }
    }
    statusStore.set(status);
  },
  resetConsent: () => {
    if (typeof localStorage !== 'undefined') {
      try {
        localStorage.removeItem(STORAGE_KEY);
      } catch {
        // Fallback
      }
    }
    statusStore.set(null);
  }
};

export function getConsentStatus(): ConsentStatus {
  return loadConsentStatus();
}

export function setConsentStatus(status: 'accepted' | 'declined'): void {
  consentStore.setConsent(status);
}

export function resetConsentStatus(): void {
  consentStore.resetConsent();
}

export function isTelemetryAllowed(): boolean {
  if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
    return false;
  }
  const status = getConsentStatus();
  // Telemetry is strictly gated and allowed ONLY if consent is explicitly 'accepted'.
  // Unconsented visitors (null) or revoked/declined consent ('declined') block tracking requests.
  return status === 'accepted';
}
