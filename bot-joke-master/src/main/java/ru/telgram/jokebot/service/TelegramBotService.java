package ru.telgram.jokebot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.telgram.jokebot.model.Joke;

import java.util.List;

@Service
public class TelegramBotService {
    private final TelegramBot telegramBot;
    private final JokeService jokeService;

    @Autowired
    public TelegramBotService(TelegramBot telegramBot, JokeService jokeService) {
        this.telegramBot = telegramBot;
        this.jokeService = jokeService;
        this.telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::buttonClickReact);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    private void buttonClickReact(Update update) {
        if (update.message() != null && update.message().text() != null) {
            String messageText = update.message().text();
            long chatId = update.message().chat().id();

            if ("/start".equals(messageText)) {
                sendStartMessage(chatId);
            } else if ("Все шутки".equals(messageText)) {
                sendAllJokes(chatId);
            } else if ("Случайная шутка".equals(messageText)) {
                sendRandomJoke(chatId);
            } else if ("Выбрать шутку по id".equals(messageText)) {
                sendSelectJokeByIdMessage(chatId);
            } else if (isNumeric(messageText)) {
                long jokeId = Long.parseLong(messageText);
                sendJokeById(chatId, jokeId);
            } else {
                SendMessage request = new SendMessage(chatId, "Такой команды не существует(")
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .disableNotification(true)
                        .replyToMessageId(update.message().messageId());
                telegramBot.execute(request);
            }
        }
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }


    private void sendSelectJokeByIdMessage(long chatId) {
        SendMessage request = new SendMessage(chatId, "Введите id шутки:")
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        telegramBot.execute(request);
    }

    private void sendJokeById(long chatId, long jokeId) {
        Joke joke = jokeService.getJokeById(jokeId).orElse(null);
        if (joke != null) {
            SendMessage request = new SendMessage(chatId, joke.getId() + ". " + joke.getText())
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true);
            telegramBot.execute(request);
        } else {
            SendMessage request = new SendMessage(chatId, "Шутка с указанным id не найдена.")
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true);
            telegramBot.execute(request);
        }
    }


    private void sendStartMessage(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(
                new String[]{"Все шутки", "Случайная шутка"},
                new String[]{"Выбрать шутку по id"}
        )
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        SendMessage request = new SendMessage(chatId, "Выберите действие:")
                .replyMarkup(keyboardMarkup);

        telegramBot.execute(request);
    }

    private void sendAllJokes(long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        List<Joke> jokes = jokeService.getAllJokes();
        for (Joke joke : jokes) {
            stringBuilder.append(joke.getId()).append(". ").append(joke.getText()).append("\n");
        }

        SendMessage request = new SendMessage(chatId, stringBuilder.toString())
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        telegramBot.execute(request);
    }

    private void sendRandomJoke(long chatId) {
        Joke randomJoke = jokeService.getRandomJoke();
        if (randomJoke != null) {
            SendMessage request = new SendMessage(chatId, randomJoke.getText())
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .disableNotification(true);
            telegramBot.execute(request);
        }
    }


}
