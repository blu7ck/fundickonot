package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.data.NoteDatabase;
import com.blu4ck.fundickonot.model.OttomanLetterCategory;
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

    private String selectedFolderType = "notes"; // varsayılan
    private String imagePath = null;

    @FXML
    public void initialize() {
        // ComboBox'a sadece iki seçenek ekliyoruz
        folderComboBox.getItems().addAll("notes", "words");
        folderComboBox.getSelectionModel().selectFirst();
        selectedFolderType = folderComboBox.getValue();

        // Osmanlı harflerini ekle
        letterCategoryComboBox.setItems(FXCollections.observableArrayList(OttomanLetterCategory.values()));

        // Başlangıçta kategori kutusu gizli
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

        boolean success = false;

        if (selectedFolderType.equals("words")) {
            OttomanLetterCategory selectedCategory = letterCategoryComboBox.getValue();
            if (selectedCategory == null) {
                showAlert("Osmanlı harf kategorisini seçmelisiniz.");
                return;
            }
            success = NoteDatabase.addNote(title, content, imagePath, "words", selectedCategory.name());

        } else {
            success = NoteDatabase.addNote(title, content, imagePath, "notes");
        }

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
