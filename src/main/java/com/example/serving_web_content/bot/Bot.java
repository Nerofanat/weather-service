package com.example.serving_web_content.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
public class Bot extends TelegramLongPollingBot {

    private final StartCommand startCommand;
    private final CachedCommand cachedCommand;
    private final WeatherCommand weatherCommand;

    // Извлекаем токен бота из application.properties
    @Value("${bot.token}")
    private String botToken;

    // Хранилище состояний команд чатов (ожидаем ввод города)
    private final Map<Long, Boolean> awaitingCityInput = new HashMap<>();

    // Конструктор с инъекцией зависимостей
    public Bot(StartCommand startCommand, CachedCommand cachedCommand, WeatherCommand weatherCommand) {
        this.startCommand = startCommand;
        this.cachedCommand = cachedCommand;
        this.weatherCommand = weatherCommand;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();
            long chatId = message.getChatId();

            // Проверяем, ждём ли мы ввод города от пользователя
            if (awaitingCityInput.containsKey(chatId)) {
                processCityInput(text, chatId); // Обработка введённого города
            } else {
                handleMessage(text, chatId); // Обычная обработка сообщений
            }
        }
    }

    /**
     * Основная логика обработки входящего текста команды.
     */
    private void handleMessage(String text, long chatId) {
        switch (text.trim().toLowerCase()) {
            case "/start":
                sendResponse(chatId, startCommand.execute());
                break;
            case "/cached":
                sendResponse(chatId, cachedCommand.execute());
                break;
            case "/weather":
                sendResponse(chatId, "Введите название города:");
                awaitingCityInput.put(chatId, true); // Ждём ввода города
                break;
            default:
                sendResponse(chatId, "Команда непонятна. Используйте /help");
                break;
        }
    }

    /**
     * Обработчик введенного названия города.
     */
    private void processCityInput(String city, long chatId) {
        // Очищаем ожидание ввода города
        awaitingCityInput.remove(chatId);

        // Отправляем сообщение с погодой в указанном городе
        sendResponse(chatId, weatherCommand.execute(city));
    }

    /**
     * Метод отправки ответа пользователю.
     */
    private void sendResponse(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace(); // Логирование ошибок отправки сообщения
        }
    }

    @Override
    public String getBotUsername() {
        return "The_weather_in_your_world_bot"; // Имя пользователя бота
    }

    @Override
    public String getBotToken() {
        return botToken; // Токен берётся из файла properties
    }
}