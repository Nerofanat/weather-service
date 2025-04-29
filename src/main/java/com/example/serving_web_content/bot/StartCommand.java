package com.example.serving_web_content.bot;

public class StartCommand {
    public static String execute() {
        return """
            🖐 Добро пожаловать!\n\n\
            Доступные команды:\n\
            /start - приветствие и инструкции.\n\
            /weather Москва - узнайте погоду в указанном городе.\n\
            /cached - посмотрите список кэшированных городов.
            """;
    }
}