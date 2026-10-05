package com.epam.bogacheva.song_service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SongDto(

        @NotNull(message = "ID is required")
        @Positive(message = "ID must be a positive number")
        Long id,

        @NotNull(message = "Song name is required")
        @Size(min = 1, max = 100, message = "Song name must be between 1 and 100 characters")
        String name,

        @NotNull(message = "Artist name is required")
        @Size(min = 1, max = 100, message = "Artist name must be between 1 and 100 characters")
        String artist,

        @NotNull(message = "Album name is required")
        @Size(min = 1, max = 100, message = "Album name must be between 1 and 100 characters")
        String album,

        @NotNull(message = "Duration is required")
        @ValidDuration(message = "Duration must be in mm:ss format with leading zeros")
        String duration,

        @NotNull(message = "Year is required")
        @ValidYear(message = "Year must be between 1900 and 2099")
        String year
) {
}
