package com.epam.bogacheva.resource_service.client;

import com.epam.bogacheva.resource_service.exception.InvalidSongMetadataException;
import com.epam.bogacheva.resource_service.model.dto.SongDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SongServiceClient {

    private static final String SONGS_PATH = "/songs";

    private final RestClient restClient;

    public SongServiceClient(RestClient.Builder builder, @Value("${song-service.url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public void createSong(SongDto song) {
        restClient.post()
                .uri(SONGS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(song)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.BAD_REQUEST), (request, response) -> {
                    throw new InvalidSongMetadataException();
                })
                .toBodilessEntity();
    }

    public void deleteSongs(List<Long> ids) {
        String csvIds = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        restClient.delete()
                .uri(uriBuilder -> uriBuilder.path(SONGS_PATH).queryParam("id", csvIds).build())
                .retrieve()
                .toBodilessEntity();
    }
}
