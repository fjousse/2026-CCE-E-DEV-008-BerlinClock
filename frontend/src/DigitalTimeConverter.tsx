import { useState, type FormEvent } from "react";
import { useDigitalTime } from "./useDigitalTime";
import "./DigitalTimeConverter.css";

export default function DigitalTimeConverter() {
  const [representation, setRepresentation] = useState("");
  const { time, error, isLoading, showDigitalTime } = useDigitalTime();

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    showDigitalTime(representation);
  }

  return (
    <section className="panel reverse-panel" aria-labelledby="reverse-heading">
      <h2 id="reverse-heading">Berlin Clock to digital time</h2>
      <p className="reverse-description">Enter the lamps in display order to read the digital time.</p>
      <form onSubmit={handleSubmit}>
        <label className="field-label" htmlFor="berlin-clock-input">Berlin Clock lamps</label>
        <div className="reverse-actions">
          <input
            className="reverse-input"
            id="berlin-clock-input"
            type="text"
            spellCheck={false}
            aria-describedby="reverse-hint"
            value={representation}
            onChange={(event) => setRepresentation(event.target.value)}
          />
          <button className="button button-primary" type="submit">Convert</button>
        </div>
        <p className="reverse-hint" id="reverse-hint">24 characters using R, Y, and O</p>
      </form>
      {isLoading && <p role="status">Converting…</p>}
      {error && <p className="error-message" role="alert">{error}</p>}
      {time && <output className="reverse-result" aria-live="polite">{time}</output>}
    </section>
  );
}
