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

    public String fiveMinuteRow(int minutes) {
        int litLamps = minutes / 5;
        StringBuilder row = new StringBuilder(11);
        for (int lamp = 1; lamp <= litLamps; lamp++) {
            row.append(lamp % 3 == 0 ? 'R' : 'Y');
        }
        return row.append("O".repeat(11 - litLamps)).toString();
    }

    public String singleMinuteRow(int minutes) {
        int litLamps = minutes % 5;
        return "Y".repeat(litLamps) + "O".repeat(4 - litLamps);
    }
}
