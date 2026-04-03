package com.example.demo.controllers;

import com.example.demo.dto.GreetingResponse;
import com.example.demo.dto.OpenMeteoResponse;
import com.example.demo.entity.User;
import com.example.demo.services.RedisService;
import com.example.demo.services.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GreetingControllerTest {

    @Test
    void greeting_shouldUseCachedWeatherWhenAvailable() {
        WeatherService weatherService = mock(WeatherService.class);
        RedisService redisService = mock(RedisService.class);
        GreetingController controller = new GreetingController(weatherService, redisService);

        User user = new User();
        user.setUsername("john");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        OpenMeteoResponse cached = new OpenMeteoResponse(
                new OpenMeteoResponse.Current(15.0, 5.0),
                null
        );

        when(redisService.get("weather_john", OpenMeteoResponse.class)).thenReturn(cached);
        when(weatherService.buildGreetingMessage(eq("john"), eq(cached))).thenReturn("Hello john!");

        GreetingResponse response = controller.greeting(authentication);

        assertEquals("john", response.username());
        assertEquals("Hello john!", response.greeting());
        assertEquals(15.0, response.temperature());
        assertEquals(5.0, response.windSpeed());

        verify(weatherService, never()).getCurrentWeather();
        verify(redisService, never()).save(anyString(), any(), anyInt());
    }

    @Test
    void greeting_shouldFetchWeatherAndSaveWhenCacheMiss() {
        WeatherService weatherService = mock(WeatherService.class);
        RedisService redisService = mock(RedisService.class);
        GreetingController controller = new GreetingController(weatherService, redisService);

        User user = new User();
        user.setUsername("john");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        OpenMeteoResponse fresh = new OpenMeteoResponse(
                new OpenMeteoResponse.Current(20.0, 8.0),
                null
        );

        when(redisService.get("weather_john", OpenMeteoResponse.class)).thenReturn(null);
        when(weatherService.getCurrentWeather()).thenReturn(fresh);
        when(weatherService.buildGreetingMessage(eq("john"), eq(fresh))).thenReturn("Fresh weather");

        GreetingResponse response = controller.greeting(authentication);

        assertEquals("john", response.username());
        assertEquals("Fresh weather", response.greeting());
        verify(redisService).save("weather_john", fresh, 3600);
    }
}