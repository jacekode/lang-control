package dev.jlynx.langcontrol.util;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class DateTimeTools {

    /**
     * Makes use of the system's UTC clock.
     *
     * @return the current {@code LocalDateTime} in UTC
     */
    public LocalDateTime getNowUtc() {
        return LocalDateTime.now(Clock.systemUTC());
    }
}
