package io.github.fjousse.berlinclock;

/** The five rows of a Berlin Clock, in display order. */
public record BerlinClock(
        String secondsLamp,
        String fiveHourRow,
        String singleHourRow,
        String fiveMinuteRow,
        String singleMinuteRow) {
}
