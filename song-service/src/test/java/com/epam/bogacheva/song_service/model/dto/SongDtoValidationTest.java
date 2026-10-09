package com.epam.bogacheva.song_service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SongDtoValidationTest {

    private static final String DURATION_FORMAT_MESSAGE = "Duration must be in mm:ss format with leading zeros";
    private static final String YEAR_RANGE_MESSAGE = "Year must be between 1900 and 2099";

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @CsvSource({"00:00, 1900", "02:59, 1977", "99:59, 2099"})
    void validSongHasNoErrors(String duration, String year) {
        assertThat(errors(song(duration, year))).isEmpty();
    }

    @Test
    void oneCharacterNamesAreValid() {
        assertThat(errors(new SongDto(1L, "a", "b", "c", "02:59", "1977"))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"02:77","0299", "35", "2:59", "02:5a", "02:59:00"})
    void invalidDurationReportsFormatMessage(String duration) {
        assertThat(errors(song(duration, "1977"))).isEqualTo(Map.of("duration", DURATION_FORMAT_MESSAGE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"01977", "1", "1899", "2100", "+977"})
    void invalidYearReportsRangeMessage(String year) {
        assertThat(errors(song("02:59", year))).isEqualTo(Map.of("year", YEAR_RANGE_MESSAGE));
    }

    @Test
    void invalidDurationAndYearReportOneMessageEach() {
        assertThat(errors(song("02:77", "01977"))).isEqualTo(Map.of(
                "duration", DURATION_FORMAT_MESSAGE,
                "year", YEAR_RANGE_MESSAGE));
    }

    @Test
    void emptyFieldsReportSizeAndFormatMessages() {
        assertThat(errors(new SongDto(1L, "", "", "", "", ""))).isEqualTo(Map.of(
                "name", "Song name must be between 1 and 100 characters",
                "artist", "Artist name must be between 1 and 100 characters",
                "album", "Album name must be between 1 and 100 characters",
                "duration", DURATION_FORMAT_MESSAGE,
                "year", YEAR_RANGE_MESSAGE));
    }

    @Test
    void missingNameReportsRequiredMessage() {
        SongDto dto = new SongDto(1L, null, "Queen", "News of the world", "02:59", "1977");

        assertThat(errors(dto)).isEqualTo(Map.of("name", "Song name is required"));
    }

    @Test
    void missingFieldsReportRequiredMessages() {
        assertThat(errors(new SongDto(1L, null, null, null, null, null))).isEqualTo(Map.of(
                "name", "Song name is required",
                "artist", "Artist name is required",
                "album", "Album name is required",
                "duration", "Duration is required",
                "year", "Year is required"));
    }

    private SongDto song(String duration, String year) {
        return new SongDto(1L, "We are the champions", "Queen", "News of the world", duration, year);
    }

    private Map<String, String> errors(SongDto dto) {
        return validator.validate(dto).stream().collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(), ConstraintViolation::getMessage));
    }
}
