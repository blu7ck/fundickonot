package com.blu4ck.fundickonot.model;

public class NotesFolder {
    private int id;         // Unique ID
    private String name;    // Harf (A, B, C gibi)

    // **ID'yi otomatik atamak için ikinci bir constructor ekledik**
    public NotesFolder(int folderId, String name ) {
        this.id = 0; // ID veritabanında otomatik atanacaksa 0 yapabiliriz.
        this.name = name;
    }

    // Getter ve Setter Metodları
    public int getId() { return id; }
    public String getName() { return name; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }

}
