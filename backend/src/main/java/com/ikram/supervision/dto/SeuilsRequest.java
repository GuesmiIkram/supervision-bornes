package com.ikram.supervision.dto;

import jakarta.validation.constraints.Positive;

public record SeuilsRequest(@Positive double seuilTemperatureMax, @Positive double seuilPuissanceMax) {
}
