import { writable } from 'svelte/store';

export type ConsentStatus = 'accepted' | 'declined' | null;

export interface CategoryPreferences {
  strictlyNecessary: boolean;
  analytics: boolean;
  functional: boolean;
  advertising: boolean;
}

const STORAGE_KEY = 'cmp_consent_status';
const CATEGORY_STORAGE_KEY = 'cmp_category_preferences';

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

export function getCategoryPreferences(): CategoryPreferences {
  const defaultPrefs: CategoryPreferences = {
    strictlyNecessary: true,
    analytics: true,
    functional: true,
    advertising: false
  };
  if (typeof window === 'undefined' || typeof localStorage === 'undefined') {
    return defaultPrefs;
  }
  try {
    const raw = localStorage.getItem(CATEGORY_STORAGE_KEY);
    if (raw) {
      const parsed = JSON.parse(raw);
      return {
        strictlyNecessary: true,
        analytics: Boolean(parsed.analytics),
        functional: Boolean(parsed.functional),
        advertising: Boolean(parsed.advertising)
      };
    }
  } catch {
    // Fallback
  }
  return defaultPrefs;
}

export function setCategoryPreferences(prefs: CategoryPreferences): void {
  if (typeof localStorage !== 'undefined') {
    try {
      localStorage.setItem(CATEGORY_STORAGE_KEY, JSON.stringify(prefs));
    } catch {
      // Fallback
    }
  }
  if (prefs.analytics) {
    setConsentStatus('accepted');
  } else {
    setConsentStatus('declined');
  }
}
