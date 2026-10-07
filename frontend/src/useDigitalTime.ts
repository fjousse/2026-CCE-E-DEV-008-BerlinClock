import { useEffect, useState } from "react";
import { convertBerlinClock } from "./berlinClockApi";

type Request = {
  representation: string;
};

export function useDigitalTime() {
  const [request, setRequest] = useState<Request | null>(null);
  const [time, setTime] = useState<string | null>(null);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (request === null) {
      return;
    }

    const controller = new AbortController();
    setTime(null);
    setError("");
    setIsLoading(true);

    convertBerlinClock(request.representation, controller.signal)
      .then((convertedTime) => {
        if (!controller.signal.aborted) {
          setTime(convertedTime);
        }
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setError(cause instanceof Error ? cause.message : "Could not convert the Berlin Clock.");
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
    time,
    error,
    isLoading,
    showDigitalTime: (representation: string) => setRequest({ representation }),
  };
}
