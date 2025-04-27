package com.example.serving_web_content;

import bot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBotRegistrar implements ApplicationRunner {

    @Autowired
    private TelegramBotsApi botsApi;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            botsApi.registerBot(new Bot());
            System.out.println("Телеграм-бот успешно зарегистрирован и запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.err.println("Ошибка при регистрации Telegram-бота: " + e.getMessage());
        }
    }
}
