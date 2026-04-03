package com.example.demo.controllers;

import com.example.demo.dto.GreetingResponse;
import com.example.demo.dto.OpenMeteoResponse;
import com.example.demo.entity.User;
import com.example.demo.services.RedisService;
import com.example.demo.services.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting")
class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    private final WeatherService weatherService;
    private final RedisService redisService;

    GreetingController(WeatherService weatherService, RedisService redisService) {
        this.weatherService = weatherService;
        this.redisService = redisService;
    }

    @GetMapping
    public GreetingResponse greeting(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Greeting requested for username={}", currentUser.getUsername());

        String key = "weather_" + currentUser.getUsername();
        OpenMeteoResponse cachedWeather = redisService.get(key, OpenMeteoResponse.class);

        OpenMeteoResponse weather;

        log.info("Cached weather: {}", cachedWeather);
        if (cachedWeather != null) {
            weather = cachedWeather;
        } else {
            weather = weatherService.getCurrentWeather();
            log.info("Fetched weather: {}", weather);
            redisService.save(key, weather, 3600);
        }

        String greetingMessage = weatherService.buildGreetingMessage(currentUser.getUsername(), weather);

        return new GreetingResponse(
                currentUser.getUsername(),
                greetingMessage,
                weather.current().temperature_2m(),
                weather.current().wind_speed_10m()
        );
    }
}
