package com.blu4ck.fundickonot.data;

import com.blu4ck.fundickonot.model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDatabase {

    public static boolean addNote(String title, String content, String imagePath, String folderType) {
        String sql = "INSERT INTO Note (title, content, imagePath, folderType, createdAt) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setString(3, imagePath);
            pstmt.setString(4, folderType);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Not eklenirken hata oluştu: " + e.getMessage());
            return false;
        }
    }

    public static List<Note> getAllNotes(String folderType) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM Note WHERE folderType = ? ORDER BY createdAt DESC";

        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, folderType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notes.add(mapNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Notlar getirilirken hata oluştu: " + e.getMessage());
        }
        return notes;
    }

    public static List<Note> searchNotes(String query, String folderType) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM Note WHERE folderType = ? AND (title LIKE ? OR content LIKE ?) ORDER BY createdAt DESC";

        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, folderType);
            pstmt.setString(2, "%" + query + "%");
            pstmt.setString(3, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notes.add(mapNoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Not araması sırasında hata oluştu: " + e.getMessage());
        }
        return notes;
    }

    private static Note mapNoteFromResultSet(ResultSet rs) throws SQLException {
        return new Note(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("imagePath"),
                rs.getTimestamp("createdAt").toLocalDateTime(),
                rs.getString("folderType")
        );
    }
    public static boolean updateNote(Note note) {
        String sql = "UPDATE Note SET title = ?, content = ?, imagePath = ?, folderType = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setString(3, note.getImagePath());
            pstmt.setString(4, note.getFolderType());
            pstmt.setInt(5, note.getId());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Not güncellenirken hata oluştu: " + e.getMessage());
            return false;
        }
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

}