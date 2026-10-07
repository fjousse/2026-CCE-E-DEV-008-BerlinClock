package io.github.fjousse.berlinclock;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class BerlinClockController {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss")
            .withResolverStyle(ResolverStyle.STRICT);

    private final BerlinClockConverter converter;

    public BerlinClockController(BerlinClockConverter converter) {
        this.converter = converter;
    }

    @GetMapping("/api/berlin-clock")
    public BerlinClock toBerlinClock(@RequestParam(required = false) String time) {
        if (time == null) {
            throw invalidTime();
        }

        try {
            return converter.toBerlinClock(LocalTime.parse(time, TIME_FORMAT));
        } catch (DateTimeParseException exception) {
            throw invalidTime();
        }
    }

    @GetMapping("/api/digital-time")
    public DigitalTimeResponse toDigitalTime(@RequestParam(required = false) String berlinClock) {
        if (berlinClock == null) {
            throw invalidBerlinClock();
        }

        try {
            LocalTime time = converter.toLocalTime(berlinClock);
            return new DigitalTimeResponse(time.format(TIME_FORMAT));
        } catch (IllegalArgumentException exception) {
            throw invalidBerlinClock();
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(ResponseStatusException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                exception.getStatusCode(), exception.getReason());
        return ResponseEntity.status(exception.getStatusCode()).body(problem);
    }

    private static ResponseStatusException invalidTime() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Time must be a valid HH:mm:ss value");
    }

    private static ResponseStatusException invalidBerlinClock() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Berlin Clock must be a valid 24-character R/Y/O representation");
    }
}
