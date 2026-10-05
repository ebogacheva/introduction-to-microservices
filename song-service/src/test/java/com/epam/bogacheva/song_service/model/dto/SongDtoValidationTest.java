package com.epam.bogacheva.song_service.model.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class SongDtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Map<String, String> errors(SongDto dto) {
        return validator.validate(dto).stream().collect(Collectors.toMap(
                v -> v.getPropertyPath().toString(), ConstraintViolation::getMessage));
    }

    @Test
    void valid() {
        assertThat(errors(new SongDto(1L, "We are the champions", "Queen", "News of the world", "02:59", "1977"))).isEmpty();
        assertThat(errors(new SongDto(1L, "a", "b", "c", "00:00", "1900"))).isEmpty();
        assertThat(errors(new SongDto(1L, "a", "b", "c", "99:59", "2099"))).isEmpty();
    }

    @Test
    void collectionCases() {
        assertThat(errors(new SongDto(102L, "n", "a", "al", "02:77", "01977"))).isEqualTo(Map.of(
                "duration", "Duration must be in mm:ss format with leading zeros",
                "year", "Year must be between 1900 and 2099"));
        assertThat(errors(new SongDto(102L, "n", "a", "al", "0299", "1977"))).containsOnlyKeys("duration");
        assertThat(errors(new SongDto(102L, "n", "a", "al", "35", "1977"))).containsOnlyKeys("duration");
        assertThat(errors(new SongDto(102L, "n", "a", "al", "02:59", "1"))).containsOnlyKeys("year");
        assertThat(errors(new SongDto(102L, "", "", "", "", ""))).isEqualTo(Map.of(
                "duration", "Duration must be in mm:ss format with leading zeros",
                "year", "Year must be between 1900 and 2099",
                "artist", "Artist name must be between 1 and 100 characters",
                "album", "Album name must be between 1 and 100 characters",
                "name", "Song name must be between 1 and 100 characters"));
        assertThat(errors(new SongDto(103L, null, "a", "al", "02:59", "1977"))).isEqualTo(Map.of("name", "Song name is required"));
        assertThat(errors(new SongDto(102L, null, null, null, null, null))).isEqualTo(Map.of(
                "duration", "Duration is required",
                "artist", "Artist name is required",
                "year", "Year is required",
                "album", "Album name is required",
                "name", "Song name is required"));
    }

    @Test
    void edgeCases() {
        assertThat(errors(new SongDto(1L, "n", "a", "al", "2:59", "1899"))).containsOnlyKeys("duration", "year");
        assertThat(errors(new SongDto(1L, "n", "a", "al", "02:5a", "2100"))).containsOnlyKeys("duration", "year");
        assertThat(errors(new SongDto(1L, "n", "a", "al", "02:59:00", "+977"))).containsOnlyKeys("duration", "year");
    }
}
