package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.data.NoteDatabase;
import com.blu4ck.fundickonot.model.Note;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;

public class EditController {

    @FXML private TextField noteTitleField;
    @FXML private TextArea noteContentArea;
    @FXML private Button uploadImageButton;
    @FXML private Label imagePathLabel;

    private Note currentNote;
    private String updatedImagePath;

    public void setNote(Note note) {
        this.currentNote = note;

        // Alanlara mevcut bilgileri yükle
        noteTitleField.setText(note.getTitle());
        noteContentArea.setText(note.getContent());
        updatedImagePath = note.getImagePath();

        if (updatedImagePath != null && !updatedImagePath.isEmpty()) {
            imagePathLabel.setText(updatedImagePath);
            uploadImageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
    }

    @FXML
    void handleUpdateNote() {
        String title = noteTitleField.getText().trim();
        String content = noteContentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Başlık ve içerik boş olamaz!");
            return;
        }

        // Note nesnesini güncelle
        currentNote.setTitle(title);
        currentNote.setContent(content);
        currentNote.setImagePath(updatedImagePath);
        currentNote.setCreatedAt(LocalDateTime.now()); // isteğe bağlı güncelleme zamanı

        boolean success = NoteDatabase.updateNote(currentNote);

        if (success) {
            closeWindow();
        } else {
            showAlert("Not güncellenemedi.");
        }
    }

    @FXML
    void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Görsel Seç");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(noteTitleField.getScene().getWindow());
        if (file != null) {
            updatedImagePath = file.getAbsolutePath();
            imagePathLabel.setText(updatedImagePath);
            uploadImageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
    }

    @FXML
    void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) noteTitleField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
