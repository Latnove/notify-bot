package com.example.bot_config;

import java.util.HashSet;
import java.util.Set;

public class BotConfig {
	// token settings
	private static final String TOKEN = "";
	private final static Set<String> OLD_GIFT_IDS = new HashSet<>(Set.of("5170145012310081615", "5170233102089322756", "5170250947678437525", "5168103777563050263", "5170144170496491616", "5170314324215857265", "5170564780938756245", "5168043875654172773", "5170690322832818290", "5170521118301225164", "6028601630662853006", "5782984811920491178"));
	// db settings
	private static final String SERVER_URL = "jdbc:postgresql://ip:port/db-name";
  private static final String USER = "";
  private static final String PASSWORD = "";
	
	public static String getToken() {
		return TOKEN;
	}

	public static Set<String> getOldGiftIds() {
		return OLD_GIFT_IDS;
	}

	public static String getServerUrl() {
		return SERVER_URL;
	}

	public static String getUser() {
		return USER;
	}

	public static String getPassword() {
		return PASSWORD;
	}
}
