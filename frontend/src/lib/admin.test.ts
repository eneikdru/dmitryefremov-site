import { describe, it, expect } from 'vitest';

export interface DsarRequest {
  id: string;
  type: string;
  subject: string;
  slaHoursLeft: number;
  status: 'CRITICAL SLA' | 'BATCH PROCESS' | 'RECONCILED' | 'PURGED';
  requiresCascadingPurge: boolean;
}

export function executeDsarPurge(request: DsarRequest): DsarRequest {
  if (request.type !== 'Erasure / Right to be Forgotten' && !request.requiresCascadingPurge) {
    throw new Error('Only erasure requests require cascading purge');
  }
  return {
    ...request,
    status: 'PURGED'
  };
}

describe('Admin Dashboard Data & Deletion Workflow Logic Unit Tests', () => {
  it('correctly classifies urgent DSAR requests', () => {
    const dsar8821: DsarRequest = {
      id: '#DSAR-8821',
      type: 'Erasure / Right to be Forgotten',
      subject: 'u_8910',
      slaHoursLeft: 18,
      status: 'CRITICAL SLA',
      requiresCascadingPurge: true
    };

    expect(dsar8821.status).toBe('CRITICAL SLA');
    expect(dsar8821.requiresCascadingPurge).toBe(true);
  });

  it('executes destructive purge workflow on erasure request', () => {
    const initialRequest: DsarRequest = {
      id: '#DSAR-8821',
      type: 'Erasure / Right to be Forgotten',
      subject: 'u_8910',
      slaHoursLeft: 18,
      status: 'CRITICAL SLA',
      requiresCascadingPurge: true
    };

    const purgedRequest = executeDsarPurge(initialRequest);
    expect(purgedRequest.status).toBe('PURGED');
  });
});
