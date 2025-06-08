package com.example.database;

import com.example.bot_config.BotConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSource {
	private static HikariDataSource ds;

  static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(BotConfig.getServerUrl());
    config.setUsername(BotConfig.getUser());
    config.setPassword(BotConfig.getPassword());

    config.setMaximumPoolSize(40);  
    config.setMinimumIdle(20); 

		ds = new HikariDataSource(config);
  }

  private DataSource() {}

  public static HikariDataSource getInstance() {
    return ds;
  }
}
