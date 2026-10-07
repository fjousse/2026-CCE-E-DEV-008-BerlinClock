package io.github.fjousse.berlinclock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BerlinClockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void convertsTimeIntoFiveNamedRows() throws Exception {
        mockMvc.perform(get("/api/berlin-clock").param("time", "23:12:47"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secondsLamp").value("O"))
                .andExpect(jsonPath("$.fiveHourRow").value("RRRR"))
                .andExpect(jsonPath("$.singleHourRow").value("RRRO"))
                .andExpect(jsonPath("$.fiveMinuteRow").value("YYOOOOOOOOO"))
                .andExpect(jsonPath("$.singleMinuteRow").value("YYOO"));
    }

    @Test
    void convertsStartOfDay() throws Exception {
        mockMvc.perform(get("/api/berlin-clock").param("time", "00:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secondsLamp").value("Y"))
                .andExpect(jsonPath("$.fiveHourRow").value("OOOO"))
                .andExpect(jsonPath("$.singleHourRow").value("OOOO"))
                .andExpect(jsonPath("$.fiveMinuteRow").value("OOOOOOOOOOO"))
                .andExpect(jsonPath("$.singleMinuteRow").value("OOOO"));
    }

    @Test
    void convertsEndOfDay() throws Exception {
        mockMvc.perform(get("/api/berlin-clock").param("time", "23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secondsLamp").value("O"))
                .andExpect(jsonPath("$.fiveHourRow").value("RRRR"))
                .andExpect(jsonPath("$.singleHourRow").value("RRRO"))
                .andExpect(jsonPath("$.fiveMinuteRow").value("YYRYYRYYRYY"))
                .andExpect(jsonPath("$.singleMinuteRow").value("YYYY"));
    }

    @Test
    void rejectsInvalidTime() throws Exception {
        mockMvc.perform(get("/api/berlin-clock").param("time", "23:60:00"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Time must be a valid HH:mm:ss value"));
    }

    @Test
    void rejectsIncompleteTime() throws Exception {
        mockMvc.perform(get("/api/berlin-clock").param("time", "23:12"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Time must be a valid HH:mm:ss value"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"24:00:00", "1:02:03", "12:34:56.7", "noon"})
    void rejectsMalformedTime(String time) throws Exception {
        mockMvc.perform(get("/api/berlin-clock").param("time", time))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Time must be a valid HH:mm:ss value"));
    }

    @Test
    void rejectsMissingTime() throws Exception {
        mockMvc.perform(get("/api/berlin-clock"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Time must be a valid HH:mm:ss value"));
    }

    @Test
    void convertsBerlinClockIntoDigitalTime() throws Exception {
        mockMvc.perform(get("/api/digital-time")
                        .param("berlinClock", "ORROOROOOYYRYYRYOOOOYYOO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value("11:37:01"));
    }

    @Test
    void reverseConversionKeepsSecondsInTheDigitalTimeFormat() throws Exception {
        mockMvc.perform(get("/api/digital-time")
                        .param("berlinClock", "Y" + "O".repeat(23)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time").value("00:00:00"));
    }

    @Test
    void rejectsMissingBerlinClock() throws Exception {
        mockMvc.perform(get("/api/digital-time"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("Berlin Clock must be a valid 24-character R/Y/O representation"));
    }

    @ParameterizedTest
    @MethodSource("invalidBerlinClocks")
    void rejectsInvalidBerlinClock(String representation) throws Exception {
        mockMvc.perform(get("/api/digital-time").param("berlinClock", representation))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail")
                        .value("Berlin Clock must be a valid 24-character R/Y/O representation"));
    }

    private static Stream<String> invalidBerlinClocks() {
        return Stream.of(
                "O".repeat(23),
                "X" + "O".repeat(23),
                "Y" + "RORO" + "O".repeat(19),
                "Y" + "RRRRRRRR" + "O".repeat(15));
    }
}
