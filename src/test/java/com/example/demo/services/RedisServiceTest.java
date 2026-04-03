package com.example.demo.services;

import com.example.demo.dto.OpenMeteoResponse;
import org.junit.jupiter.api.Disabled;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RedisService redisService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisService = new RedisService(redisTemplate, objectMapper);
    }

    @Test
    void save_shouldSerializeAndStoreJson() {
        OpenMeteoResponse value = new OpenMeteoResponse(
                new OpenMeteoResponse.Current(10.5, 4.2),
                null
        );

        redisService.save("weather_john", value, 3600);

        verify(valueOperations).set(eq("weather_john"), contains("\"temperature_2m\":10.5"), eq(Duration.ofSeconds(3600)));
    }

    @Test
    void get_shouldDeserializeJsonToDto() {
        String json = """
                {
                  "current": {
                    "temperature_2m": 12.5,
                    "wind_speed_10m": 7.3
                  },
                  "hourly": null
                }
                """;

        when(valueOperations.get("weather_john")).thenReturn(json);

        OpenMeteoResponse result = redisService.get("weather_john", OpenMeteoResponse.class);

        assertNotNull(result);
        assertEquals(12.5, result.current().temperature_2m());
        assertEquals(7.3, result.current().wind_speed_10m());
    }

    @Test
    void get_shouldReturnNullWhenKeyMissing() {
        when(valueOperations.get("missing")).thenReturn(null);

        OpenMeteoResponse result = redisService.get("missing", OpenMeteoResponse.class);

        assertNull(result);
    }

    @Test
    void get_shouldReturnNullWhenJsonIsInvalid() {
        when(valueOperations.get("bad")).thenReturn("{invalid-json}");

        OpenMeteoResponse result = redisService.get("bad", OpenMeteoResponse.class);

        assertNull(result);
    }

    @Test
    void delete_shouldRemoveKey() {
        redisService.delete("weather_john");

        verify(redisTemplate).delete("weather_john");
    }
}
