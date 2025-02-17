package com.blu4ck.fundickonot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:notes.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void createTables() {
        String createFolders = "CREATE TABLE IF NOT EXISTS folders ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ");";

        String createNotes = "CREATE TABLE IF NOT EXISTS notes ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "folder_id INTEGER NOT NULL, "
                + "type TEXT NOT NULL, "
                + "title TEXT, "
                + "content TEXT, "
                + "explanation TEXT, "
                + "category TEXT, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(folder_id) REFERENCES folders(id)"
                + ");";

        String createCategories = "CREATE TABLE IF NOT EXISTS categories ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createFolders);
            stmt.execute(createNotes);
            stmt.execute(createCategories);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Folder CRUD
    public static void insertFolder(String name) {
        String sql = "INSERT INTO folders(name) VALUES(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Folder> getAllFolders() {
        List<Folder> folders = new ArrayList<>();
        String sql = "SELECT id, name, created_at FROM folders";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Folder folder = new Folder(rs.getInt("id"), rs.getString("name"), rs.getString("created_at"));
                folders.add(folder);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folders;
    }

    public static void deleteFolder(int folderId) {
        String sql = "DELETE FROM folders WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, folderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Note CRUD
    public static void insertNote(int folderId, String type, String title, String content, String explanation, String category) {
        String sql = "INSERT INTO notes(folder_id, type, title, content, explanation, category) VALUES(?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, folderId);
            pstmt.setString(2, type);
            pstmt.setString(3, title);
            pstmt.setString(4, content);
            pstmt.setString(5, explanation);
            pstmt.setString(6, category);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Note> getNotesByFolder(int folderId) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE folder_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, folderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Note note = new Note(
                        rs.getInt("id"),
                        rs.getInt("folder_id"),
                        rs.getString("type"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("explanation"),
                        rs.getString("category"),
                        rs.getString("created_at"),
                        rs.getString("updated_at")
                );
                notes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    public static void updateNote(int noteId, String newTitle, String newContent, String newExplanation, String newCategory) {
        String sql = "UPDATE notes SET title = ?, content = ?, explanation = ?, category = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newContent);
            pstmt.setString(3, newExplanation);
            pstmt.setString(4, newCategory);
            pstmt.setInt(5, noteId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteNote(int noteId) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, noteId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Category CRUD
    public static void insertCategory(String name) {
        String sql = "INSERT INTO categories(name) VALUES(?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name, created_at FROM categories";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                Category cat = new Category(rs.getInt("id"), rs.getString("name"), rs.getString("created_at"));
                categories.add(cat);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public static void updateCategory(int id, String newName) {
        String sql = "UPDATE categories SET name = ?, created_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }

    }
}
