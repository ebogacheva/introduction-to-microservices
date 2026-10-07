package com.epam.bogacheva.song_service.exception;

public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException(Long id) {
        super("Song metadata for ID=%d not found".formatted(id));
    }
}
