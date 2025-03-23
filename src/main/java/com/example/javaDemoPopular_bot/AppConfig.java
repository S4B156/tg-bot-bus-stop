package com.example.javaDemoPopular_bot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import com.example.javaDemoPopular_bot.settings.CommandsBot;

@Configuration
@ComponentScan("com.example.javaDemoPopular_bot")
public class AppConfig {

    @Bean
    public CommandsBot registration(){
        CommandsBot bot = new CommandsBot();
        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(bot);
            System.out.println("Бот @"+bot.getBotUsername()+" успешно запущен!!!");
        } catch (Exception e){
            e.printStackTrace();
        }
        return bot;
    }
}
