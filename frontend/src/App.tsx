import { useState, type FormEvent } from "react";
import BerlinClock from "./BerlinClock";
import { useBerlinClock } from "./useBerlinClock";
import "./App.css";

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
    <div className="page-shell">
      <header className="page-header">
        <p className="eyebrow">Berlin Clock Kata</p>
        <h1>Berlin Clock</h1>
        <p className="page-description">
          The lamps show time in blocks of five and single units.
        </p>
      </header>

      <main>
        <section className="panel controls-panel" aria-label="Time controls">
          <form onSubmit={handleSubmit}>
            <label className="field-label" htmlFor="time">Manual time</label>
            <div className="control-actions">
              <input
                className="time-input"
                id="time"
                type="time"
                step="1"
                required
                value={manualTime}
                onChange={(event) => setManualTime(event.target.value)}
              />
              <button className="button button-primary" type="submit">Show</button>
              <button
                className="button button-current"
                type="button"
                aria-pressed={mode === "current"}
                onClick={showCurrentTime}
              >
                Current time
              </button>
            </div>
          </form>
          <label className="refresh-control">
            <input
              type="checkbox"
              checked={autoRefresh}
              onChange={(event) => setAutoRefresh(event.target.checked)}
            />
            <span>Refresh automatically every second</span>
          </label>
        </section>

        {error && <p className="error-message" role="alert">{error}</p>}
        <BerlinClock time={time} mode={mode} rows={rows} isLoading={isLoading} />
      </main>
    </div>
  );
}
