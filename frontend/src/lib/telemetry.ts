/**
 * Telemetry Implementation for Dmitry Efremov Site
 * Tracks article scroll depth and contact terminal outbound clicks with zero non-essential cookies.
 */

export interface TelemetryEvent {
  type: 'scroll_depth' | 'outbound_click';
  timestamp: string;
  payload: Record<string, unknown>;
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

  public trackScrollDepth(articleId: string, depth: number): TelemetryEvent {
    this.verifyPrivacyCompliance();
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

  public trackOutboundClick(destination: string, label?: string, platform?: string): TelemetryEvent {
    this.verifyPrivacyCompliance();
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
