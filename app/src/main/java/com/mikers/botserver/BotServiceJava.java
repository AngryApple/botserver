package com.mikers.botserver;

import android.content.Intent;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class BotServiceJava {

    private static BotServiceJava instance;
    private final TelegramBot bot = new TelegramBot("botToken");

    public static BotServiceJava getInstance() {
        if(instance == null) {
            instance = new BotServiceJava();
        }
        return instance;
    }

    public void start(IBroadcastSender broadcastSender) {
        bot.setUpdatesListener(updates -> {
            try {
                Message message = updates.get(0).message();
                Long chatId = message.chat().id();
                String text = message.text();

                // send broadcast message
                Intent intent = new Intent("com.mikers.botserver.DATA");
                intent.putExtra("text",  getMessageTime(message.date()) + ": " + text);
                broadcastSender.sendBroadcastFunc(intent);

                bot.execute(new SendMessage(chatId, "Ваш текст: " + text));

                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            } catch (Exception ex) {
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }

    public void stop() {
        bot.removeGetUpdatesListener();
    }

    private String getMessageTime(Integer messageTime) {
        Date date = new Date(messageTime * 1000);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

}