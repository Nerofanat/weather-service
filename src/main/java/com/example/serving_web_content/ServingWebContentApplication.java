package com.example.serving_web_content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import bot.Bot; // Ваш класс Telegram-бота

@SpringBootApplication
public class ServingWebContentApplication {

	public static void main(String[] args) {
//		try {
//			// Регистрация и запуск Telegram-бота
//			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//			botsApi.registerBot(new Bot()); // Передача вашего бота
//			System.out.println("Телеграм-бот успешно зарегистрирован и запущен!");
//		} catch (TelegramApiException e) {
//			e.printStackTrace();
//			System.err.println("Ошибка при регистрации Telegram-бота: " + e.getMessage());
//		}

		// Запуск основного Spring Boot-приложения
		SpringApplication.run(ServingWebContentApplication.class, args);
	}

}