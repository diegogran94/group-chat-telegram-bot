package org.telegram;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotActionsHandler extends TelegramLongPollingBot {

    private static final String FILE_NAME = "res/file.txt";

    @Override
    public void onUpdateReceived(Update update) {
  
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageToSend = "";

            if (isOraculoCommand(update.getMessage().getText())) {
                messageToSend = getOraculoMessage(update.getMessage().getText());
            } else if(isAddText(update.getMessage().getText())) {
                messageToSend = addText(update.getMessage().getText());
            } else if(isGetText(update.getMessage().getText())){
                messageToSend = getText();
            } else {
                messageToSend = update.getMessage().getText();
            }
            sendMessage(update.getMessage().getChatId(), messageToSend);
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

    private void sendMessage(Long chatId, String messageToSend) {
        SendMessage message = new SendMessage(chatId, messageToSend);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isOraculoCommand(String message){
        return message.startsWith("/oraculo");
    }

    private String getOraculoMessage(String message) {
        int randomNum = getRandomNumber(0, 1 + 1);
        return randomNum == 0 ? "Yes" : "No";
    }

    private boolean isAddText(String message){
        return message.startsWith("/addText");
    }

    private String addText(String message){
        return storeText(getFilePath(), message);
    }

    private Path getFilePath() {

        Path filePath = Paths.get(FILE_NAME);
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }

    private String storeText(Path filePath, String message) {

        // Remove the command from the string /addText. First replace adds a whitespace, we remove it
        String messageToStore = message.replace("/addText","").replace(" ", "") + System.lineSeparator();

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {
            writer.write(messageToStore);
            return "Stored";
        } catch (IOException ioe) {
            System.err.format("IOException: %s%n", ioe);
            return "Error";
        }
    }

    private boolean isGetText(String message) {
        return message.startsWith("/getText");
    }

    private String getText() {
        try {
            List<String> allLines = Files.readAllLines(Paths.get(FILE_NAME));
            return allLines.get(getRandomNumber(0, allLines.size()));
        } catch (IOException e) {
            e.printStackTrace();
            return "File not found";
        }
    }

    private int getRandomNumber(int ini, int end) {
        return ThreadLocalRandom.current().nextInt(ini, end);
    }
}
