package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.model.Note;
import com.blu4ck.fundickonot.model.OttomanLetterCategory;
import com.blu4ck.fundickonot.remote.NoteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class EditController {
    @FXML private TextField noteTitleField;
    @FXML private TextArea noteContentArea;
    @FXML private Label imagePathLabel;
    @FXML private ComboBox<OttomanLetterCategory> letterCategoryComboBox;
    @FXML private Button uploadImageButton;

    private Note note;
    private String accessToken; // 🔐

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setNote(Note note) {
        this.note = note;

        System.out.println("📦 Güncellenmeye gelen notun ID'si: " + note.getId());
        noteTitleField.setText(note.getTitle());
        noteContentArea.setText(note.getContent());
        imagePathLabel.setText(note.getImageUrl() != null ? "📷" : "");

        if ("words".equals(note.getFolderType())) {
            letterCategoryComboBox.setItems(FXCollections.observableArrayList(OttomanLetterCategory.values()));
            letterCategoryComboBox.getSelectionModel().select(OttomanLetterCategory.valueOf(note.getCategory()));
            letterCategoryComboBox.setVisible(true);
            letterCategoryComboBox.setManaged(true);
        } else {
            letterCategoryComboBox.setVisible(false);
            letterCategoryComboBox.setManaged(false);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    @FXML
    private void handleUpdateNote() {
        String newTitle = noteTitleField.getText().trim();
        String newContent = noteContentArea.getText().trim();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            showAlert("Başlık ve içerik boş olamaz!");
            return;
        }

        note.setTitle(newTitle);
        note.setContent(newContent);

        if ("words".equals(note.getFolderType())) {
            OttomanLetterCategory selectedCategory = letterCategoryComboBox.getValue();
            if (selectedCategory == null) {
                showAlert("Lütfen kategori seçiniz.");
                return;
            }
            note.setCategory(selectedCategory.name());
        }

        System.out.println("🛠 Güncellenmek istenen not ID: " + note.getId());

        boolean success = NoteService.updateNote(note, accessToken);
        if (!success) {
            showAlert("Not güncellenirken hata oluştu.");
        }
        closeWindow();
    }


    @FXML
    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Görsel Seç");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(noteTitleField.getScene().getWindow());
        if (selectedFile != null) {
            note.setImageUrl(selectedFile.getAbsolutePath());
            imagePathLabel.setText("📷");
            uploadImageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) noteTitleField.getScene().getWindow();
        stage.close();
    }
}
