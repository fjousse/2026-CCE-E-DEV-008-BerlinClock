package io.github.fjousse.berlinclock;

public class BerlinClockConverter {

    public boolean isSecondsLampOn(int seconds) {
        return seconds % 2 == 0;
    }

    public String fiveHourRow(int hours) {
        int litLamps = hours / 5;
        return "R".repeat(litLamps) + "O".repeat(4 - litLamps);
    }

    public String singleHourRow(int hours) {
        int litLamps = hours % 5;
        return "R".repeat(litLamps) + "O".repeat(4 - litLamps);
    }
}
