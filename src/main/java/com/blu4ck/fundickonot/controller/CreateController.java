package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.data.FolderDatabase;
import com.blu4ck.fundickonot.data.NoteDatabase;
import com.blu4ck.fundickonot.data.SubFolderDatabase;
import com.blu4ck.fundickonot.model.Folder;
import com.blu4ck.fundickonot.model.Note;
import com.blu4ck.fundickonot.model.SubFolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateController {

    @FXML
    private Button cancel;

    @FXML
    private Button createFolderBtn;

    @FXML
    private AnchorPane dialogGrid;

    @FXML
    private ComboBox<Folder> folderComboBox;

    @FXML
    private TextArea noteContentArea;

    @FXML
    private TextField noteTitleField;

    @FXML
    private ComboBox<SubFolder> subFolderComboBox;

    @FXML
    private Button uploadImageButton;

    private File selectedImageFile;


    private static final List<String> predefinedSubFolders = Arrays.asList("A", "B", "C", "D", "E");

    @FXML
    public void initialize() {
        loadFolders();
        List<SubFolder> subFolders = predefinedSubFolders.stream()
                .map(letter -> new SubFolder(0, letter, 0)) // id=0, folderId=0 (Sadece ComboBox için)
                .collect(Collectors.toList());

        subFolderComboBox.getItems().addAll(subFolders);
    }

    /** Kullanılabilir klasörleri yükler ve ComboBox'a ekler */
    private void loadFolders() {
        List<Folder> folders = FolderDatabase.getAllFolders();
        folderComboBox.getItems().addAll(folders);
    }

    /** Kullanıcı 'İptal' butonuna basınca pencereyi kapatır */
    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    /** Yeni bir klasör oluşturur ve veritabanına ekler */
    @FXML
    void createFolder(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Yeni Klasör Oluştur");
        dialog.setHeaderText(null);
        dialog.setContentText("Klasör adı:");

        dialog.showAndWait().ifPresent(name -> {
            int folderId = FolderDatabase.addFolder(name); // ✅ Yeni klasör ekleyip ID'sini al
            if (folderId > 0) {
                createDefaultSubFolders(folderId); // ✅ Yeni klasör için alt klasörleri oluştur
                refreshFolders(); // ✅ Klasör listesini güncelle
                refreshSubFolders(folderId); // ✅ Alt klasörleri güncelle
            } else {
                showAlert("Hata", "Klasör oluşturulamadı!", Alert.AlertType.ERROR);
            }
        });
    }


    private void createDefaultSubFolders(int folderId) {
        List<String> predefinedLetters = Arrays.asList("A", "B", "C", "D", "E");

        for (String letter : predefinedLetters) {
            SubFolder newSubFolder = new SubFolder(0, letter, folderId); // ✅ Folder ID ile bağlanıyor
            SubFolderDatabase.addSubFolder(newSubFolder); // ✅ Veritabanına ekleme
        }
    }



    private void refreshFolders() {
        folderComboBox.getItems().clear();
        folderComboBox.getItems().addAll(FolderDatabase.getAllFolders());
    }


    /** Kullanıcı resim eklemek için 'Yükle' butonuna basınca çalışır */
    @FXML
    void handleImageUpload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                File saveDir = new File("resources/local_resources");
                if (!saveDir.exists()) saveDir.mkdirs();

                File destFile = new File(saveDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                uploadImageButton.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            } catch (IOException e) {
                uploadImageButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                showAlert("Hata", "Resim yüklenirken bir hata oluştu.", Alert.AlertType.ERROR);
            }
        }
    }

    /** Notu veritabanına kaydeder */
    @FXML
    void handleSaveNote(ActionEvent event) {
        // Kullanıcının girdiği verileri al
        String title = noteTitleField.getText().trim();
        String content = noteContentArea.getText().trim();
        SubFolder selectedSubFolder = subFolderComboBox.getValue();

        // **1️⃣ Girdi Kontrolleri**
        if (title.isEmpty() || content.isEmpty()) {
            showAlert("Hata", "Başlık ve içerik boş olamaz!", Alert.AlertType.ERROR);
            return;
        }
        if (selectedSubFolder == null) {
            showAlert("Hata", "Bir alt klasör seçmelisiniz!", Alert.AlertType.ERROR);
            return;
        }

        // **2️⃣ Resim Yükleme İşlemi**
        String imagePath = null;
        if (selectedImageFile != null) {
            try {
                File saveDir = new File("resources/local_resources");
                if (!saveDir.exists()) saveDir.mkdirs();

                File destFile = new File(saveDir, selectedImageFile.getName());
                Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                imagePath = destFile.getAbsolutePath();
            } catch (IOException e) {
                showAlert("Hata", "Resim kaydedilemedi!", Alert.AlertType.ERROR);
                return;
            }
        }

        // **3️⃣ Notu Veritabanına Kaydet**
        boolean success = NoteDatabase.addNote(title, content, selectedSubFolder.getId(), imagePath);

        if (success) {
            showAlert("Başarılı", "Not başarıyla kaydedildi.", Alert.AlertType.INFORMATION);

            // **4️⃣ Pencereyi Kapat**
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        } else {
            showAlert("Hata", "Not kaydedilemedi!", Alert.AlertType.ERROR);
        }
    }



    /** Seçilen klasöre göre alt klasörleri günceller */
    @FXML
    void onFolderSelected(ActionEvent event) {
        Folder selectedFolder = folderComboBox.getValue();

        if (selectedFolder == null) {
            subFolderComboBox.getItems().clear();
            return;
        }

        subFolderComboBox.getItems().clear();

        List<SubFolder> subFolders = predefinedSubFolders.stream()
                .map(letter -> new SubFolder(0, letter, selectedFolder.getId()))
                .collect(Collectors.toList());

        subFolderComboBox.getItems().addAll(subFolders);
    }



    /** Seçilen alt klasörü işler  */
    @FXML
    void onSubFolderSelected(ActionEvent event) {

        if (subFolderComboBox.getItems().isEmpty()) {
            showAlert("Hata", "Alt klasör listesi boş! Lütfen bir klasör seçin.", Alert.AlertType.ERROR);
            return;
        }

        SubFolder selectedSubFolder = subFolderComboBox.getValue();

        if (selectedSubFolder == null) {
            showAlert("Hata", "Lütfen bir alt klasör seçiniz.", Alert.AlertType.ERROR);
            return;
        }
        Folder selectedFolder = folderComboBox.getValue();

        if (selectedFolder == null) {
            showAlert("Hata", "Önce bir ana klasör seçmelisiniz!", Alert.AlertType.ERROR);
            return;
        }


        SubFolder newSubFolder = new SubFolder(0, selectedSubFolder.getName(), selectedFolder.getId());
    }


    private void refreshSubFolders(int folderId) {
        subFolderComboBox.getItems().clear();

        List<SubFolder> subFolders = SubFolderDatabase.getSubFoldersByFolderId(folderId);

        if (subFolders.isEmpty()) {
            System.out.println("Uyarı: Seçili klasörün alt klasörü yok.");
            return;
        }

        subFolderComboBox.getItems().addAll(subFolders);
    }


    /** Kullanıcıya bilgi veya hata mesajı göstermek için kullanılan yardımcı metod */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
