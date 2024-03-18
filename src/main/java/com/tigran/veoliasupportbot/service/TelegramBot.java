package com.tigran.veoliasupportbot.service;

import com.tigran.veoliasupportbot.config.BotConfig;
import com.tigran.veoliasupportbot.config.BotInitializer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot
{

    List<String> listOfSendMessages = new ArrayList<>();
    final BotConfig config;
    public TelegramBot(BotConfig config)  { this.config = config; }
    @Override
    public String getBotToken() { return config.getToken(); }
    @Override
    public String getBotUsername() { return config.getBotName(); }
    @Override
    public void onUpdateReceived(Update update) {
        Parser parser = new Parser();
        String messageText = update.getMessage().getText();
        try {
            parser.getReadyToMessage(messageText, update, config, listOfSendMessages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void startCommandReceived(long chatId, String name)  {
        String answer = "բարև "+ name + ", դուք միացել եք VeoliaSupportBot-ին," +
                " խնդրում ենք մուտքագրեք ձեր բնակության հասցեն և ջրամատակարարման դադարեցման մասին կտեղեկացվեք, " +
                "եթե դա տեղի ունենա ձեր տարածքում ";
        log.info("Replied to user "+ name);
        sendMessage(chatId, answer);
    }
    public void sendMessage(long chatId, String textToSend)  {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        }
        catch (TelegramApiException e)
        {
            log.error("Error occurred: "+ e.getMessage());
        }
    }
}
















