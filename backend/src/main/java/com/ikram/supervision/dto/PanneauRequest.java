package com.ikram.supervision.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PanneauRequest(@Min(0) @Max(90) int angle) {
}
