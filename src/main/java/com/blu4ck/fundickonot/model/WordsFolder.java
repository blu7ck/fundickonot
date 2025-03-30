package com.blu4ck.fundickonot.model;

import java.util.List;

public class WordsFolder {
    private int id;               // Benzersiz Folder ID
    private String name;          // Klasör Adı

    // Constructor
    public WordsFolder(int id, String name) {
        this.id = id;
        this.name = name;
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
