package ru.skypro.recommendation.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.skypro.recommendation.service.RecommendationService;

@Component
public class RecommendationBot extends TelegramLongPollingBot {

    private final RecommendationService recommendationService;

    public RecommendationBot(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                sendMessage(chatId, """
                        👋 Привет! Я бот рекомендаций.
                        
                        Доступная команда:
/recommend username — получить рекомендации по имени пользователя.
                        """);
            } else if (messageText.startsWith("/recommend")) {
                String[] parts = messageText.split(" ", 2);
                if (parts.length < 2) {
                    sendMessage(chatId, "❗ Укажите имя пользователя. Пример: /recommend Иван Иванов");
                } else {
                    String username = parts[1].trim();
                    String result = recommendationService.getRecommendationsText(username);
                    sendMessage(chatId, result);
                }
            } else {
                sendMessage(chatId, "Я вас не понял. Введите /start, чтобы узнать список команд.");
            }
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
