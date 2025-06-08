package com.example;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import com.example.bot_config.BotConfig;

public class Main {
    public static void main(String[] args) {
        try(TelegramBotsLongPollingApplication telegramApp = new TelegramBotsLongPollingApplication()) {
            telegramApp.registerBot(BotConfig.getToken(), new NotifyGiftsBot());
            System.out.println("бот запустился");
            NewGiftsChecker.startMonitoring(new OkHttpTelegramClient(BotConfig.getToken()), 2000);
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}