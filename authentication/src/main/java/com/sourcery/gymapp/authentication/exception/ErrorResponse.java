package com.sourcery.gymapp.authentication.exception;

import java.util.List;

public record ErrorResponse(
        String message,
        ErrorCode code,
        List<FieldResponse> fields
) {
}