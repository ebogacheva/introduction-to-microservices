package com.epam.bogacheva.resource_service.exception;

public class InvalidSongMetadataException extends BadRequestException {

    public InvalidSongMetadataException() {
        super("MP3 file has invalid or missing tags");
    }
}
