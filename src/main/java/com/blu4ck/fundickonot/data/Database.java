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
        String createFoldersTable = """
            CREATE TABLE Folder (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE\s
           );
       \s""";

        String createSubFoldersTable = """
            CREATE TABLE SubFolder (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            folder_id INTEGER NOT NULL,
            FOREIGN KEY (folder_id) REFERENCES Folder(id),
            UNIQUE(name, folder_id)\s
            );
       \s""";

        String createNotesTable = """
            CREATE TABLE Note (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                subFolder_id INTEGER NOT NULL,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                imagePath TEXT,
                FOREIGN KEY (subFolder_id) REFERENCES SubFolder(id) ON DELETE CASCADE
            );
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createFoldersTable);
            stmt.execute(createSubFoldersTable);
            stmt.execute(createNotesTable);
            System.out.println("✅ Tablolar başarıyla oluşturuldu.");
        } catch (SQLException e) {
            System.err.println("❌ Tablolar oluşturulurken hata: " + e.getMessage());
        }
    }


}
