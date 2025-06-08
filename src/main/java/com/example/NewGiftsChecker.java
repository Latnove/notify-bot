package com.example;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.telegram.telegrambots.meta.api.methods.gifts.GetAvailableGifts;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.gifts.Gift;
import org.telegram.telegrambots.meta.api.objects.gifts.Gifts;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import com.example.bot_config.BotConfig;
import com.example.database.DataBase;

public class NewGiftsChecker {
	public static void startMonitoring(TelegramClient telegramClient, long intervalSeconds) {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.scheduleAtFixedRate(() -> {
      try {
        Gifts gifts = telegramClient.execute(new GetAvailableGifts());
				Set<String> oldGiftIds = BotConfig.getOldGiftIds();
				Set<Gift> newGifts = new HashSet<>();

        for (Gift gift : gifts.getGifts()) {
          if (!oldGiftIds.contains(String.valueOf(gift.getId())) && !newGifts.contains(gift) && gift.getTotalCount() != null) {
						newGifts.add(gift);
					}
        }

				if (!newGifts.isEmpty()) {
					StringBuilder stringBuilder = new StringBuilder();
					for (Gift gift : newGifts) {
						stringBuilder.append(gift.getSticker().getEmoji())
							.append(" -> ").append(gift.getTotalCount())
							.append(" шт., ").append(gift.getStarCount())
							.append("🌟").append("\n");
					}

					stringBuilder.append("\n");

					for (long chatId : DataBase.keySet()) {
						if (DataBase.getIsWait(chatId) && DataBase.isFilled(chatId)) {
							int cycles = DataBase.getCycles(chatId);
							DataBase.setCycles(chatId, --cycles);

							SendMessage sendMessage = SendMessage.builder()
								.chatId(chatId)
								.text(stringBuilder.toString() + String.join(" ", DataBase.getUsers(chatId)) + "\n\nОсталось уведомлений: " + cycles)
								.replyMarkup(InlineKeyboardMarkup
									.builder()
									.keyboardRow(new InlineKeyboardRow(InlineKeyboardButton.builder()
										.text("Удалить меня")
										.callbackData("remove_me")
										.build()))
									.build())
								.build();

							try {
								telegramClient.execute(sendMessage);
							} catch (TelegramApiException e) {
								e.printStackTrace();
							}
						} else {
							DataBase.setIsWait(chatId, false);
						}
					}
				}
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
    }, 0, intervalSeconds, TimeUnit.MILLISECONDS);
  }
}
