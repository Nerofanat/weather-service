package bot;

import com.example.serving_web_content.Domain.Message;
import com.example.serving_web_content.GreetingController;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherCommand {

    public static String execute(String city) {
        try {
            var response = HttpClient.getInstance().sendGet("/weather?city=" + city);
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