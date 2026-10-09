package com.epam.bogacheva.resource_service.exception;

public class InvalidMp3Exception extends BadRequestException {

    public InvalidMp3Exception() {
        super("Invalid MP3 file");
    }
}
