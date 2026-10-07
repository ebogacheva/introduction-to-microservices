package com.epam.bogacheva.song_service.model.dto;

import java.util.Map;

public record ValidationErrorDto(String errorMessage, Map<String, String> details, String errorCode) {
}
