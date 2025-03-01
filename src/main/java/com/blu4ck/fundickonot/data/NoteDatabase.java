package com.blu4ck.fundickonot.data;

import com.blu4ck.fundickonot.model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDatabase {

        public static List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM Note ORDER BY createdAt DESC"; // En yeni notlar önce gelsin

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("subFolder_id"),
                        rs.getTimestamp("createdAt").toLocalDateTime(),
                        rs.getString("imagePath")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Notlar getirilirken hata oluştu: " + e.getMessage());
        }
        return notes;
    }



    public static List<Note> getNotesBySubFolderId(int subFolderId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM Note WHERE subFolder_id = ? ORDER BY createdAt DESC";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, subFolderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("subFolder_id"),
                        rs.getTimestamp("createdAt").toLocalDateTime(),
                        rs.getString("imagePath")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Notlar getirilirken hata oluştu: " + e.getMessage());
        }
        return notes;
    }



    public static boolean deleteNote(int noteId) {
        String sql = "DELETE FROM Note WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Not silinirken hata oluştu: " + e.getMessage());
            return false;
        }
    }


    public static List<Note> searchNotes(String query) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM Note WHERE title LIKE ? OR content LIKE ? ORDER BY createdAt DESC";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + query + "%"); // Başlıkta ara
            pstmt.setString(2, "%" + query + "%"); // İçerikte ara
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getInt("subFolder_id"),
                        rs.getTimestamp("createdAt").toLocalDateTime(),
                        rs.getString("imagePath")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Not araması sırasında hata oluştu: " + e.getMessage());
        }
        return notes;
    }


    public static boolean addNote(String title, String content, int subFolderId, String imagePath) {
        String sql = "INSERT INTO Note (title, content, subFolder_id, imagePath) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, subFolderId);
            pstmt.setString(4, imagePath);

            pstmt.executeUpdate();
            return true; // Başarıyla eklendi
        } catch (SQLException e) {
            System.out.println("Not eklenirken hata oluştu: " + e.getMessage());
            return false; // Hata varsa eklenmediğini döndür
        }
    }


    public static boolean updateNote(Note note) {
        String sql = "UPDATE Note SET title = ?, content = ?, imagePath = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setString(3, note.getImagePath());
            pstmt.setInt(4, note.getId());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Not güncellenirken hata oluştu: " + e.getMessage());
            return false;
        }
    }


}
