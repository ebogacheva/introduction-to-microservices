package com.epam.bogacheva.resource_service.service;

import com.epam.bogacheva.resource_service.exception.CsvTooLongException;
import com.epam.bogacheva.resource_service.exception.InvalidCsvIdException;
import com.epam.bogacheva.resource_service.exception.InvalidIdException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdParserTest {

    private final IdParser parser = new IdParser();

    @Test
    void parseIdValid() {
        assertThat(parser.parseId("1")).isEqualTo(1L);
        assertThat(parser.parseId("2147483647")).isEqualTo(2147483647L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC", "1.1", "-1", "0", "", "99999999999999999999"})
    void parseIdInvalid(String value) {
        assertThatThrownBy(() -> parser.parseId(value))
                .isInstanceOf(InvalidIdException.class)
                .hasMessage("Invalid value '%s' for ID. Must be a positive integer".formatted(value));
    }

    @Test
    void parseCsvIdsValid() {
        assertThat(parser.parseCsvIds("1,2,3")).containsExactly(1L, 2L, 3L);
        assertThat(parser.parseCsvIds("99999")).containsExactly(99999L);
    }

    @Test
    void parseCsvIdsInvalidValue() {
        assertThatThrownBy(() -> parser.parseCsvIds("1,2,3,4,V"))
                .isInstanceOf(InvalidCsvIdException.class)
                .hasMessage("Invalid ID format: 'V'. Only positive integers are allowed");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,2,", "1,,2", ""})
    void parseCsvIdsEmptyValue(String csv) {
        assertThatThrownBy(() -> parser.parseCsvIds(csv))
                .isInstanceOf(InvalidCsvIdException.class)
                .hasMessage("Invalid ID format: ''. Only positive integers are allowed");
    }

    @Test
    void parseCsvIdsValueWithSpace() {
        assertThatThrownBy(() -> parser.parseCsvIds("1, 2"))
                .isInstanceOf(InvalidCsvIdException.class)
                .hasMessage("Invalid ID format: ' 2'. Only positive integers are allowed");
    }

    @Test
    void parseCsvIdsTooLong() {
        String csv = String.join(",", List.of(
                "2147483647", "2147483646", "2147483645", "2147483644", "2147483643", "2147483642",
                "2147483641", "2147483640", "2147483639", "2147483638", "2147483637", "2147483636",
                "2147483635", "2147483634", "2147483633", "2147483632", "2147483631", "2147483630",
                "2147483629"));

        assertThatThrownBy(() -> parser.parseCsvIds(csv))
                .isInstanceOf(CsvTooLongException.class)
                .hasMessage("CSV string is too long: received 208 characters, maximum allowed is 200");
    }
}
