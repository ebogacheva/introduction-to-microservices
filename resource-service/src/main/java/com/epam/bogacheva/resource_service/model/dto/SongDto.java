package com.epam.bogacheva.resource_service.model.dto;

public record SongDto(
        Long id,
        String name,
        String artist,
        String album,
        String duration,
        String year
) {
}
