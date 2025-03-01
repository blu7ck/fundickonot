package com.blu4ck.fundickonot.data;

import com.blu4ck.fundickonot.model.SubFolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubFolderDatabase {

    public static List<SubFolder> getSubFolders(int folderId) {
        List<SubFolder> subFolders = new ArrayList<>();
        String sql = "SELECT * FROM subfolders WHERE folder_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, folderId);  // Use folderId directly
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subFolders.add(new SubFolder(
                        rs.getInt("id"),
                        rs.getString("name"),
                        folderId
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subFolders;
    }

//    public static boolean addSubFolder(String name, int folderId) {
//        String sql = "INSERT INTO SubFolder (name, folder_id) VALUES (?, ?)";
//
//        try (Connection conn = Database.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, name);
//            pstmt.setInt(2, folderId);
//
//            pstmt.executeUpdate();
//            return true; // Başarıyla eklendi
//        } catch (SQLException e) {
//            System.out.println("SubFolder eklenirken hata oluştu: " + e.getMessage());
//            return false; // Hata varsa eklenmediğini döndür
//        }
//    }


    public static List<SubFolder> getSubFoldersByFolderId(int folderId) {
        List<SubFolder> subFolders = new ArrayList<>();
        String sql = "SELECT * FROM SubFolder WHERE folder_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, folderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                subFolders.add(new SubFolder(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("folder_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Alt klasörler getirilirken hata oluştu: " + e.getMessage());
        }
        return subFolders;
    }


    public static void addSubFolder(String name, int folderId) {
        addSubFolder(new SubFolder(0, name, folderId));
    }

    public static boolean addSubFolder(SubFolder subFolder) {
        String checkSql = "SELECT COUNT(*) FROM SubFolder WHERE name = ? AND folder_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, subFolder.getName());
            checkStmt.setInt(2, subFolder.getFolderId());

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Bu klasör içinde zaten aynı isimde bir alt klasör var!");
                return false; // Alt klasör zaten varsa ekleme yapma
            }
        } catch (SQLException e) {
            System.out.println("Alt klasör kontrol edilirken hata oluştu: " + e.getMessage());
            return false;
        }

        // Eğer aynı isimde subfolder yoksa ekleme yap
        String sql = "INSERT INTO SubFolder (name, folder_id) VALUES (?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, subFolder.getName());
            pstmt.setInt(2, subFolder.getFolderId());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Alt klasör eklenirken hata oluştu: " + e.getMessage());
            return false;
        }
    }
}