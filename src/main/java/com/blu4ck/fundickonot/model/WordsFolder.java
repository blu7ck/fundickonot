package com.blu4ck.fundickonot.model;

public class WordsFolder {
    private int id;               // Benzersiz Folder ID
    private String name;          // Klasör Adı
    private OttomanLetterCategory category;

    // Constructor
    public WordsFolder(int id, String name, OttomanLetterCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public OttomanLetterCategory getCategory() {
        return category;
    }

    public void setCategory(OttomanLetterCategory category) {
        this.category = category;
    }

    // Getter ve Setter Metodları
    public int getId() { return id; }
    public String getName() { return name; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return name;
    }

}
