package net.engineeringdigest.journalApp.service;

import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.constants.Placeholder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherService {

    @Value("${weather_api_key}")
    private String apiKey;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisService redisService;

    @GetMapping
    public WeatherResponse getWeather(String city){
        WeatherResponse weatherResponse = redisService.get("weather_of_"+city, WeatherResponse.class);
        if(weatherResponse != null){
            return weatherResponse;
        }
        String finalAPI = appCache.APPCACHE.get(AppCache.KEYS.WEATHER_API.getName()).replace(Placeholder.API_KEY, apiKey).replace(Placeholder.CITY, city);
        ResponseEntity<WeatherResponse> weatherResponseEntity = restTemplate.exchange(finalAPI, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse responseBody = weatherResponseEntity.getBody();
        if(responseBody != null){
            redisService.set("weather_of_"+city, responseBody, 300l);
        }
        return responseBody;
    }

}
