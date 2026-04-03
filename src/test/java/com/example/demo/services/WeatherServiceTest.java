package com.example.demo.services;

import com.example.demo.dto.OpenMeteoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    @Test
    void buildGreetingMessage_shouldDescribeWarmAndWindyWeather() {
        RestClient.Builder builder = mock(RestClient.Builder.class);
        RestClient restClient = mock(RestClient.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);

        WeatherService weatherService = new WeatherService(builder, 52.52, 13.41);

        OpenMeteoResponse response = new OpenMeteoResponse(
                new OpenMeteoResponse.Current(30.0, 22.0),
                null
        );

        String message = weatherService.buildGreetingMessage("john", response);

        assertTrue(message.contains("Hello john!"));
        assertTrue(message.contains("warm day"));
        assertTrue(message.contains("wind is strong"));
    }

    @Test
    void buildGreetingMessage_shouldDescribeCoolAndCalmWeather() {
        RestClient.Builder builder = mock(RestClient.Builder.class);
        RestClient restClient = mock(RestClient.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);

        WeatherService weatherService = new WeatherService(builder, 52.52, 13.41);

        OpenMeteoResponse response = new OpenMeteoResponse(
                new OpenMeteoResponse.Current(5.0, 3.0),
                null
        );

        String message = weatherService.buildGreetingMessage("john", response);

        assertTrue(message.contains("quite cool today"));
        assertTrue(message.contains("wind is calm"));
    }

    @Test
    void buildGreetingMessage_shouldDescribePleasantAndCalmWeather() {
        RestClient.Builder builder = mock(RestClient.Builder.class);
        RestClient restClient = mock(RestClient.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);

        WeatherService weatherService = new WeatherService(builder, 52.52, 13.41);

        OpenMeteoResponse response = new OpenMeteoResponse(
                new OpenMeteoResponse.Current(18.0, 4.0),
                null
        );

        String message = weatherService.buildGreetingMessage("john", response);

        assertTrue(message.contains("pleasant"));
        assertTrue(message.contains("wind is calm"));
    }

    @Test
    void buildGreetingMessage_shouldDescribeCoolAndBreezyWeather() {
        RestClient.Builder builder = mock(RestClient.Builder.class);
        RestClient restClient = mock(RestClient.class);

        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);

        WeatherService weatherService = new WeatherService(builder, 52.52, 13.41);

        OpenMeteoResponse response = new OpenMeteoResponse(
                new OpenMeteoResponse.Current(8.0, 12.0),
                null
        );

        String message = weatherService.buildGreetingMessage("john", response);

        assertTrue(message.contains("quite cool today"));
        assertTrue(message.contains("noticeable breeze"));
    }
}
