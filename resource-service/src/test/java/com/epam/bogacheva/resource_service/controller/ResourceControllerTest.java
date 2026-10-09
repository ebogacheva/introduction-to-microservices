package com.epam.bogacheva.resource_service.controller;

import com.epam.bogacheva.resource_service.exception.InvalidCsvIdException;
import com.epam.bogacheva.resource_service.exception.InvalidIdException;
import com.epam.bogacheva.resource_service.exception.InvalidMp3Exception;
import com.epam.bogacheva.resource_service.exception.InvalidSongMetadataException;
import com.epam.bogacheva.resource_service.exception.ResourceNotFoundException;
import com.epam.bogacheva.resource_service.service.ResourceService;
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

@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

    private static final String AUDIO_MPEG = "audio/mpeg";
    private static final byte[] DATA = {1, 2, 3};

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ResourceService resourceService;

    @Test
    void uploadReturnsId() {
        when(resourceService.upload(DATA)).thenReturn(1L);

        assertThat(mvc.post().uri("/resources").contentType(AUDIO_MPEG).content(DATA))
                .hasStatusOk()
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"id": 1}""");
    }

    @Test
    void uploadRejectsWrongContentType() {
        assertThat(mvc.post().uri("/resources").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .hasStatus(400)
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Invalid file format: application/json. Only MP3 files are allowed",
                         "errorCode": "400"}""");
        verify(resourceService, never()).upload(any());
    }

    @Test
    void uploadRejectsEmptyBody() {
        assertThat(mvc.post().uri("/resources").contentType(AUDIO_MPEG))
                .hasStatus(400)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Request body is missing or malformed", "errorCode": "400"}""");
        verify(resourceService, never()).upload(any());
    }

    @Test
    void uploadReturnsBadRequestOnInvalidMp3() {
        when(resourceService.upload(DATA)).thenThrow(new InvalidMp3Exception());

        assertThat(mvc.post().uri("/resources").contentType(AUDIO_MPEG).content(DATA))
                .hasStatus(400)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Invalid MP3 file", "errorCode": "400"}""");
    }

    @Test
    void uploadReturnsBadRequestOnInvalidSongMetadata() {
        when(resourceService.upload(DATA)).thenThrow(new InvalidSongMetadataException());

        assertThat(mvc.post().uri("/resources").contentType(AUDIO_MPEG).content(DATA))
                .hasStatus(400)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "MP3 file has invalid or missing tags", "errorCode": "400"}""");
    }

    @Test
    void getByIdReturnsAudio() {
        when(resourceService.getById("1")).thenReturn(DATA);

        assertThat(mvc.get().uri("/resources/1"))
                .hasStatusOk()
                .hasHeader(HttpHeaders.CONTENT_TYPE, AUDIO_MPEG)
                .hasHeader(HttpHeaders.CONTENT_LENGTH, "3")
                .body().isEqualTo(DATA);
    }

    @Test
    void getByIdReturnsNotFound() {
        when(resourceService.getById("99999")).thenThrow(new ResourceNotFoundException(99999L));

        assertThat(mvc.get().uri("/resources/99999"))
                .hasStatus(404)
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Resource with ID=99999 not found", "errorCode": "404"}""");
    }

    @Test
    void getByIdReturnsBadRequestOnInvalidId() {
        when(resourceService.getById("ABC")).thenThrow(new InvalidIdException("ABC"));

        assertThat(mvc.get().uri("/resources/ABC"))
                .hasStatus(400)
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Invalid value 'ABC' for ID. Must be a positive integer",
                         "errorCode": "400"}""");
    }

    @Test
    void deleteReturnsDeletedIds() {
        when(resourceService.deleteByIds("1,101")).thenReturn(List.of(1L));

        assertThat(mvc.delete().uri("/resources").param("id", "1,101"))
                .hasStatusOk()
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"ids": [1]}""");
    }

    @Test
    void deleteReturnsBadRequestOnInvalidCsv() {
        when(resourceService.deleteByIds("1,V")).thenThrow(new InvalidCsvIdException("V"));

        assertThat(mvc.delete().uri("/resources").param("id", "1,V"))
                .hasStatus(400)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Invalid ID format: 'V'. Only positive integers are allowed",
                         "errorCode": "400"}""");
    }

    @Test
    void deleteReturnsBadRequestWhenIdParamMissing() {
        assertThat(mvc.delete().uri("/resources"))
                .hasStatus(400)
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "Required parameter 'id' is missing", "errorCode": "400"}""");
    }

    @Test
    void unexpectedErrorReturnsInternalServerError() {
        when(resourceService.getById("1")).thenThrow(new RuntimeException("boom"));

        assertThat(mvc.get().uri("/resources/1"))
                .hasStatus(500)
                .hasHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyJson().isStrictlyEqualTo("""
                        {"errorMessage": "An internal server error has occurred", "errorCode": "500"}""");
    }
}
