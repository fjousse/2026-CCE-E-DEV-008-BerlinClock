package io.github.fjousse.berlinclock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class BerlinClockConverterTest {

    private final BerlinClockConverter converter = new BerlinClockConverter();

    private static String clock(String seconds, String fiveHours, String singleHours,
            String fiveMinutes, String singleMinutes) {
        return seconds + fiveHours + singleHours + fiveMinutes + singleMinutes;
    }

    @ParameterizedTest
    @CsvSource({
        "Y, 00:00:00",
        "O, 00:00:01"
    })
    void reverseConversionUsesSecondsLampParity(String secondsLamp, LocalTime expected) {
        assertEquals(expected, converter.toLocalTime(clock(
                secondsLamp, "OOOO", "OOOO", "OOOOOOOOOOO", "OOOO")));
    }

    @ParameterizedTest
    @CsvSource({
        "OOOO, OOOO, 00:00:00",
        "ROOO, OOOO, 05:00:00",
        "ROOO, RRRR, 09:00:00",
        "RROO, OOOO, 10:00:00",
        "RRRR, RRRO, 23:00:00"
    })
    void reverseConversionAddsFiveHourAndSingleHourRows(
            String fiveHourRow, String singleHourRow, LocalTime expected) {
        assertEquals(expected, converter.toLocalTime(clock(
                "Y", fiveHourRow, singleHourRow, "OOOOOOOOOOO", "OOOO")));
    }

    @ParameterizedTest
    @CsvSource({
        "OOOOOOOOOOO, OOOO, 00:00:00",
        "OOOOOOOOOOO, YYYY, 00:04:00",
        "YOOOOOOOOOO, OOOO, 00:05:00",
        "YOOOOOOOOOO, YYYY, 00:09:00",
        "YYROOOOOOOO, OOOO, 00:15:00",
        "YYRYYRYOOOO, YYOO, 00:37:00",
        "YYRYYRYYRYY, YYYY, 00:59:00"
    })
    void reverseConversionAddsFiveMinuteAndSingleMinuteRows(
            String fiveMinuteRow, String singleMinuteRow, LocalTime expected) {
        assertEquals(expected, converter.toLocalTime(clock(
                "Y", "OOOO", "OOOO", fiveMinuteRow, singleMinuteRow)));
    }

    @Test
    void reverseConversionReadsTheCompleteRepresentationInDisplayOrder() {
        assertEquals(LocalTime.of(11, 37, 1),
                converter.toLocalTime("ORROOROOOYYRYYRYOOOOYYOO"));
    }

    @ParameterizedTest
    @ValueSource(ints = {23, 25})
    void reverseConversionRejectsIncorrectLength(int length) {
        assertThrows(IllegalArgumentException.class,
                () -> converter.toLocalTime("O".repeat(length)));
    }

    @Test
    void reverseConversionRejectsUnsupportedLampCharacters() {
        assertThrows(IllegalArgumentException.class,
                () -> converter.toLocalTime("X" + "O".repeat(23)));
    }

    @ParameterizedTest
    @MethodSource("structurallyInvalidClocks")
    void reverseConversionRejectsStructurallyInconsistentRows(String representation) {
        assertThrows(IllegalArgumentException.class,
                () -> converter.toLocalTime(representation));
    }

    private static Stream<String> structurallyInvalidClocks() {
        return Stream.of(
                clock("R", "OOOO", "OOOO", "OOOOOOOOOOO", "OOOO"),
                clock("Y", "RORO", "OOOO", "OOOOOOOOOOO", "OOOO"),
                clock("Y", "YOOO", "OOOO", "OOOOOOOOOOO", "OOOO"),
                clock("Y", "OOOO", "RORO", "OOOOOOOOOOO", "OOOO"),
                clock("Y", "OOOO", "OOOO", "YYYOOOOOOOO", "OOOO"),
                clock("Y", "OOOO", "OOOO", "YOYOOOOOOOO", "OOOO"),
                clock("Y", "OOOO", "OOOO", "OOOOOOOOOOO", "RYOO"),
                clock("Y", "OOOO", "OOOO", "OOOOOOOOOOO", "YOYO"),
                clock("Y", "RRRR", "RRRR", "OOOOOOOOOOO", "OOOO"));
    }

    @ParameterizedTest
    @CsvSource({
        "0, true",
        "1, false",
        "58, true",
        "59, false"
    })
    void secondsLampIsOnForEvenSecondsAndOffForOddSeconds(int seconds, boolean expected) {
        assertEquals(expected, converter.isSecondsLampOn(seconds));
    }

    @ParameterizedTest
    @CsvSource({
        "0, OOOO",
        "4, OOOO",
        "5, ROOO",
        "9, ROOO",
        "10, RROO",
        "19, RRRO",
        "20, RRRR",
        "23, RRRR"
    })
    void fiveHourRowLightsOneRedLampForEachCompleteFiveHours(int hours, String expectedRow) {
        assertEquals(expectedRow, converter.fiveHourRow(hours));
    }

    @ParameterizedTest
    @CsvSource({
        "0, OOOO",
        "4, RRRR",
        "5, OOOO",
        "6, ROOO",
        "9, RRRR",
        "10, OOOO",
        "19, RRRR",
        "20, OOOO",
        "23, RRRO"
    })
    void singleHourRowLightsRemainingHoursAfterFiveHourBlocks(int hours, String expectedRow) {
        assertEquals(expectedRow, converter.singleHourRow(hours));
    }

    @ParameterizedTest
    @CsvSource({
        "0, OOOOOOOOOOO",
        "4, OOOOOOOOOOO",
        "5, YOOOOOOOOOO",
        "14, YYOOOOOOOOO",
        "15, YYROOOOOOOO",
        "30, YYRYYROOOOO",
        "45, YYRYYRYYROO",
        "55, YYRYYRYYRYY",
        "59, YYRYYRYYRYY"
    })
    void fiveMinuteRowLightsEachCompleteBlockWithEveryThirdLampRed(int minutes, String expectedRow) {
        assertEquals(expectedRow, converter.fiveMinuteRow(minutes));
    }

    @ParameterizedTest
    @CsvSource({
        "0, OOOO",
        "4, YYYY",
        "5, OOOO",
        "6, YOOO",
        "9, YYYY",
        "10, OOOO",
        "44, YYYY",
        "55, OOOO",
        "59, YYYY"
    })
    void singleMinuteRowLightsRemainingMinutesAfterFiveMinuteBlocks(int minutes, String expectedRow) {
        assertEquals(expectedRow, converter.singleMinuteRow(minutes));
    }
}
