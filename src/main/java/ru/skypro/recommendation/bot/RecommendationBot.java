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

    private static final String CMD_START = "/start";
    private static final String CMD_RECOMMEND = "/recommend";

    private final RecommendationService recommendationService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    public RecommendationBot(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

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
        // guard: нас интересуют только текстовые сообщения
        if (!isTextMessage(update)) return;

        final String text = update.getMessage().getText().trim();
        final long chatId = update.getMessage().getChatId();

        if (CMD_START.equals(text)) {
            handleStart(chatId);
            return;
        }

        if (text.startsWith(CMD_RECOMMEND)) {
            handleRecommend(chatId, text);
            return;
        }

        // дефолтный ответ
        sendMessage(chatId, "Я вас не понял. Введите /start, чтобы узнать список команд.");
    }

    // ===== handlers =====

    private void handleStart(long chatId) {
        sendMessage(chatId, """
                👋 Привет! Я бот рекомендаций.
                
                Доступная команда:
                /recommend username — получить рекомендации по имени пользователя.
                """);
    }

    private void handleRecommend(long chatId, String fullCommandText) {
        String username = parseCommandArg(fullCommandText);
        if (username == null || username.isBlank()) {
            sendMessage(chatId, "❗ Укажите имя пользователя. Пример: /recommend Иван Иванов");
            return;
        }

        String result = recommendationService.getRecommendationsText(username);
        sendMessage(chatId, result);
    }

    // ===== helpers =====

    private boolean isTextMessage(Update update) {
        return update != null
                && update.hasMessage()
                && update.getMessage().hasText();
    }

    /**
     * Возвращает аргумент после команды (всё, что после первого пробела), либо null.
     */
    private String parseCommandArg(String text) {
        int space = text.indexOf(' ');
        return (space < 0 || space == text.length() - 1) ? null : text.substring(space + 1).trim();
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            // можно заменить на логгер при желании
            e.printStackTrace();
        }
    }
}
