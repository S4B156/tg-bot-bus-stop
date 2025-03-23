package com.example.javaDemoPopular_bot.handlers;

import com.example.javaDemoPopular_bot.BotUtils;
import com.example.javaDemoPopular_bot.settings.LimitUsers;
import com.example.javaDemoPopular_bot.entite.BusStop;
import com.example.javaDemoPopular_bot.entite.BusStopDoc;
import com.example.javaDemoPopular_bot.entite.User;
import com.example.javaDemoPopular_bot.keyboard.BusStopInlineKeyboardFavorite;
import com.example.javaDemoPopular_bot.keyboard.BusStopInlineKeyboardFind;
import com.example.javaDemoPopular_bot.repository.UserRepository;
import com.example.javaDemoPopular_bot.service.BusStopService;
import com.example.javaDemoPopular_bot.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Component
@Slf4j
public class MessageHandler {
    private static final Set<Long> usersId = new HashSet<>();
    private static final long adminId = 123456789;
    private static final Set<String> passwords = LimitUsers.generatePassword(5);

    @Autowired private BusStopService busStopService;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    public void handleUpdate(Update update, TelegramLongPollingBot bot){
        if(update.hasMessage()) {
            Message message = update.getMessage();
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();

            if (message.hasLocation()){
                // Код отвечающий за поиск ближающей остановки
                Location location = message.getLocation();
                log.info("Получена локация: " + location.getLatitude() + ", " + location.getLongitude());
                BusStop busStop = busStopService.findNearestStop(location.getLongitude(), location.getLatitude()).
                        orElseThrow(() -> new RuntimeException("Stop not found"));
                BotUtils.sendMsg(chatId, String.format("Остановка \"%s\"", busStop.getName()), bot);
                BotUtils.sendMsg(BusStopInlineKeyboardFind.favoriteBusStopInlineKeyboard(chatId, busStop.getStopId()), bot);

            } else if(message.hasText() && passwords.contains(message.getText())){
                passwords.remove(message.getText());
                userService.saveNewUser(new User(userId));
                log.info("Пользователь с id - {}, воспользовался паролем", userId);
                BotUtils.sendMsg(chatId, "Вы получили доступ к этому боту", bot);
            } else if(message.hasText() && userService.existsByUserId(userId)){
                String messText = update.getMessage().getText();
                switch (messText){
                    case "/start":
                        BotUtils.startCommandToTalk(chatId, update.getMessage().getChat().getUserName(), bot);
                        break;

                    case "Избранные остановки": // /favorite

                        InlineKeyboardMarkup keyboardMarkup =  BusStopInlineKeyboardFavorite.buildPaginatedKeyboard(userService.findBusStopsByUserId(userId), 5, 10);
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        sendMessage.setText("Ваши избранные остановки:");

                        sendMessage.setReplyMarkup(keyboardMarkup);
                        BotUtils.sendMsg(sendMessage, bot);
                        break;

                    case "Ближающая остановка": // /nearby

                        BotUtils.sendMsg(requestLocation(chatId), bot);
                        break;

                    case "Найти остановку": // /find

                        BotUtils.sendMsg(chatId, "Чтобы найти нужную остановку введите команду \"/find Название остановки\"", bot);
                        break;

                    default:

                        if(messText.length() > 6 && messText.startsWith("/find")) {
                            String query = messText.substring("/find ".length() - 1);
                            log.info(query);
                            Pageable pageable = PageRequest.of(0, 10);
                            BotUtils.sendMsg(BusStopInlineKeyboardFind.busStopInlineKeyboardAb(chatId, busStopService.search(query, pageable), query), bot);
                        } else {
                            BotUtils.sendMsg(chatId, messText, bot);
                        }
                }
            } else{

                log.info("Пользоавтель с id - {}, пытается воспользоваться вашим ботом", Long.toString(userId));
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(chatId);
                sendMessage.setText("Приложение на данный момент находится в бета-тестировании");
                BotUtils.sendMsg(sendMessage, bot);

            }
        } else if (update.hasCallbackQuery()){
            handleCallback(update.getCallbackQuery(), bot);
        }
    }
    private void handleCallback(CallbackQuery callbackQuery, TelegramLongPollingBot bot){
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        long userId = callbackQuery.getFrom().getId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        if(data.startsWith("favorite")){
            // favorite:1234
            long stopId = Long.parseLong(data.split(":")[1]);
            handlerFavorite(chatId, callbackQuery.getFrom().getId(), stopId, bot);

        } else if (data.startsWith("page")) {
            // page:1234:Остановка
            String[] parts = data.split(":");
            if(parts.length == 3) {
                String query = parts.length == 3 ? parts[2] : "";
                Pageable pageable = PageRequest.of(Integer.parseInt(parts[1]), 10);
                EditMessageText editMessage = BusStopInlineKeyboardFind.busStopInlineKeyboardAbEdit(chatId, messageId, busStopService.search(query, pageable), query);
                BotUtils.sendMsg(editMessage, bot);
            } else {
                int newPage = Integer.parseInt(parts[1]);
                InlineKeyboardMarkup inlineKeyboardMarkup = BusStopInlineKeyboardFavorite.buildPaginatedKeyboard(userService.findBusStopsByUserId(userId), newPage, 10);
                EditMessageText edit = EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .replyMarkup(inlineKeyboardMarkup)
                        .build();
                edit.setReplyMarkup(inlineKeyboardMarkup);
                BotUtils.sendMsg(edit, bot);
            }
        } else if(data.startsWith("delete")){

            long stopId = Long.parseLong(data.split(":")[1]);
            handlerDeleteFavorite(chatId, callbackQuery.getFrom().getId(), stopId, bot);

        } else if (data.startsWith("stop1")){
            // stop1:1234
            BusStopDoc busStopDoc = busStopService.findInElasticSearch(data.split(":")[1]);
            BotUtils.sendMsg(chatId, String.format("Остановка \"%s\"", busStopDoc.getName()), bot);
            BotUtils.sendMsg(BusStopInlineKeyboardFind.favoriteBusStopInlineKeyboard(chatId, busStopDoc.getStopId()), bot);
        } else if(data.startsWith("stop2")){
            BusStopDoc busStopDoc = busStopService.findInElasticSearch(data.split(":")[1]);
            BotUtils.sendMsg(chatId, String.format("Остановка \"%s\"", busStopDoc.getName()), bot);

            BotUtils.sendMsg(BusStopInlineKeyboardFavorite.favoriteBusStopInlineKeyboard(chatId, busStopDoc.getStopId()), bot);
        }
    }
    @Transactional
    public void handlerFavorite(long chatId, long userId, long stopId, TelegramLongPollingBot bot){
        BusStop busStop = busStopService.find(stopId);
        if(busStop == null){
            BotUtils.sendMsg(chatId, "Остановка не найдена", bot);
            return;
        }
        if(userRepository.existsByUserId(userId)){
            if(userService.containsInBusStops(userId, stopId)){
                BotUtils.sendMsg(chatId, "Остановка уже в избранном", bot);
            } else{
                userService.addNewFavoriteBusStop(userId, stopId);
                BotUtils.sendMsg(chatId, "Остановка успешно сохранена", bot);
            }
        } else {
            User user = new User();
            user.setUserId(userId);
            user.addBusStop(busStop);
            userRepository.save(user);
            BotUtils.sendMsg(chatId, "Остановка успешно сохранена", bot);
        }
    }

    @Transactional
    public void handlerDeleteFavorite(long chatId, long userId, long stopId, TelegramLongPollingBot bot){
        BusStop busStop = busStopService.find(stopId);
        if(busStop == null){
            BotUtils.sendMsg(chatId, "Остановка не найдена", bot);
            return;
        }
        userService.removeFavoriteBusStop(userId, stopId);
        BotUtils.sendMsg(chatId, "Остановка удалена с ибранного", bot);
    }

    public synchronized SendMessage requestLocation(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Пожалуйста, отправьте своё местоположение.");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        KeyboardButton locationButton = new KeyboardButton("Отправить местоположение");
        locationButton.setRequestLocation(true);

        KeyboardRow row = new KeyboardRow();
        row.add(locationButton);

        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);
        return message;
    }
}
