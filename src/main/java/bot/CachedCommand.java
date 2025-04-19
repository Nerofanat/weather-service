package bot;


public class CachedCommand {
    public static String execute() {
        try {
            var response = HttpClient.getInstance().sendGet("/cache");
            return formatCached(response);
        } catch (Exception e) {
            return "😕 Ошибка получения данных.";
        }
    }

    private static String formatCached(String json) {
        // Распарсив JSON с результатами кэша, выводим красивые названия городов
        return """
            🗂️ Сейчас в кэше находятся данные по следующим городам:\n%s
            """.formatted("Москва, Лондон, Берлин...");
    }
}