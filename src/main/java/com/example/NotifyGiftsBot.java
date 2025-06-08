package com.example;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import com.example.bot_config.BotConfig;
import com.example.database.DataBase;

public class NotifyGiftsBot implements LongPollingSingleThreadUpdateConsumer {
	private final TelegramClient telegramClient = new OkHttpTelegramClient(BotConfig.getToken());

	@Override
	public void consume(Update update) {
		if (update.hasMessage()) {
			String messageText = update.getMessage().getText();
			if (messageText == null) return;
			long chatId = update.getMessage().getChatId();
			if (messageText.split("\\s+")[0].equals("/users@notify_gifts_bot")) {
				setUsers(messageText, chatId);
			} else if (messageText.split("\\s+")[0].equals("/cycles@notify_gifts_bot")) {
				setCycles(messageText, chatId);
			} else if (messageText.contains("/start@notify_gifts_bot")) {
				start(chatId);
			} else if (messageText.contains("/stop@notify_gifts_bot")) {
				stop(chatId);
			} else if (messageText.contains("/commands@notify_gifts_bot")) {
				commands(chatId);
			}
		} else if (update.hasCallbackQuery()) {
			CallbackQuery callback = update.getCallbackQuery();
			if (callback.getData().equals("remove_me")) {
				DataBase.removeUser(callback.getMessage().getChatId(), "@" + callback.getFrom().getUserName());
				
			}
		}
	}

	private void setUsers(String messageText, long chatId) {
		List<String> users = getUsersList(messageText);
		String text;

		if (users.isEmpty()) {
			text = "Введите хотя бы одного юзера (через пробел) после команды /users@notify_gifts_bot";
		} else {
			DataBase.setUsers(chatId, users);
			text = "Вы успешно добавили юзернеймы пользователей для уведомления новых подарков";	
		}

		SendMessage sendMessage = SendMessage.builder()
				.text(text)
				.chatId(chatId)
				.build();

		sendResponse(sendMessage);
	}

	private List<String> getUsersList(String message) {
		List<String> list = new ArrayList<>();
		String[] arr = message.split("\\s+");
		for (String str : arr) {
			if (str.equals("/users@notify_gifts_bot") || str.equals("@notify_gifts_bot")) continue;
			if (str.startsWith("@")) list.add(str);
		}

		return list;
	}

	private void setCycles(String messageText, long chatId) {
		String text;
		try {
			if (messageText.split("\\s+").length != 2) throw new NumberFormatException();

			int cycles = Integer.parseInt(messageText.split("\\s+")[1]);

			if (cycles > 25 || cycles <= 0) throw new NumberFormatException();
			DataBase.setCycles(chatId, cycles);
			text = "Количество оповещений " + cycles + " успешно добавлено";
		} catch (NumberFormatException e) {
			text = "Введите корректное целое число, например: 1 - 25";
		}

		SendMessage sendMessage = SendMessage.builder()
			.text(text)
			.chatId(chatId)
			.build();
			
		sendResponse(sendMessage);
	}

	private void start(long chatId) {
		String text;
		if (DataBase.isFilled(chatId)) {
			DataBase.setIsWait(chatId, true);
			text = "Ожидание новых подарков успешно запущено";
		} else {
			if (DataBase.getCycles(chatId) == 0) {
				text = "Добавьте количество циклов через /cycles@notify_gifts_bot";
			} else {
				text = "Добавьте хотя бы одного пользователя через /users@notify_gifts_bot";
			}
		}

		SendMessage sendMessage = SendMessage.builder()
			.text(text)
			.chatId(chatId)
			.build();
			
		sendResponse(sendMessage);
	}

	private void stop(long chatId) {
		DataBase.setIsWait(chatId, false);

		SendMessage sendMessage = SendMessage.builder()
			.text("Вы остановили уведомления о новых подарках")
			.chatId(chatId)
			.build();
			
		sendResponse(sendMessage);
	}

	private void commands(long chatId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder
			.append("/users@notify_gifts_bot - через пробел юзерки тех, кого тегать при выходе\n")
			.append("/cycles@notify_gifts_bot - количество циклов (1-25)\n")
			.append("/start@notify_gifts_bot - запуск ожидания\n")
			.append("/stop@notify_gifts_bot - остановка");

		SendMessage sendMessage = SendMessage.builder()
			.text(stringBuilder.toString())
			.chatId(chatId)
			.build();
			
		sendResponse(sendMessage);
	}

	private void sendResponse(BotApiMethodMessage message) {
		try {
			telegramClient.execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}


}
