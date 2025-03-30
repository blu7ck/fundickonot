package com.blu4ck.fundickonot.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:fundickonot.db";

    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            System.out.println("✅ Veritabanına bağlantı kuruldu.");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Veritabanına bağlanılamadı: " + e.getMessage());
            return null;
        }
    }

    public static void initializeDatabase() {
        String createWordsFoldersTable = """
        CREATE TABLE IF NOT EXISTS WordsFolder (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE
        );
    """;

        String createNotesFoldersTable = """
        CREATE TABLE IF NOT EXISTS NotesFolder (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE
        );
    """;

        String createNotesTable = """
        CREATE TABLE IF NOT EXISTS Note (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            content TEXT NOT NULL,
            imagePath TEXT,
            folderType TEXT NOT NULL CHECK(folderType IN ('notes', 'words')),
            createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createWordsFoldersTable);
            stmt.execute(createNotesFoldersTable);
            stmt.execute(createNotesTable);
            System.out.println("✅ Veritabanı tabloları başarıyla oluşturuldu.");
        } catch (SQLException e) {
            System.err.println("❌ Veritabanı tabloları oluşturulurken hata: " + e.getMessage());
        }
    }
}
