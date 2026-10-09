package com.epam.bogacheva.resource_service.exception;

public class InvalidIdException extends BadRequestException {

    public InvalidIdException(String value) {
        super("Invalid value '%s' for ID. Must be a positive integer".formatted(value));
    }
}
