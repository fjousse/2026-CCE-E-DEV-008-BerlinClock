import type { BerlinClockRows } from "./berlinClockApi";
import "./BerlinClock.css";

type BerlinClockProps = {
  time: string;
  mode: "current" | "manual";
  rows: BerlinClockRows | null;
  isLoading: boolean;
};

type LampRowProps = {
  label: string;
  value: string;
  shape: "seconds" | "short" | "long";
};

function LampRow({ label, value, shape }: LampRowProps) {
  return (
    <div className="clock-row">
      <span className="clock-row-label">{label}</span>
      <div className={`clock-lamps clock-lamps--${shape}`} aria-hidden="true">
        {Array.from(value, (lamp, index) => (
          <span
            className={`lamp lamp--${lamp === "R" ? "red" : lamp === "Y" ? "yellow" : "off"}`}
            key={index}
          />
        ))}
      </div>
      <code className="clock-row-code">{value}</code>
    </div>
  );
}

export default function BerlinClock({ time, mode, rows, isLoading }: BerlinClockProps) {
  return (
    <section className="panel clock-panel" aria-label="Berlin Clock result">
      <div className="clock-heading">
        <h2>{time}</h2>
        <span className="mode-label">{mode === "current" ? "Current time" : "Manual time"}</span>
      </div>
      {isLoading && <p className="clock-status" role="status">Converting…</p>}
      {rows && (
        <>
          <div className="clock-rows">
            <LampRow label="Seconds" value={rows.secondsLamp} shape="seconds" />
            <LampRow label="Five-hour blocks" value={rows.fiveHourRow} shape="short" />
            <LampRow label="Single hours" value={rows.singleHourRow} shape="short" />
            <LampRow label="Five-minute blocks" value={rows.fiveMinuteRow} shape="long" />
            <LampRow label="Single minutes" value={rows.singleMinuteRow} shape="short" />
          </div>
          <p className="lamp-legend">R = red <span>Y = yellow</span> <span>O = off</span></p>
        </>
      )}
    </section>
  );
}
