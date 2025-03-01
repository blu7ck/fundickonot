package com.blu4ck.fundickonot.model;

public class SubFolder {
    private int id;         // Unique ID
    private String name;    // Harf (A, B, C gibi)
    private int folderId;   // Ana klasörün ID'si

    // **ID'yi otomatik atamak için ikinci bir constructor ekledik**
    public SubFolder(String name, int folderId) {
        this.id = 0; // ID veritabanında otomatik atanacaksa 0 yapabiliriz.
        this.name = name;
        this.folderId = folderId;
    }

    public SubFolder(int id, String name, int folderId) {
        this.id = id;
        this.name = name;
        this.folderId = folderId;
    }

    // Getter ve Setter Metodları
    public int getId() { return id; }
    public String getName() { return name; }
    public int getFolderId() { return folderId; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setFolderId(int folderId) { this.folderId = folderId; }

    // **ComboBox gibi bileşenlerde SubFolder'ın adını düzgün göstermek için**
    @Override
    public String toString() {
        return name;
    }
}
