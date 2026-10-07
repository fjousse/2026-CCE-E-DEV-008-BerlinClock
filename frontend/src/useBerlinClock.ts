import { useEffect, useState } from "react";
import { convertTime, type BerlinClockRows } from "./berlinClockApi";

type TimeRequest = {
  mode: "current" | "manual";
  time: string;
};

type ConversionResult = TimeRequest & {
  rows: BerlinClockRows;
};

function localTimeNow(): string {
  const now = new Date();
  const twoDigits = (value: number) => String(value).padStart(2, "0");
  return `${twoDigits(now.getHours())}:${twoDigits(now.getMinutes())}:${twoDigits(now.getSeconds())}`;
}

export function useBerlinClock() {
  const [request, setRequest] = useState<TimeRequest>(() => ({
    mode: "current",
    time: localTimeNow(),
  }));
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [result, setResult] = useState<ConversionResult | null>(null);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const controller = new AbortController();
    setError("");
    setIsLoading(true);

    convertTime(request.time, controller.signal)
      .then((convertedRows) => {
        if (!controller.signal.aborted) {
          setResult({ ...request, rows: convertedRows });
        }
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setError(cause instanceof Error ? cause.message : "Could not convert the time.");
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setIsLoading(false);
        }
      });

    return () => controller.abort();
  }, [request]);

  useEffect(() => {
    if (request.mode !== "current" || !autoRefresh) {
      return;
    }

    const timer = window.setInterval(() => {
      const time = localTimeNow();
      setRequest((previous) =>
        previous.mode === "current" && previous.time !== time
          ? { mode: "current", time }
          : previous,
      );
    }, 1000);

    return () => window.clearInterval(timer);
  }, [request.mode, autoRefresh]);

  const visibleResult = result?.mode === request.mode
    && (request.mode === "current" || result.time === request.time)
    ? result
    : null;

  return {
    mode: request.mode,
    time: visibleResult?.time ?? request.time,
    rows: visibleResult?.rows ?? null,
    error,
    isLoading: isLoading && !visibleResult,
    autoRefresh,
    setAutoRefresh,
    showCurrentTime: () => setRequest({ mode: "current", time: localTimeNow() }),
    showManualTime: (time: string) => setRequest({ mode: "manual", time }),
  };
}
