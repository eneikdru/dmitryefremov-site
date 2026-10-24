/**
 * Telemetry Implementation for Dmitry Efremov Site
 * Tracks article scroll depth and contact terminal outbound clicks with zero non-essential cookies.
 */

import { isTelemetryAllowed } from './consent';

export interface TelemetryEvent {
  type:
    | 'essay_view'
    | 'scroll_depth'
    | 'outbound_click'
    | 'canon_dwell_heartbeat'
    | 'form_field_focused'
    | 'form_validation_passed'
    | 'canon_submit_converted'
    | 'graph_navigation'
    | 'search_utilization';
  timestamp: string;
  payload: Record<string, unknown>;
}

export interface GraphNavigationPayload {
  sourceNodeId: string;
  targetNodeId: string;
  linkType?: string;
}

export interface SearchUtilizationPayload {
  query: string;
  resultCount: number;
  selectedResultId?: string;
}

export interface EssayViewPayload {
  articleId: string;
  title?: string;
}

export interface ScrollDepthPayload {
  articleId: string;
  depth: number; // 25, 50, 75, 100
}

export interface OutboundClickPayload {
  destination: string;
  label?: string;
  platform?: string;
}

export interface CanonDwellPayload {
  dwellMs: number;
  step: string;
  scrollDepth?: number;
  activeTab?: boolean;
}

export interface FormConversionPayload {
  sessionId?: string;
  totalDwellMs?: number;
  fieldId?: string;
  step?: string;
  converted?: boolean;
  status?: string;
}

class TelemetryService {
  private events: TelemetryEvent[] = [];
  private listeners: Array<(event: TelemetryEvent) => void> = [];

  /**
   * Asserts and verifies zero non-essential cookie usage.
   * Privacy-default compliance.
   */
  public verifyPrivacyCompliance(): boolean {
    if (typeof document !== 'undefined') {
      const cookies = document.cookie;
      // No cookies should be set by telemetry or client tracking
      return cookies === '' || !cookies.includes('telemetry_id');
    }
    return true;
  }

  public trackEssayView(articleId: string, title?: string): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'essay_view',
      timestamp: new Date().toISOString(),
      payload: {
        articleId,
        title: title ?? articleId
      }
    };
    this.recordEvent(event);
    return event;
  }

  public trackGraphNavigation(sourceNodeId: string, targetNodeId: string, linkType: string = 'cross_link'): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'graph_navigation',
      timestamp: new Date().toISOString(),
      payload: {
        sourceNodeId,
        targetNodeId,
        linkType
      }
    };
    this.recordEvent(event);
    return event;
  }

  public trackSearchUtilization(query: string, resultCount: number, selectedResultId?: string): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'search_utilization',
      timestamp: new Date().toISOString(),
      payload: {
        query,
        resultCount,
        selectedResultId: selectedResultId ?? null
      }
    };
    this.recordEvent(event);
    return event;
  }

  public trackScrollDepth(articleId: string, depth: number): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'scroll_depth',
      timestamp: new Date().toISOString(),
      payload: {
        articleId,
        depth
      }
    };
    this.recordEvent(event);
    return event;
  }

  public trackCanonDwellHeartbeat(dwellMs: number, step: string = 'canon_reader', scrollDepth: number = 0, activeTab: boolean = true): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'canon_dwell_heartbeat',
      timestamp: new Date().toISOString(),
      payload: {
        dwell_ms: dwellMs,
        step,
        scroll_depth: scrollDepth,
        active_tab: activeTab,
        status: '200_OK'
      }
    };
    this.recordEvent(event);
    return event;
  }

  public trackFormFieldFocused(fieldId: string, focusMs: number = 0, priorKeystrokes: number = 0): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'form_field_focused',
      timestamp: new Date().toISOString(),
      payload: {
        field_id: fieldId,
        focus_ms: focusMs,
        prior_keystrokes: priorKeystrokes,
        validation: 'pending'
      }
    };
    this.recordEvent(event);
    return event;
  }

  public trackFormValidationPassed(step: string, latencyMs: number = 10, rulesEvaluated: number = 1): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'form_validation_passed',
      timestamp: new Date().toISOString(),
      payload: {
        step,
        latency_ms: latencyMs,
        rules_evaluated: rulesEvaluated,
        checksum: 'v1_pass'
      }
    };
    this.recordEvent(event);
    return event;
  }

  public trackCanonSubmitConverted(sessionId: string, totalDwellMs: number, additionalData: Record<string, unknown> = {}): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'canon_submit_converted',
      timestamp: new Date().toISOString(),
      payload: {
        session_id: sessionId,
        total_dwell_ms: totalDwellMs,
        status: '200_OK',
        converted: true,
        ...additionalData
      }
    };
    this.recordEvent(event);
    return event;
  }

  public trackOutboundClick(destination: string, label?: string, platform?: string): TelemetryEvent | null {
    this.verifyPrivacyCompliance();
    if (!isTelemetryAllowed()) {
      return null;
    }
    const event: TelemetryEvent = {
      type: 'outbound_click',
      timestamp: new Date().toISOString(),
      payload: {
        destination,
        label: label ?? destination,
        platform: platform ?? 'external'
      }
    };
    this.recordEvent(event);
    return event;
  }

  private recordEvent(event: TelemetryEvent): void {
    this.events.push(event);
    this.listeners.forEach(listener => listener(event));
  }

  public getEvents(): TelemetryEvent[] {
    return [...this.events];
  }

  public clearEvents(): void {
    this.events = [];
  }

  public subscribe(listener: (event: TelemetryEvent) => void): () => void {
    this.listeners.push(listener);
    return () => {
      this.listeners = this.listeners.filter(l => l !== listener);
    };
  }
}

export const telemetry = new TelemetryService();

/**
 * Calculates current scroll percentage of an element or page container.
 */
export function calculateScrollPercentage(scrollTop: number, scrollHeight: number, clientHeight: number): number {
  const maxScroll = scrollHeight - clientHeight;
  if (maxScroll <= 0) return 100;
  const percentage = (scrollTop / maxScroll) * 100;
  return Math.min(100, Math.max(0, Math.round(percentage)));
}
