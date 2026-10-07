package com.epam.bogacheva.song_service.controller;

import com.epam.bogacheva.song_service.exception.InvalidIdException;
import com.epam.bogacheva.song_service.exception.SongAlreadyExistsException;
import com.epam.bogacheva.song_service.exception.SongNotFoundException;
import com.epam.bogacheva.song_service.model.dto.SongDto;
import com.epam.bogacheva.song_service.service.SongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(SongController.class)
class SongControllerTest {

    private static final SongDto DTO = new SongDto(1L, "We are the champions", "Queen", "News of the world", "02:59", "1977");
    private static final String DTO_JSON = """
            {"id": 1, "name": "We are the champions", "artist": "Queen",
             "album": "News of the world", "duration": "02:59", "year": "1977"}""";

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private SongService songService;

    @Test
    void createReturnsId() {
        when(songService.create(DTO)).thenReturn(1L);

        assertThat(mvc.post().uri("/songs").contentType(MediaType.APPLICATION_JSON).content(DTO_JSON))
                .hasStatusOk()
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"id": 1}""");
    }

    @Test
    void createReturnsValidationErrorWithDetails() {
        String json = """
                {"id": 102, "artist": "Queen", "album": "News of the world", "duration": "02:77", "year": "1"}""";

        assertThat(mvc.post().uri("/songs").contentType(MediaType.APPLICATION_JSON).content(json))
                .hasStatus(400)
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Validation error",
                         "details": {"name": "Song name is required",
                                     "duration": "Duration must be in mm:ss format with leading zeros",
                                     "year": "Year must be between 1900 and 2099"},
                         "errorCode": "400"}""");
        verify(songService, never()).create(any());
    }

    @Test
    void createReturnsBadRequestOnMalformedBody() {
        assertThat(mvc.post().uri("/songs").contentType(MediaType.APPLICATION_JSON).content("{"))
                .hasStatus(400)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Request body is missing or malformed", "errorCode": "400"}""");
    }

    @Test
    void createReturnsConflictWhenSongExists() {
        when(songService.create(DTO)).thenThrow(new SongAlreadyExistsException(1L));

        assertThat(mvc.post().uri("/songs").contentType(MediaType.APPLICATION_JSON).content(DTO_JSON))
                .hasStatus(409)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Metadata for resource ID=1 already exists", "errorCode": "409"}""");
    }

    @Test
    void getByIdReturnsSong() {
        when(songService.getById("1")).thenReturn(DTO);

        assertThat(mvc.get().uri("/songs/1"))
                .hasStatusOk()
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo(DTO_JSON);
    }

    @Test
    void getByIdReturnsNotFound() {
        when(songService.getById("99999")).thenThrow(new SongNotFoundException(99999L));

        assertThat(mvc.get().uri("/songs/99999"))
                .hasStatus(404)
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Song metadata for ID=99999 not found", "errorCode": "404"}""");
    }

    @Test
    void getByIdReturnsBadRequestOnInvalidId() {
        when(songService.getById("ABC")).thenThrow(new InvalidIdException("ABC"));

        assertThat(mvc.get().uri("/songs/ABC"))
                .hasStatus(400)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Invalid value 'ABC' for ID. Must be a positive integer", "errorCode": "400"}""");
    }

    @Test
    void getByIdHidesUnexpectedErrorText() {
        when(songService.getById("1")).thenThrow(new IllegalStateException("connection to song-db refused"));

        assertThat(mvc.get().uri("/songs/1"))
                .hasStatus(500)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "An internal server error has occurred", "errorCode": "500"}""");
    }

    @Test
    void deleteReturnsDeletedIds() {
        when(songService.deleteByIds("1,101,102")).thenReturn(List.of(1L));

        assertThat(mvc.delete().uri("/songs").param("id", "1,101,102"))
                .hasStatusOk()
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"ids": [1]}""");
    }

    @Test
    void deleteReturnsBadRequestWhenIdParameterIsMissing() {
        assertThat(mvc.delete().uri("/songs"))
                .hasStatus(400)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Required parameter 'id' is missing", "errorCode": "400"}""");
    }
}
