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
public class BusStopInlineKeyboardFavorite {
    public static InlineKeyboardMarkup buildPaginatedKeyboard(List<BusStop> objects, int page, int itemsPerPage) {
        int totalItems = objects.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        page = Math.max(1, Math.min(page, totalPages));

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalItems);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = startIndex; i < endIndex; i++) {
            BusStop obj = objects.get(i);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(obj.getName())
                    .callbackData("stop2:" + obj.getStopId())
                    .build();
            keyboard.add(List.of(button));
        }

        List<InlineKeyboardButton> navigationRow = new ArrayList<>();

        InlineKeyboardButton prevButton = InlineKeyboardButton.builder()
                .text("<")
                .callbackData(page > 1 ? "page:" + (page - 1) : "noop")
                .build();

        InlineKeyboardButton pageButton = InlineKeyboardButton.builder()
                .text(page + "/" + totalPages)
                .callbackData("noop")
                .build();

        InlineKeyboardButton nextButton = InlineKeyboardButton.builder()
                .text(">")
                .callbackData(page < totalPages ? "page:" + (page + 1) : "noop")
                .build();

        navigationRow.add(prevButton);
        navigationRow.add(pageButton);
        navigationRow.add(nextButton);
        keyboard.add(navigationRow);

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
    public static SendMessage favoriteBusStopInlineKeyboard(long chatId, Long stopId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(BusChecker.getInfoBus(String.valueOf(stopId)));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Удалить с избранного")
                .callbackData("delete:" + stopId)
                .build();
        keyboard.add(List.of(button));

        markup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(markup);
        return sendMessage;
    }
}

