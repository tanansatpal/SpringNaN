package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(
        Current current,
        Hourly hourly
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            double temperature_2m,
            double wind_speed_10m
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Hourly(
            double[] temperature_2m,
            double[] relative_humidity_2m,
            double[] wind_speed_10m
    ) {
    }
}