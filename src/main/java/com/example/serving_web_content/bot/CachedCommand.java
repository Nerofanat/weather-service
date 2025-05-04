package com.example.serving_web_content.bot;

import com.example.serving_web_content.GreetingController;
import com.example.serving_web_content.GreetingController.WeatherDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.StringJoiner;

@Component
public class CachedCommand {

    @Autowired
    private GreetingController greetingController; // Инъекция контроллера через Spring

    /**
     * Основной метод исполнения команды /cached
     */
    public String execute() {
        try {
            // Получаем сырую коллекцию объектов WeatherDto прямо из контроллера
            Collection<WeatherDto> cachedItems = greetingController.getAllCachedWeatherAsDtos();

            if (cachedItems.isEmpty()) {
                return "🙃 Нет данных в кэше.";
            }

            // Форматируем вывод
            return formatCachedData(cachedItems);
        } catch (Exception e) {
            return "😕 Ошибка получения данных: " + e.getMessage();
        }
    }

    /**
     * Форматирует вывод кэша
     *
     * @param items коллекция объектов WeatherDto
     * @return строка с результатами
     */
    private String formatCachedData(Collection<WeatherDto> items) {
        StringJoiner joiner = new StringJoiner("\n\n", "📚 Сообщения из кэша:\n", "");
        for (WeatherDto item : items) {
            joiner.add("""
                🌐 Город: %s\n
                ⛅ Температура: %.1f°C\n
                💧 Влажность: %.0f\n
                🍃 Скорость ветра: %.1f м/с (%s)\n
                📍 Координаты: %.4f, %.4f
                """.formatted(
                    item.getCity(),
                    item.getTemperature(),
                    item.getHumidity(),
                    item.getWindSpeed(),
                    item.getWindDirection(),
                    item.getCoordinates().getLatitude(),
                    item.getCoordinates().getLongitude()
            ));
        }
        return joiner.toString();
    }
}