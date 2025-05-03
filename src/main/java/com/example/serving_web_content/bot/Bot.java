package com.example.serving_web_content.bot;

import com.example.serving_web_content.GreetingController;
import com.example.serving_web_content.GreetingController.WeatherDto;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Хранилище для временного сохранения состояния чата (для ввода города)
    private final Map<Long, String> waitingForCityInput = new HashMap<>();

    // Конструктор с внедрением зависимостей
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

            // Проверяем, ждёт ли бот ввода города от пользователя
            if (waitingForCityInput.containsKey(chatId)) {
                processCityInput(text, chatId); // Обрабатываем введённый город
            } else {
                handleMessage(text, chatId); // Обычная обработка сообщений
            }
        }
    }

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
                waitingForCityInput.put(chatId, "/weather"); // Ждём ввод города
                break;
            default:
                sendResponse(chatId, "Непонятная команда. Попробуйте /help.");
                break;
        }
    }

    private void processCityInput(String city, long chatId) {
        // Удаляем состояние ожидания ввода города
        waitingForCityInput.remove(chatId);

        // Запрашиваем погоду по указанному городу
        sendResponse(chatId, weatherCommand.execute(city));
    }

    private void sendResponse(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "The_weather_in_your_world_bot";
    }

    @Override
    public String getBotToken() {
        return "7732911982:AAEU7fxw624DigEWTrFyswN2ILSesYgXE80";
    }
}