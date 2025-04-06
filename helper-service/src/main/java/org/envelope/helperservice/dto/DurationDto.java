package org.envelope.helperservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DurationDto(@Schema(description = "Количество секунд назад", example = "60")
                          @Min(5) @Max(1000000)
                          Long seconds) {
}
