import { useEffect, useState } from "react";
import { convertTime, type BerlinClockRows } from "./berlinClockApi";

type TimeRequest = {
  mode: "current" | "manual";
  time: string;
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
  const [rows, setRows] = useState<BerlinClockRows | null>(null);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const controller = new AbortController();
    setRows(null);
    setError("");
    setIsLoading(true);

    convertTime(request.time, controller.signal)
      .then((convertedRows) => {
        if (!controller.signal.aborted) {
          setRows(convertedRows);
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

  return {
    mode: request.mode,
    time: request.time,
    rows,
    error,
    isLoading,
    showCurrentTime: () => setRequest({ mode: "current", time: localTimeNow() }),
    showManualTime: (time: string) => setRequest({ mode: "manual", time }),
  };
}
