package io.github.fjousse.berlinclock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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
}
