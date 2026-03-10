package com.andruf.sez.mapper;

import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;

@Component
public class DateTimeMapper {

    public OffsetDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime;
    }

}