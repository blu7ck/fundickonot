package com.blu4ck.fundickonot.data;

import com.blu4ck.fundickonot.model.Folder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FolderDatabase {


    public static List<Folder> getAllFolders() {
        List<Folder> folders = new ArrayList<>();
        String sql = "SELECT * FROM Folder";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                folders.add(new Folder(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Klasörler getirilirken hata oluştu: " + e.getMessage());
        }
        return folders;
    }


    public static void deleteFolder ( int folderId){
            String sql = "DELETE FROM Folder WHERE id = ?";

            try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, folderId);
                pstmt.executeUpdate();
                System.out.println("🗑️ Klasör silindi (ID: " + folderId + ")");
            } catch (SQLException e) {
                System.err.println("❌ Klasör silinemedi: " + e.getMessage());
            }
        }

    public static int addFolder(String name) {
        String sql = "INSERT INTO Folder (name) VALUES (?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // ✅ Yeni oluşturulan klasörün ID’sini döndür
                }
            }
        } catch (SQLException e) {
            System.out.println("Klasör eklenirken hata oluştu: " + e.getMessage());
        }

        return -1; // ❌ Başarısız olursa -1 döndür
    }


}

