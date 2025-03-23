package com.example.javaDemoPopular_bot.settings;

import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    private final String botName = "bus_way_bot";
    private final String botToken = "key_from_bot_father";

    public String getBotName() {
        return botName;
    }

    public String getBotToken() {
        return botToken;
    }
}
