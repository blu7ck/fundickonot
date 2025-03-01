package com.blu4ck.fundickonot.model;

import java.util.List;

public class Folder {
    private int id;               // Benzersiz Folder ID
    private String name;          // Klasör Adı
    private List<SubFolder> subFolders; // Alt klasör listesi

    // Constructor
    public Folder(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter ve Setter Metodları
    public int getId() { return id; }
    public String getName() { return name; }
    public List<SubFolder> getSubFolders() { return subFolders; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSubFolders(List<SubFolder> subFolders) { this.subFolders = subFolders; }

    @Override
    public String toString() {
        return name;
    }

}
