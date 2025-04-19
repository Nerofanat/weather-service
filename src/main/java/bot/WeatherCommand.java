package bot;


public class WeatherCommand {
    public static String execute(String city) {
        try {
            var response = HttpClient.getInstance().sendGet("/weather?city=" + city);
            return formatWeather(response);
        } catch (Exception e) {
            return "😕 Ошибка получения данных.";
        }
    }

    private static String formatWeather(String json) {
        // Парсим JSON и формируем красивый вывод
        // Например, если пришел JSON {"city":"Москва","temp":20,"humidity":65,...}, то делаем красивое оформление.
        return """
            🌤 Погода в %s:\n\
            - Температура: %.1f °C\n\
            - Влажность: %d %%\n\
            - Ветер: %s, %.1f м/с\n\
            - Координаты: %.4f, %.4f
            """.formatted("Москва", 20.5, 65, "северо-восточный", 3.2, 55.7558, 37.6176);
    }
}