package com.blu4ck.fundickonot;

public class Note {
    private int id;
    private int folderId;
    private String type;
    private String title;
    private String content;
    private String explanation;
    private String category;
    private String createdAt;
    private String updatedAt;

    public Note(int id, int folderId, String type, String title, String content, String explanation, String category, String createdAt, String updatedAt) {
        this.id = id;
        this.folderId = folderId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.explanation = explanation;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() { return id; }
    public int getFolderId() { return folderId; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getExplanation() { return explanation; }
    public String getCategory() { return category; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "[" + type + "] " + title + ": " + content;
    }
}
