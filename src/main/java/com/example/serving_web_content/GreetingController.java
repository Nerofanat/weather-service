package com.example.serving_web_content;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GreetingController {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @GetMapping("/greeting")
    public String greeting(
            @RequestParam(name = "name", required = false, defaultValue = "World") String name,
            Map<String, Object> model
    ) {
        model.put("name", name);
        return "greeting";
    }

    @GetMapping
    public String main(Map<String, Object> model) {
        model.put("some", "hello, letsCode!");
        return "main";
    }

    @GetMapping("/message")
    @ResponseBody
    public Map<String, Object> getMessage() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Это константное сообщение");
        response.put("count", 123);
        response.put("isActive", true);
        return response;
    }

    @GetMapping("/message-dto")
    @ResponseBody
    public MessageDTO getMessageDTO() {
        return new MessageDTO("Это сообщение через DTO");
    }

    private static class MessageDTO {
        private String message;

        public MessageDTO(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @PostMapping("/weather")
    @ResponseBody
    public WeatherDto getWeather(@RequestBody WeatherRequest request) {
        String city = request.getCity();
        String url = "https://api.openweathermap.org/data/2.5/weather?" +
                "q=" + city +
                "&appid=" + apiKey +
                "&units=metric&lang=ru";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<WeatherData> response = restTemplate.getForEntity(url, WeatherData.class);

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            WeatherData data = response.getBody();
            return new WeatherDto(
                    data.getName(),
                    data.getMain().getTemp()
            );
        }
        return new WeatherDto("Ошибка", 0.0);
    }

    public class WeatherRequest {
        private String city;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    public class WeatherDto {
        private String city;
        private double temperature;

        public WeatherDto(String city, double temperature) {
            this.city = city;
            this.temperature = temperature;
        }

        public String getCity() {
            return city;
        }

        public double getTemperature() {
            return temperature;
        }
    }

    public class WeatherData {
        private String name;
        private Main main;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Main getMain() {
            return main;
        }

        public void setMain(Main main) {
            this.main = main;
        }

        public static class Main {
            private double temp;

            public double getTemp() {
                return temp;
            }

            public void setTemp(double temp) {
                this.temp = temp;
            }
        }
    }
}
