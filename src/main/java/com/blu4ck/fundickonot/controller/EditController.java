package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.data.NoteDatabase;
import com.blu4ck.fundickonot.model.Note;
import com.blu4ck.fundickonot.model.OttomanLetterCategory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;

public class EditController {

    @FXML private TextField noteTitleField;
    @FXML private TextArea noteContentArea;
    @FXML private Button uploadImageButton;
    @FXML private Label imagePathLabel;
    @FXML private ComboBox<OttomanLetterCategory> letterCategoryComboBox;
    @FXML private HBox categoryBox;

    private Note note;
    private String selectedImagePath;

    public void setNote(Note note) {
        this.note = note;

        noteTitleField.setText(note.getTitle());
        noteContentArea.setText(note.getContent());

        if (note.getImagePath() != null && !note.getImagePath().isEmpty()) {
            selectedImagePath = note.getImagePath();
            imagePathLabel.setText("📷 Yüklü");
        }

        if (note.getFolderType().equals("words")) {
            categoryBox.setVisible(true);
            categoryBox.setManaged(true);
            letterCategoryComboBox.setItems(FXCollections.observableArrayList(OttomanLetterCategory.values()));
            letterCategoryComboBox.setValue(OttomanLetterCategory.valueOf(note.getCategory()));
        } else {
            categoryBox.setVisible(false);
            categoryBox.setManaged(false);
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

        note.setTitle(title);
        note.setContent(content);
        note.setImagePath(selectedImagePath);
        note.setCreatedAt(LocalDateTime.now()); // isteğe bağlı

        if (note.getFolderType().equals("words")) {
            OttomanLetterCategory selectedCategory = letterCategoryComboBox.getValue();
            if (selectedCategory == null) {
                showAlert("Lütfen bir Osmanlı harf kategorisi seçiniz.");
                return;
            }
            note.setCategory(selectedCategory.name());
        }

        boolean success = NoteDatabase.updateNote(note);

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
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = fileChooser.showOpenDialog(noteTitleField.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            imagePathLabel.setText("📷 Güncellendi");
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
