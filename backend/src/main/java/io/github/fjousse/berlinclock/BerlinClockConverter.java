package io.github.fjousse.berlinclock;

public class BerlinClockConverter {

    public boolean isSecondsLampOn(int seconds) {
        return seconds % 2 == 0;
    }

    public String fiveHourRow(int hours) {
        return "OOOO";
    }
}
