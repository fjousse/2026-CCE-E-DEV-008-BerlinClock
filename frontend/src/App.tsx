import { useState, type FormEvent } from "react";
import BerlinClock from "./BerlinClock";
import { useBerlinClock } from "./useBerlinClock";

export default function App() {
  const [manualTime, setManualTime] = useState("");
  const {
    mode,
    time,
    rows,
    error,
    isLoading,
    autoRefresh,
    setAutoRefresh,
    showCurrentTime,
    showManualTime,
  } = useBerlinClock();

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    showManualTime(manualTime);
  }

  return (
    <main>
      <h1>Berlin Clock</h1>
      <p>Mode: {mode === "current" ? "Current Time" : "Manual Time"}</p>
      <button type="button" onClick={showCurrentTime}>
        Use current time
      </button>
      <label>
        <input
          type="checkbox"
          checked={autoRefresh}
          onChange={(event) => setAutoRefresh(event.target.checked)}
        />
        Automatic refresh (Current Time only)
      </label>
      <form onSubmit={handleSubmit}>
        <label htmlFor="time">Manual time (HH:mm:ss)</label>
        <input
          id="time"
          type="time"
          step="1"
          required
          value={manualTime}
          onChange={(event) => setManualTime(event.target.value)}
        />
        <button type="submit">Convert manual time</button>
      </form>

      <p>Digital time: {time}</p>
      {isLoading && <p role="status">Converting…</p>}
      {error && <p role="alert">{error}</p>}
      {rows && <BerlinClock rows={rows} />}
    </main>
  );
}
