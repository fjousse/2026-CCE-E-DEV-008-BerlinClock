package io.github.fjousse.berlinclock;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
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

    @Test
    void rejectsMissingTime() throws Exception {
        mockMvc.perform(get("/api/berlin-clock"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Time must be a valid HH:mm:ss value"));
    }
}
