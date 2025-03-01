package com.blu4ck.fundickonot.model;

import java.time.LocalDateTime;

public class Note {
    private int id;               // Benzersiz kimlik
    private String title;         // Not başlığı
    private String content;       // Not içeriği
    private int subFolderId;      // Bağlı olduğu alt klasör
    private LocalDateTime createdAt; // Notun oluşturulma zamanı
    private String imagePath;     // Notun içerdiği resim dosyası (opsiyonel)

    // Constructor
    public Note(int id, String title, String content, int subFolderId, LocalDateTime createdAt, String imagePath) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.subFolderId = subFolderId;
        this.createdAt = createdAt;
        this.imagePath = imagePath;
    }

    // Getter ve Setter Metodları
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public int getSubFolderId() { return subFolderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getImagePath() { return imagePath; }

    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setSubFolderId(int subFolderId) { this.subFolderId = subFolderId; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
