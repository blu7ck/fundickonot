package com.blu4ck.fundickonot.data;

import com.blu4ck.fundickonot.model.WordsFolder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WordsFolderDatabase {

    public static List<WordsFolder> getAllFolders() {
        List<WordsFolder> folders = new ArrayList<>();
        String sql = "SELECT * FROM WordsFolder";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                folders.add(new WordsFolder(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            System.out.println("WordsFolder verileri alınamadı: " + e.getMessage());
        }

        return folders;
    }

    public static int addFolder(String name) {
        String sql = "INSERT INTO WordsFolder (name) VALUES (?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("WordsFolder eklenemedi: " + e.getMessage());
        }

        return -1;
    }

    public static void deleteFolder(int folderId) {
        String sql = "DELETE FROM WordsFolder WHERE id = ?";

        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, folderId);
            pstmt.executeUpdate();
            System.out.println("WordsFolder silindi (ID: " + folderId + ")");
        } catch (SQLException e) {
            System.err.println("WordsFolder silinemedi: " + e.getMessage());
        }
    }
}
