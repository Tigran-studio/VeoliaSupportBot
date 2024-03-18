package com.tigran.veoliasupportbot.service;

import com.tigran.veoliasupportbot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
@Slf4j
@Component
public class Parser {
    String url = "https://interactive.vjur.am//";
    public List<String>  toParse(String url) throws IOException {
        List<String> listOfArticle = new ArrayList<>();
        Document page = Jsoup.parse(new URL(url), 3000);
        Elements infoAboutDateAndPlace = page.select("span[style=color:blue]");
        for(Element element: infoAboutDateAndPlace)
            listOfArticle.add(element.text());
        return listOfArticle;
    }
    public  void toTimer() throws IOException {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    toParse(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 3600000);
    }
    public void getReadyToMessage(String messageText, Update update, BotConfig config, List<String> listOfSendMessages) throws IOException {
        toTimer();
        TelegramBot telegramBot = new TelegramBot(config);
        if(update.hasMessage() && update.getMessage().hasText())
        {
            long chatId = update.getMessage().getChatId();
            if(messageText.equals("/start"))
            {
                telegramBot.startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            }
            try {
                for (String text : toParse(url))
                {
                    String[] words = text.split("\\s+");
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < Math.min(5, words.length); i++) {
                        result.append(words[i]).append(" ");
                    }
                    if (text.contains(messageText)&&!listOfSendMessages.contains(messageText)) {
                        telegramBot.sendMessage(chatId, messageText + " ջրամատակարարումը կդադարեցվի "+ result.toString().trim());
                        listOfSendMessages.add(messageText);
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}