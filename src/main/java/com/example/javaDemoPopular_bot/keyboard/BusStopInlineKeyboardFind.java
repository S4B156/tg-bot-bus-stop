package com.example.javaDemoPopular_bot.keyboard;

import com.example.javaDemoPopular_bot.entite.BusStop;
import com.example.javaDemoPopular_bot.entite.BusStopDoc;
import com.example.javaDemoPopular_bot.settings.BusChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class BusStopInlineKeyboardFind {
    public static SendMessage busStopInlineKeyboardAb(long chatId, Page<BusStopDoc> busStopPage, String query) {
        log.debug("Создание клавиатуры для страницы: number={}, totalElements={}, query={}",
                busStopPage.getNumber(), busStopPage.getTotalElements(), query);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите нужную остановку");

        InlineKeyboardMarkup markup = buildInlineKeyboard(busStopPage, query);
        sendMessage.setReplyMarkup(markup);
        return sendMessage;
    }

    public static EditMessageText busStopInlineKeyboardAbEdit(long chatId, int messageId, Page<BusStopDoc> busStopPage, String query) {
        log.debug("Редактирование клавиатуры для страницы: number={}, totalElements={}, query={}",
                busStopPage.getNumber(), busStopPage.getTotalElements(), query);

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText("Выберите нужную остановку");

        InlineKeyboardMarkup markup = buildInlineKeyboard(busStopPage, query);
        editMessage.setReplyMarkup(markup);
        return editMessage;
    }

    private static InlineKeyboardMarkup buildInlineKeyboard(Page<BusStopDoc> busStopPage, String query) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (BusStopDoc stop : busStopPage.getContent()) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(stop.getName())
                    .callbackData("stop1:" + stop.getStopId())
                    .build();
            keyboard.add(List.of(button));
        }

        List<InlineKeyboardButton> navigation = new ArrayList<>();
        int currentPage = busStopPage.getNumber() + 1;
        int totalPages = busStopPage.getTotalPages();

        if (busStopPage.hasPrevious()) {
            navigation.add(InlineKeyboardButton.builder()
                    .text("<")
                    .callbackData("page:" + (busStopPage.getNumber() - 1) + ":" + query)
                    .build());
        }

        navigation.add(InlineKeyboardButton.builder()
                .text(currentPage + "/" + totalPages)
                .callbackData("noop")
                .build());

        if (busStopPage.hasNext()) {
            navigation.add(InlineKeyboardButton.builder()
                    .text(">")
                    .callbackData("page:" + (busStopPage.getNumber() + 1) + ":" + query)
                    .build());
        }

        if (!navigation.isEmpty()) {
            keyboard.add(navigation);
        }

        markup.setKeyboard(keyboard);
        return markup;
    }

    public static SendMessage favoriteBusStopInlineKeyboard(long chatId, Long stopId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(BusChecker.getInfoBus(String.valueOf(stopId)));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Добавить в избранное")
                .callbackData("favorite:" + stopId)
                .build();
        keyboard.add(List.of(button));

        markup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(markup);
        return sendMessage;
    }
}
