package com.example.database;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataBase {

	public static void setUsers(long chatId, List<String> list) {
		try (Connection conn = DataSource.getInstance().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO chats (chat_id, users) VALUES (?, ?) " + "ON CONFLICT (chat_id) DO UPDATE SET users = EXCLUDED.users")) {
        pstmt.setLong(1, chatId);
        pstmt.setArray(2, conn.createArrayOf("text", list.toArray(new String[0])));
        pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void removeUser(long chatId, String user) {
		try (Connection conn = DataSource.getInstance().getConnection()) {
			String[] users;
    	try (PreparedStatement pstmt = conn.prepareStatement("SELECT users FROM chats WHERE chat_id = ?")) {
        pstmt.setLong(1, chatId);
        try (ResultSet rs = pstmt.executeQuery()) {
          if (rs.next()) {
            Array array = rs.getArray("users");
            users = (String[]) array.getArray();
          } else {
            users = new String[0];
          }
      	}
    	}

			List<String> newUsers = new ArrayList<>(Arrays.asList(users));
      if (newUsers.contains(user)) {
        newUsers.remove(user);
      }

			setUsers(chatId, newUsers);
      
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setCycles(long chatId, int cycles) {
		try (Connection conn = DataSource.getInstance().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO chats (chat_id, cycles) VALUES (?, ?) " + "ON CONFLICT (chat_id) DO UPDATE SET cycles = EXCLUDED.cycles")) {
        pstmt.setLong(1, chatId);
        pstmt.setInt(2, cycles);
        pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setIsWait(long chatId, boolean isWait) {
		try (Connection conn = DataSource.getInstance().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("INSERT INTO chats (chat_id, is_wait) VALUES (?, ?) " + "ON CONFLICT (chat_id) DO UPDATE SET is_wait = EXCLUDED.is_wait")) {
      pstmt.setLong(1, chatId); 
      pstmt.setBoolean(2, isWait);
      pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean isFilled(long chatId) {
    try (Connection conn = DataSource.getInstance().getConnection();
      PreparedStatement pstmt = conn.prepareStatement("SELECT users, cycles FROM chats WHERE chat_id = ?")) {
      pstmt.setLong(1, chatId);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          Array usersArray = rs.getArray("users");
          String[] users = usersArray != null ? (String[]) usersArray.getArray() : new String[0];
          int cycles = rs.getInt("cycles");
          return users.length > 0 && cycles > 0;
        }
      }
    	} catch (SQLException e) {
        e.printStackTrace();
    	}
    return false;
	}

	public static Set<Long> keySet() {
		Set<Long> res = new HashSet<>();

		try (Connection conn = DataSource.getInstance().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM chats")) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				long chatId = rs.getLong("chat_id");
				res.add(chatId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return res;
	}

	public static int getCycles(long chatId) {
		try (Connection conn = DataSource.getInstance().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT cycles FROM chats WHERE chat_id = ?")) {
			pstmt.setLong(1, chatId);
			try (ResultSet rs = pstmt.executeQuery()) {
          if (rs.next()) {
            return rs.getInt("cycles");
          }
        }
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	public static boolean getIsWait(Long chatId) {
		try (Connection conn = DataSource.getInstance().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT is_wait FROM chats WHERE chat_id = ?")) {
				pstmt.setLong(1, chatId);
				try (ResultSet rs = pstmt.executeQuery()) {
          if (rs.next()) {
            return rs.getBoolean("is_wait");
          }
        }
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static List<String> getUsers(Long chatId) {
		try (Connection conn = DataSource.getInstance().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("SELECT users FROM chats WHERE chat_id = ?")) {
				pstmt.setLong(1, chatId);
				try (ResultSet rs = pstmt.executeQuery()) {
          if (rs.next()) {
            String[] arr = (String[]) rs.getArray("users").getArray();

						return new ArrayList<>(List.of(arr));
          }
        }
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new ArrayList<>();
	}
}
