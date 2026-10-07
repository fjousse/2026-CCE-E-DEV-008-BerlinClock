package io.github.fjousse.berlinclock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BerlinClockConverterTest {

    private final BerlinClockConverter converter = new BerlinClockConverter();

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
