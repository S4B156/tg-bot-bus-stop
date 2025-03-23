package com.example.javaDemoPopular_bot.settings;

import com.example.javaDemoPopular_bot.handlers.MessageHandler;
import com.example.javaDemoPopular_bot.keyboard.BusStopInlineKeyboardFavorite;
import com.example.javaDemoPopular_bot.keyboard.BusStopInlineKeyboardFind;
import com.example.javaDemoPopular_bot.entite.BusStop;
import com.example.javaDemoPopular_bot.entite.BusStopDoc;
import com.example.javaDemoPopular_bot.entite.User;
import com.example.javaDemoPopular_bot.repository.UserRepository;
import com.example.javaDemoPopular_bot.service.BusStopService;
import com.example.javaDemoPopular_bot.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CommandsBot extends TelegramLongPollingBot{
    private final BotConfig botConfig = new BotConfig();

    @Autowired private MessageHandler messageHandler;
    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Async
    @Override
    public void onUpdateReceived(Update update) {
        messageHandler.handleUpdate(update, this);
    }
}