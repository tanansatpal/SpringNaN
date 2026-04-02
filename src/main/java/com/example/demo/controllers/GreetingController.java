package com.example.demo.controllers;

import com.example.demo.dto.GreetingResponse;
import com.example.demo.dto.OpenMeteoResponse;
import com.example.demo.entity.User;
import com.example.demo.services.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/greeting")
class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    private final WeatherService weatherService;

    GreetingController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public GreetingResponse greeting(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        log.info("Greeting requested for username={}", currentUser.getUsername());

        OpenMeteoResponse weather = weatherService.getCurrentWeather();
        String greetingMessage = weatherService.buildGreetingMessage(currentUser.getUsername(), weather);

        return new GreetingResponse(
                currentUser.getUsername(),
                greetingMessage,
                weather.current().temperature_2m(),
                weather.current().wind_speed_10m()
        );
    }
}
