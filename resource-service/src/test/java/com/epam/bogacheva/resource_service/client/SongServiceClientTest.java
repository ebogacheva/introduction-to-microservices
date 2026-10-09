package com.epam.bogacheva.resource_service.client;

import com.epam.bogacheva.resource_service.exception.InvalidSongMetadataException;
import com.epam.bogacheva.resource_service.model.dto.SongDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(components = SongServiceClient.class, properties = "song-service.url=http://song-service")
class SongServiceClientTest {

    private static final String SONGS_URL = "http://song-service/songs";
    private static final SongDto SONG = new SongDto(1L, "Test Title", "Test Artist", "Test Album", "00:08", "2025");

    @Autowired
    private SongServiceClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void createSongPostsJson() {
        server.expect(requestTo(SONGS_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {"id": 1, "name": "Test Title", "artist": "Test Artist",
                         "album": "Test Album", "duration": "00:08", "year": "2025"}
                        """, true))
                .andRespond(withSuccess("{\"id\": 1}", MediaType.APPLICATION_JSON));

        client.createSong(SONG);

        server.verify();
    }

    @Test
    void createSongBadRequest() {
        server.expect(requestTo(SONGS_URL))
                .andRespond(withBadRequest());

        assertThatThrownBy(() -> client.createSong(SONG))
                .isInstanceOf(InvalidSongMetadataException.class)
                .hasMessage("MP3 file has invalid or missing tags");
    }

    @Test
    void createSongServerError() {
        server.expect(requestTo(SONGS_URL))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.createSong(SONG))
                .isInstanceOf(HttpServerErrorException.class);
    }

    @Test
    void deleteSongsSendsCsv() {
        server.expect(requestTo(SONGS_URL + "?id=1,2"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("{\"ids\": [1, 2]}", MediaType.APPLICATION_JSON));

        client.deleteSongs(List.of(1L, 2L));

        server.verify();
    }
}
