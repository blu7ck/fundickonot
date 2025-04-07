package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.model.Note;
import com.blu4ck.fundickonot.model.OttomanLetterCategory;
import com.blu4ck.fundickonot.remote.NoteService;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class CreateController {

    @FXML private Button cancel;
    @FXML private AnchorPane dialogGrid;
    @FXML private ComboBox<String> folderComboBox;
    @FXML private TextArea noteContentArea;
    @FXML private TextField noteTitleField;
    @FXML private Button saveButton;
    @FXML private Button uploadImageButton;
    @FXML private ComboBox<OttomanLetterCategory> letterCategoryComboBox;
    @FXML private HBox categoryBox;

    private String selectedFolderType = "notes";
    private String imagePath = null;
    private String accessToken; // 🔐

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @FXML
    public void initialize() {
        folderComboBox.getItems().addAll("notes", "words");
        folderComboBox.getSelectionModel().selectFirst();
        selectedFolderType = folderComboBox.getValue();

        letterCategoryComboBox.setItems(FXCollections.observableArrayList(OttomanLetterCategory.values()));
        categoryBox.setVisible(false);
        categoryBox.setManaged(false);

        folderComboBox.setOnAction(event -> {
            selectedFolderType = folderComboBox.getValue();
            boolean isWords = selectedFolderType.equals("words");
            categoryBox.setVisible(isWords);
            categoryBox.setManaged(isWords);
        });
    }

    @FXML
    void handleSaveNote(ActionEvent event) {
        String title = noteTitleField.getText().trim();
        String content = noteContentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Başlık ve içerik boş olamaz!");
            return;
        }

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setImageUrl(imagePath);
        note.setFolderType(selectedFolderType);

        if (selectedFolderType.equals("words")) {
            OttomanLetterCategory selectedCategory = letterCategoryComboBox.getValue();
            if (selectedCategory == null) {
                showAlert("Osmanlı harf kategorisini seçmelisiniz.");
                return;
            }
            note.setCategory(selectedCategory.name());
        }

        boolean success = NoteService.createNote(note, accessToken); // 🔑 accessToken ile istek

        if (success) {
            closeWindow();
        } else {
            showAlert("Not kaydedilemedi.");
        }
    }

    @FXML
    void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Görsel Seç");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Resim Dosyaları", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(dialogGrid.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            uploadImageButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
