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
