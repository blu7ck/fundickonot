package com.blu4ck.fundickonot.model;

import java.io.File;

public class Note {
    private String title;
    private String content;
    private String imagePath;
    private String folderName;
    private final File selectedImageFile;

    public Note(String title, String content, String imagePath, String folderName, File selectedImageFile) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.folderName = folderName;
        this.selectedImageFile = selectedImageFile;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getImagePath() { return imagePath; }
    public String getFolderName() { return folderName; }

    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setFolderName(String folderName) { this.folderName = folderName; }
}
