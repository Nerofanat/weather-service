package bot;

import bot.StartCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            //получаем комнаду + получаем айдишник чата в который надо кинуть ответ
            handleMessage(update.getMessage().getText(), update.getMessage().getChatId());
        }
    }

    private void handleMessage(String text, long chatId) {
        switch (text.trim().toLowerCase()) {
            case "/start":
                sendResponse(chatId, StartCommand.execute());
                break;
            case "/cached":
                sendResponse(chatId, CachedCommand.execute());
                break;
            default:
                if (text.startsWith("/weather")) {
                    String[] parts = text.split("\\s+");
                    if (parts.length > 1) {
                        String city = parts[1];
                        sendResponse(chatId, WeatherCommand.execute(city));
                    } else {
                        sendResponse(chatId, "Укажите город после команды /weather");
                    }
                } else {
                    sendResponse(chatId, "Непонятная команда. Попробуйте /help.");
                }
                break;
        }
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

