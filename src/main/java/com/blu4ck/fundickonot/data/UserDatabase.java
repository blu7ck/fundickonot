package com.blu4ck.fundickonot.data;

import com.blu4ck.fundickonot.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDatabase {

    public static void createUsersTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS users (
            user_id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL
        );
    """;

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Users tablosu başarıyla oluşturuldu.");
        } catch (SQLException e) {
            System.err.println("❌ Users tablosu oluşturulurken hata: " + e.getMessage());
        }
    }

    public static void ensureDefaultUser() {
        String checkSql = "SELECT 1 FROM users WHERE user_id = ?";
        String insertSql = "INSERT INTO users (user_id, username, password) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, 1);  // Varsayılan kullanıcı ID'si 1

            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                // Kullanıcı yoksa ekle
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, 1);  // Sabit user_id
                    insertStmt.setString(2, "Funda");  // Varsayılan kullanıcı adı
                    insertStmt.setString(3, "Erdurmuş");  // Varsayılan şifre
                    insertStmt.executeUpdate();
                    System.out.println("✅ Varsayılan kullanıcı eklendi.");
                }
            } else {
                System.out.println("ℹ️ Varsayılan kullanıcı zaten mevcut.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Kullanıcı kontrolünde hata: " + e.getMessage());
        }
    }


    public static boolean authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // Sonuç varsa giriş başarılı

        } catch (SQLException e) {
            System.err.println("❌ Girişte hata: " + e.getMessage());
            return false;
        }
    }


    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password")
                ));
            }

        } catch (SQLException e) {
            System.out.println("🚫 Kullanıcılar listelenemedi: " + e.getMessage());
        }
        return users;
    }

    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("🚫 Kullanıcı silinemedi: " + e.getMessage());
            return false;
        }
    }
}
