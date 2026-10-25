/**
 * Pattern 03: Sensorimotor Loop & Inhibitory Circuit Breaker
 * Epistemic source: Susan Hurley (Shared Circuits Model & Breakdown of Classical Sandwich)
 * Architectural synthesis: Dmitry Efremov
 */

export interface EnvironmentalSensors {
  readonly gitDiffStatLines: number;
  readonly exitCode: number;
  readonly failingTestsCount: number;
}

export interface AgentIteration {
  readonly iterationNumber: number;
  readonly toolName: string;
  readonly feedback: EnvironmentalSensors;
}

export type ExecutionVerdict = 'CONTINUE' | 'HALT_NO_PROGRESS';

export class InhibitoryCircuitBreaker {
  private history: AgentIteration[] = [];
  private readonly maxStallCycles: number;

  constructor(maxStallCycles: number = 2) {
    this.maxStallCycles = maxStallCycles;
  }

  public recordStep(step: AgentIteration): ExecutionVerdict {
    this.history.push(step);

    if (this.history.length < this.maxStallCycles) {
      return 'CONTINUE';
    }

    const recent = this.history.slice(-this.maxStallCycles);
    const zeroProgress = recent.every(
      s => s.feedback.gitDiffStatLines === 0 && s.feedback.failingTestsCount >= 0
    );

    if (zeroProgress) {
      return 'HALT_NO_PROGRESS';
    }

    return 'CONTINUE';
  }

  public getHistory(): readonly AgentIteration[] {
    return this.history;
  }
}
