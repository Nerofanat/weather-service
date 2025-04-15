package com.example.serving_web_content;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Controller  // Указывает, что это контроллер для обработки HTTP-запросов
public class GreetingController {

    @GetMapping("/greeting")  // Определяет эндпоинт для GET-запросов по пути /greeting
    public String greeting(
            @RequestParam(name="name", required=false, defaultValue="World") String name,
            // Параметр запроса с именем, который не обязателен и имеет значение по умолчанию "World"
            Map<String, Object> model  // Модель данных, которая передается в представление
    ) {
        model.put("name", name);
        // Добавляем значение параметра name в модель
        return "greeting";  // Возвращаем имя шаблона для отображения
    }

    @GetMapping // Определяет GET-эндпоинт без указания пути (будет использоваться корневой путь)
    public String main(
            Map<String, Object> model  // Модель данных
    ) {
        model.put("some", "hello, letsCode!");  // Добавляем значение в модель
        return "main";  // Возвращаем имя шаблона для отображения
    }

    // Новый эндпоинт для возврата JSON
    @GetMapping("/message")
    @ResponseBody  // Указывает, что метод возвращает JSON
    public Map<String, Object> getMessage() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Это константное сообщение");
        response.put("count", 123); // число
        response.put("isActive", true);
        return response;
    }

    // Второй вариант с использованием DTO
    @GetMapping("/message-dto")
    @ResponseBody
    public MessageDTO getMessageDTO() {
        return new MessageDTO("Это сообщение через DTO");
    }

    // Простой DTO для ответа
    private static class MessageDTO {
        private String message;

        public MessageDTO(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @GetMapping("/message-dto")
    @ResponseBody
    public WeatherDto getWeather(@RequestParam String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?" +
                "q=" + city +
                "&appid=" + apiKey +
                "&units=metric&lang=ru";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<WeatherData> response = restTemplate.getForEntity(url, WeatherData.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            WeatherData data = response.getBody();
            return new WeatherDto(
                    data.getName(),
                    data.getMain().getTemp()
            );
        }

        return null;
    }
}
}
