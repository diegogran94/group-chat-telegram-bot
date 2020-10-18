package org.telegram;

import java.util.concurrent.ThreadLocalRandom;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotActionsHandler extends TelegramLongPollingBot {
    
    @Override
    public void onUpdateReceived(Update update) {

    int randomNum = ThreadLocalRandom.current().nextInt(0, 1 + 1);
    
    if (update.hasMessage() && update.getMessage().hasText() && isOraculoCommand(update.getMessage().getText())) {
        SendMessage message = new SendMessage() 
                .setChatId(update.getMessage().getChatId())
                .setText(randomNum == 0 ? "Yes" : "No");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

    @Override
    public String getBotUsername() {
        return BotConfig.USERNAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.TOKEN;
    }

    private boolean isOraculoCommand(String message){
        return message.startsWith("/oraculo");
    }
}
