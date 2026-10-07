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
}
