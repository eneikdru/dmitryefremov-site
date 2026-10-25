/**
 * Pattern 02: Popper-Thagard Falsification Test Harness
 * Epistemic source: Paul Thagard (Computational Explanatory Coherence & Falsification)
 * Architectural synthesis: Dmitry Efremov
 */

export interface FalsificationResult {
  passed: boolean;
  attemptedRuns: number;
  failures: string[];
}

export type FallibleExecution<T> = () => Promise<T>;

/**
 * Прогоняет функцию в условиях параллельной конкурентной нагрузки,
 * проверяя устойчивость к гонкам и дедлокам без синтетических моков.
 */
export async function runConcurrencyFalsificationTest<T>(
  task: FallibleExecution<T>,
  concurrencyCount: number = 10
): Promise<FalsificationResult> {
  const promises = Array.from({ length: concurrencyCount }, () => task());
  const settled = await Promise.allSettled(promises);

  const failures: string[] = [];
  settled.forEach((res, idx) => {
    if (res.status === 'rejected') {
      failures.push(`Task #${idx} rejected: ${String(res.reason)}`);
    }
  });

  return {
    passed: failures.length === 0,
    attemptedRuns: concurrencyCount,
    failures
  };
}
