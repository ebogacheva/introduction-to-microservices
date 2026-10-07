package com.epam.bogacheva.song_service.exception;

public class SongAlreadyExistsException extends RuntimeException {

    public SongAlreadyExistsException(Long id) {
        super("Metadata for resource ID=%d already exists".formatted(id));
    }
}
