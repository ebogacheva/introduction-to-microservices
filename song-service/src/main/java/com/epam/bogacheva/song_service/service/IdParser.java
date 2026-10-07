package com.epam.bogacheva.song_service.service;

import com.epam.bogacheva.song_service.exception.CsvTooLongException;
import com.epam.bogacheva.song_service.exception.InvalidCsvIdException;
import com.epam.bogacheva.song_service.exception.InvalidIdException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class IdParser {

    private static final int MAX_CSV_LENGTH = 200;

    public Long parseId(String value) {
        return parsePositiveLong(value)
                .orElseThrow(() -> new InvalidIdException(value));
    }

    public List<Long> parseCsvIds(String csv) {
        if (csv.length() > MAX_CSV_LENGTH) {
            throw new CsvTooLongException(csv.length(), MAX_CSV_LENGTH);
        }
        return Arrays.stream(csv.split(",", -1))
                .map(this::parseCsvId)
                .toList();
    }

    private Long parseCsvId(String value) {
        return parsePositiveLong(value)
                .orElseThrow(() -> new InvalidCsvIdException(value));
    }

    private Optional<Long> parsePositiveLong(String value) {
        try {
            return Optional.of(Long.parseLong(value)).filter(id -> id > 0);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
