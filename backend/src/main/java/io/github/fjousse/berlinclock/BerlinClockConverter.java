package io.github.fjousse.berlinclock;

import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class BerlinClockConverter {

    private static final int SHORT_ROW_LAMPS = 4;
    private static final int FIVE_MINUTE_ROW_LAMPS = 11;

    public BerlinClock toBerlinClock(LocalTime time) {
        return new BerlinClock(
                isSecondsLampOn(time.getSecond()) ? "Y" : "O",
                fiveHourRow(time.getHour()),
                singleHourRow(time.getHour()),
                fiveMinuteRow(time.getMinute()),
                singleMinuteRow(time.getMinute()));
    }

    /** Converts a lamp representation to a time, using 00 or 01 seconds for even or odd parity. */
    public LocalTime toLocalTime(String representation) {
        if (representation == null || representation.length() != 24) {
            throw new IllegalArgumentException("Berlin Clock representation must contain exactly 24 lamps.");
        }
        if (!representation.matches("[RYO]{24}")) {
            throw new IllegalArgumentException("Berlin Clock lamps must be R, Y, or O.");
        }

        int hours = 5 * litLamps(representation.substring(1, 5))
                + litLamps(representation.substring(5, 9));
        int minutes = 5 * litLamps(representation.substring(9, 20))
                + litLamps(representation.substring(20, 24));
        if (hours > 23) {
            throw new IllegalArgumentException("Berlin Clock hours must be less than 24.");
        }

        int seconds = representation.charAt(0) == 'Y' ? 0 : 1;
        LocalTime time = LocalTime.of(hours, minutes, seconds);
        BerlinClock expected = toBerlinClock(time);
        String expectedRepresentation = expected.secondsLamp() + expected.fiveHourRow()
                + expected.singleHourRow() + expected.fiveMinuteRow() + expected.singleMinuteRow();
        if (!representation.equals(expectedRepresentation)) {
            throw new IllegalArgumentException("Berlin Clock lamps are not in a valid arrangement.");
        }
        return time;
    }

    private static int litLamps(String row) {
        return (int) row.chars().filter(lamp -> lamp != 'O').count();
    }

    public boolean isSecondsLampOn(int seconds) {
        return seconds % 2 == 0;
    }

    public String fiveHourRow(int hours) {
        int litLamps = hours / 5;
        return singleColorRow(litLamps, 'R');
    }

    public String singleHourRow(int hours) {
        int litLamps = hours % 5;
        return singleColorRow(litLamps, 'R');
    }

    public String fiveMinuteRow(int minutes) {
        int litLamps = minutes / 5;
        StringBuilder row = new StringBuilder(FIVE_MINUTE_ROW_LAMPS);
        for (int lamp = 1; lamp <= litLamps; lamp++) {
            row.append(lamp % 3 == 0 ? 'R' : 'Y');
        }
        return row.append("O".repeat(FIVE_MINUTE_ROW_LAMPS - litLamps)).toString();
    }

    public String singleMinuteRow(int minutes) {
        int litLamps = minutes % 5;
        return singleColorRow(litLamps, 'Y');
    }

    private static String singleColorRow(int litLamps, char color) {
        return String.valueOf(color).repeat(litLamps)
                + "O".repeat(SHORT_ROW_LAMPS - litLamps);
    }
}
