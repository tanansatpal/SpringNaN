package com.example.demo.services;

import com.example.demo.dto.OpenMeteoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final RestClient restClient;
    private final double latitude;
    private final double longitude;

    public WeatherService(
            RestClient.Builder restClientBuilder,
            @Value("${app.weather.latitude:52.52}") double latitude,
            @Value("${app.weather.longitude:13.41}") double longitude
    ) {
        this.restClient = restClientBuilder.baseUrl("https://api.open-meteo.com").build();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public OpenMeteoResponse getCurrentWeather() {
        log.info("Fetching weather data from Open-Meteo for latitude={} longitude={}", latitude, longitude);

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("current", "temperature_2m,wind_speed_10m")
                        .queryParam("hourly", "temperature_2m,relative_humidity_2m,wind_speed_10m")
                        .build())
                .retrieve()
                .body(OpenMeteoResponse.class);
    }

    public String buildGreetingMessage(String username, OpenMeteoResponse response) {
        double temperature = response.current().temperature_2m();
        double windSpeed = response.current().wind_speed_10m();

        String weatherTone;
        if (temperature >= 25) {
            weatherTone = "It's a warm day";
        } else if (temperature <= 10) {
            weatherTone = "It's quite cool today";
        } else {
            weatherTone = "The weather looks pleasant";
        }

        if (windSpeed >= 20) {
            weatherTone += ", but the wind is strong";
        } else if (windSpeed >= 10) {
            weatherTone += ", with a noticeable breeze";
        } else {
            weatherTone += ", and the wind is calm";
        }

        return String.format(
                "Hello %s! %s. Current temperature is %.1f°C and wind speed is %.1f km/h.",
                username,
                weatherTone,
                temperature,
                windSpeed
        );
    }
}
