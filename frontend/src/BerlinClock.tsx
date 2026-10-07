import type { BerlinClockRows } from "./berlinClockApi";

type BerlinClockProps = {
  rows: BerlinClockRows;
};

export default function BerlinClock({ rows }: BerlinClockProps) {
  return (
    <section aria-label="Berlin Clock result">
      <h2>Berlin Clock</h2>
      <dl>
        <dt>Seconds</dt>
        <dd>{rows.secondsLamp}</dd>
        <dt>Five-hour row</dt>
        <dd>{rows.fiveHourRow}</dd>
        <dt>Single-hour row</dt>
        <dd>{rows.singleHourRow}</dd>
        <dt>Five-minute row</dt>
        <dd>{rows.fiveMinuteRow}</dd>
        <dt>Single-minute row</dt>
        <dd>{rows.singleMinuteRow}</dd>
      </dl>
    </section>
  );
}
