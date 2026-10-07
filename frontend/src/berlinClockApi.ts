export type BerlinClockRows = {
  secondsLamp: string;
  fiveHourRow: string;
  singleHourRow: string;
  fiveMinuteRow: string;
  singleMinuteRow: string;
};

type ProblemDetail = {
  detail?: string;
};

export async function convertTime(time: string, signal?: AbortSignal): Promise<BerlinClockRows> {
  const response = await fetch(`/api/berlin-clock?time=${encodeURIComponent(time)}`, { signal });

  if (!response.ok) {
    const problem: ProblemDetail = await response.json().catch(() => ({}));
    throw new Error(problem.detail ?? "Could not convert the time.");
  }

  return response.json() as Promise<BerlinClockRows>;
}
