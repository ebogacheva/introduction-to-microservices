package com.epam.bogacheva.resource_service.service;

import com.epam.bogacheva.resource_service.exception.InvalidMp3Exception;
import com.epam.bogacheva.resource_service.model.dto.SongDto;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Mp3MetadataExtractorTest {

    private final Mp3MetadataExtractor extractor = new Mp3MetadataExtractor();

    @Test
    void extractValidSample() throws IOException {
        SongDto song = extractor.extract(readResource("/valid-sample-with-required-tags.mp3"));

        assertThat(song).isEqualTo(new SongDto(null, "Test Title", "Test Artist", "Test Album", "00:08", "2025"));
    }

    @Test
    void extractPassesEmptyTagsThroughUnchanged() throws IOException {
        SongDto song = extractor.extract(readResource("/invalid-sample-with-missed-tags.mp3"));

        assertThat(song).isEqualTo(new SongDto(null, "Valid Title", "", "", "00:08", "2025"));
    }

    @Test
    void extractThrowsOnNonMp3Data() {
        byte[] data = "not an mp3".getBytes();

        assertThatThrownBy(() -> extractor.extract(data))
                .isInstanceOf(InvalidMp3Exception.class)
                .hasMessage("Invalid MP3 file");
    }

    private byte[] readResource(String name) throws IOException {
        try (InputStream stream = Objects.requireNonNull(
                getClass().getResourceAsStream(name), "Missing test resource: " + name)) {
            return stream.readAllBytes();
        }
    }
}
