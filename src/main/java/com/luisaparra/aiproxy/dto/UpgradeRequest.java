package com.luisaparra.aiproxy.dto;

import com.luisaparra.aiproxy.model.Plan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * HTTP body of POST /api/quota/upgrade.
 * The payment is simulated: only the card holder and the last 4 digits are received.
 */
public record UpgradeRequest(
        @NotNull(message = "Debes indicar el plan destino.")
        Plan targetPlan,

        @NotBlank(message = "El nombre del titular es obligatorio.")
        String cardHolder,

        @NotBlank(message = "Los últimos 4 dígitos de la tarjeta son obligatorios.")
        @Pattern(regexp = "\\d{4}", message = "Los últimos dígitos de la tarjeta deben ser 4 números.")
        String cardLastFour
) {
}
