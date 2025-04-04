package com.blu4ck.fundickonot.model;

import java.time.LocalDateTime;

public class Note {
    private int id;
    private String title;
    private String content;
    private String imagePath;
    private LocalDateTime createdAt;
    private String folderType; // "notes" veya "words"
    private String category;   // Osmanlı harfi (sadece "words" için)

    public Note(int id, String title, String content, String imagePath, LocalDateTime createdAt, String folderType, String category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
        this.folderType = folderType;
        this.category = category;
    }

    // Getter & Setter
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImagePath() { return imagePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getFolderType() { return folderType; }
    public String getCategory() { return category; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setFolderType(String folderType) { this.folderType = folderType; }
    public void setCategory(String category) { this.category = category; }
}
