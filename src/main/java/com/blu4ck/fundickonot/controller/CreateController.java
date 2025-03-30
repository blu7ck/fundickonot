package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.data.NoteDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
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

    private String selectedFolderType = "notes";  // default
    private String imagePath = null;

    @FXML
    public void initialize() {
        folderComboBox.getItems().addAll("notes", "words");
        folderComboBox.getSelectionModel().selectFirst();
        selectedFolderType = folderComboBox.getValue();
    }

    @FXML
    void onFolderSelected(ActionEvent event) {
        selectedFolderType = folderComboBox.getValue();
    }

    @FXML
    void handleSaveNote(ActionEvent event) {
        String title = noteTitleField.getText().trim();
        String content = noteContentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Başlık ve içerik boş olamaz!");
            return;
        }

        boolean success = NoteDatabase.addNote(title, content, imagePath, selectedFolderType);

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
