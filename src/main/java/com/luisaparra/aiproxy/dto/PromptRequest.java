package com.luisaparra.aiproxy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * HTTP body of POST /api/ai/generate.
 */
public record PromptRequest(
        @NotBlank(message = "El prompt no puede estar vacío.")
        @Size(max = 4000, message = "El prompt no puede superar los 4000 caracteres.")
        String prompt
) {
}
