package com.example.serving_web_content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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