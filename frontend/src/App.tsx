import { useState, type FormEvent } from "react";
import BerlinClock from "./BerlinClock";
import { convertTime, type BerlinClockRows } from "./berlinClockApi";

export default function App() {
  const [time, setTime] = useState("");
  const [result, setResult] = useState<{ time: string; rows: BerlinClockRows } | null>(null);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsLoading(true);
    setError("");

    try {
      const rows = await convertTime(time);
      setResult({ time, rows });
    } catch (cause) {
      setResult(null);
      setError(cause instanceof Error ? cause.message : "Could not convert the time.");
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <main>
      <h1>Berlin Clock</h1>
      <form onSubmit={handleSubmit}>
        <label htmlFor="time">Time (HH:mm:ss)</label>
        <input
          id="time"
          type="time"
          step="1"
          required
          value={time}
          onChange={(event) => setTime(event.target.value)}
        />
        <button type="submit" disabled={isLoading}>
          Convert
        </button>
      </form>

      {isLoading && <p role="status">Converting…</p>}
      {error && <p role="alert">{error}</p>}
      {result && (
        <>
          <p>Digital time: {result.time}</p>
          <BerlinClock rows={result.rows} />
        </>
      )}
    </main>
  );
}
