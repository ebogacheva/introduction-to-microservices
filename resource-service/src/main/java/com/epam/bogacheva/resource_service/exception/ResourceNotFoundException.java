package com.epam.bogacheva.resource_service.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Long id) {
        super("Resource with ID=%d not found".formatted(id));
    }
}
