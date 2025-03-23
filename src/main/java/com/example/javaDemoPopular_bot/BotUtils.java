package com.example.javaDemoPopular_bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class BotUtils {
    public static void startCommandToTalk(long chatId, String name, TelegramLongPollingBot bot){
        String hello = "Привет " + name + ", рад тебя видеть сегодня!";
        sendMsg(chatId, hello, bot);
    }
    public synchronized static void sendMsg(long chatId, String s, TelegramLongPollingBot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        setButtons(sendMessage);
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.info(e.toString());
        }
    }

    public static synchronized void sendMsg(SendMessage sendMessage, TelegramLongPollingBot bot) {
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.info(e.toString());
        }
    }
    public static synchronized void sendMsg(EditMessageText editMessageText, TelegramLongPollingBot bot) {
        try {
            bot.execute(editMessageText);
        } catch (TelegramApiException e) {
            log.info(e.toString());
        }
    }
    public static synchronized void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("Избранные остановки"));
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("Ближающая остановка"));
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add(new KeyboardButton("Найти остановку"));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }
}
