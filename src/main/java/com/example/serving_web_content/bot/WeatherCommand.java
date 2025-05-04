package com.example.serving_web_content.bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class WeatherCommand {

    public static String execute(String city) {
        try {
            var response = HttpClient.getInstance().sendGet("/weather?city=" + city);

            // Парсим ответ с помощью Jackson
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Проверяем наличие ошибки
            if (root.has("city") && root.get("city").asText().equals("Ошибка")) {
                return "🚫 Город не найден.";
            }

            return formatWeather(response);
        } catch (Exception e) {
            return "😕 Ошибка получения данных: " + e.getMessage();
        }
    }

    private static String formatWeather(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            WeatherInfo weather = mapper.readValue(json, WeatherInfo.class);

            return """
                🌤 Погода в %s:\n\
                - Температура: %.1f °C\n\
                - Влажность: %d %%\n\
                - Ветер: %s, %.1f м/с\n\
                - Координаты: %.4f, %.4f
                """.formatted(
                    weather.getCity(),
                    weather.getTemperature(),
                    weather.getHumidity(),
                    weather.getWindDirection(),
                    weather.getWindSpeed(),
                    weather.getCoordinates().getLatitude(),
                    weather.getCoordinates().getLongitude()
            );
        } catch (Exception ex) {
            return "😕 Произошла ошибка при разборе данных: " + ex.getMessage();

        }
    }
}