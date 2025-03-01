package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.data.FolderDatabase;
import com.blu4ck.fundickonot.data.NoteDatabase;
import com.blu4ck.fundickonot.data.SubFolderDatabase;
import com.blu4ck.fundickonot.model.Folder;
import com.blu4ck.fundickonot.model.Note;
import com.blu4ck.fundickonot.model.SubFolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AppController {

    @FXML
    private Button closeButton;

    @FXML
    private Button createNoteButton;

    @FXML
    private Button editBtn;

    @FXML
    private Accordion folderAccordion;

    @FXML
    private ComboBox<Folder> folderComboBox;

    @FXML
    private Button fullscreenButton;

    @FXML
    private Button minimizeButton;

    @FXML
    private ToggleButton mode;

    @FXML
    private Accordion notesAccordion;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<SubFolder> subFolderComboBox;

    @FXML
    public void initialize() {
        loadFolders();
    }

    private void loadFolders() {
        folderComboBox.getItems().clear();

        List<Folder> folders = FolderDatabase.getAllFolders();
        if (!folders.isEmpty()) {
            folderComboBox.getItems().addAll(folders);
        }
    }

    private void refreshFolders() {
        loadFolders();
    }


    /** Uygulamayı kapatır */
    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /** Not düzenleme işlemini başlatır */
    @FXML
    void editNote(ActionEvent event) {

        Note selectedNote = getSelectedNote();
        if (selectedNote == null) {
            showAlert("Hata", "Düzenlemek için bir not seçmelisiniz!", Alert.AlertType.ERROR);
            return;
        }

        for (TitledPane pane : notesAccordion.getPanes()) {
            if (pane.isExpanded() && pane.getUserData() instanceof Note) {

                Note note = (Note) pane.getUserData();

                // TextField ve TextArea oluştur (Düzenlenebilir Alanlar)
                TextField titleField = new TextField(note.getTitle());
                TextArea contentArea = new TextArea(note.getContent());

                titleField.setOnAction(e -> saveEditedNote(note, titleField, contentArea, pane));
                contentArea.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) saveEditedNote(note, titleField, contentArea, pane);
                });

                VBox editBox = new VBox(titleField, contentArea);
                pane.setContent(editBox);
                break;
            }
        }
    }

    /** Notu kaydetme işlemi */
    private void saveEditedNote(Note note, TextField titleField, TextArea contentArea, TitledPane pane) {
        String newTitle = titleField.getText().trim();
        String newContent = contentArea.getText().trim();

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            showAlert("Hata", "Başlık ve içerik boş olamaz!", Alert.AlertType.ERROR);
            return;
        }

        // Veritabanını güncelle
        note.setTitle(newTitle);
        note.setContent(newContent);
        NoteDatabase.updateNote(note);

        // Güncellenmiş verileri UI'ya yansıt
        pane.setText(newTitle);
        pane.setContent(new Label(newContent));

        showAlert("Başarılı", "Not güncellendi!", Alert.AlertType.INFORMATION);
    }


    /** Pencereyi tam ekran yapar */
    @FXML
    void fullscreen(ActionEvent event) {
        Stage stage = (Stage) fullscreenButton.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    /** Arama kutusuna girilen metne göre notları filtreler */
    @FXML
    void handleSearch(KeyEvent event) {
        String query = searchField.getText().toLowerCase();
        List<Note> notes = NoteDatabase.getAllNotes().stream()
                .filter(note -> note.getTitle().toLowerCase().contains(query) || note.getContent().toLowerCase().contains(query))
                .collect(Collectors.toList());

        updateNotesAccordion(notes);
    }

    /** Pencereyi simge durumuna küçültür */
    @FXML
    void minimize(ActionEvent event) {
        Stage stage = (Stage) minimizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    /** Seçilen klasöre bağlı alt klasörleri yükler */
    @FXML
    void onFolderSelected(ActionEvent event) {
        Folder selectedFolder = folderComboBox.getValue();
        if (selectedFolder != null) {
            refreshSubFolders(selectedFolder.getId()); // ✅ Sadece ilgili klasörün alt klasörlerini getir
        }
    }



    private void refreshSubFolders(int folderId) {
        subFolderComboBox.getItems().clear(); // ✅ Önce temizle

        List<SubFolder> subFolders = SubFolderDatabase.getSubFoldersByFolderId(folderId);

        if (subFolders.isEmpty()) {
            subFolderComboBox.setVisible(false); // ✅ Eğer alt klasör yoksa ComboBox'ı gizle
        } else {
            subFolderComboBox.setVisible(true); // ✅ Eğer alt klasör varsa görünür yap
            subFolderComboBox.getItems().addAll(subFolders); // ✅ Sadece ilgili klasörün alt klasörlerini ekle
        }
    }



    @FXML
    void onSubFolderSelected(ActionEvent event) {
        SubFolder selectedSubFolder = subFolderComboBox.getValue();
        refreshNotes(selectedSubFolder);
    }



//    /** Seçilen alt klasöre göre notları günceller */
//    @FXML
//    void onSubFolderSelected(ActionEvent event) {
//        SubFolder selectedSubFolder = subFolderComboBox.getValue();
//        if (selectedSubFolder != null) {
//            List<Note> notes = NoteDatabase.getNotesBySubFolderId(selectedSubFolder.getId());
//            updateNotesAccordion(notes);
//        }
//    }

    /** Yeni not oluşturma penceresini açar */
    @FXML
    void openCreateDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/create/createDialog.fxml"));
            Parent root = loader.load();

            Stage createStage = new Stage();
            createStage.initModality(Modality.APPLICATION_MODAL);
            createStage.initStyle(StageStyle.UTILITY);
            createStage.setScene(new Scene(root));

            createStage.showAndWait();

            SubFolder selectedSubFolder = subFolderComboBox.getValue();
            if (selectedSubFolder != null) {
                refreshNotes(selectedSubFolder);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Hata", "Not oluşturma penceresi açılamadı! Dosya eksik olabilir.", Alert.AlertType.ERROR);
        }
    }


    /** Karanlık / aydınlık mod geçişini sağlar */
    @FXML
    void toggleMode(ActionEvent event) {
        if (mode.isSelected()) {
            rootPane.getStylesheets().clear();
            rootPane.getStylesheets().add(getClass().getResource("/views/app/darkMode.css").toExternalForm());
        } else {
            rootPane.getStylesheets().clear();
            rootPane.getStylesheets().add(getClass().getResource("/views/app/lightMode.css").toExternalForm());
        }
    }

    /** Seçili notu getirir (UI'da seçili bir not olmalı) */
    private Note getSelectedNote() {
        for (TitledPane pane : notesAccordion.getPanes()) {
            if (pane.isExpanded() && pane.getUserData() instanceof Note) {
                return (Note) pane.getUserData();
            }
        }
        return null;
    }

    /** Notları güncellemek için Accordion bileşenini temizleyip tekrar doldurur */
    private void updateNotesAccordion(List<Note> notes) {
        notesAccordion.getPanes().clear();
        for (Note note : notes) {
            TitledPane notePane = new TitledPane(note.getTitle(), new Label(note.getContent()));
            notePane.setUserData(note);
            notesAccordion.getPanes().add(notePane);
        }
    }

    /** Notları yeniler (örn. yeni not eklendiğinde) */
    private void refreshNotes(SubFolder subFolder) {
        notesAccordion.getPanes().clear();

        if (subFolder == null) {
            return;
        }

        List<Note> notes = NoteDatabase.getNotesBySubFolderId(subFolder.getId());

        for (Note note : notes) {
            TitledPane notePane = new TitledPane(note.getTitle(), new Label(note.getContent()));
            notePane.setUserData(note);
            notesAccordion.getPanes().add(notePane);
        }
    }




    /** Kullanıcıya hata veya bilgi mesajı göstermek için yardımcı metod */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
