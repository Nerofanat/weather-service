package bot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class CachedCommand {
    public static String execute() {
        try {
            var response = HttpClient.getInstance().sendGet("/cache");
            return formatCached(response);
        } catch (Exception e) {
            return "😕 Ошибка получения данных: " + e.getMessage();
        }
    }

    private static String formatCached(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> cachedData = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});

            // Извлекаем названия городов из кэша
            String citiesInCache = cachedData.stream()
                    .map(map -> map.getOrDefault("city", "").toString())
                    .filter(city -> !city.isBlank())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            return """
                🗂️ Сейчас в кэше находятся данные по следующим городам:\n%s
                """.formatted(citiesInCache);
        } catch (Exception ex) {
            return "😕 Произошла ошибка при разборе данных: " + ex.getMessage();
        }
    }
}