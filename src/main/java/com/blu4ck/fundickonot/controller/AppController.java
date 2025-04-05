package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.data.NoteDatabase;
import com.blu4ck.fundickonot.model.Note;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AppController {
    @FXML private ScrollPane imagePreview;
    @FXML private ImageView noteImageView;
    @FXML private Accordion accordion;
    @FXML private Button WordsButton;
    @FXML private Button notesButton;
    @FXML private Button createNoteButton;
    @FXML private Button deleteNoteButton;
    @FXML private Button editeNoteButton;
    @FXML private TextField searchField;
    @FXML private ToggleButton toggleMode;

    @FXML private ListView<String> noteListView;
    @FXML private TextArea noteContentArea;
    private boolean isDarkMode = false;


    private String currentFolderType = "notes";
    private ObservableList<Note> currentNotes = FXCollections.observableArrayList();
    private Note selectedNote;

    @FXML
    public void initialize() {
        loadNotes();

        noteListView.setOnMouseClicked(this::handleNoteSelection);

        searchField.setOnKeyReleased(event -> handleSearch());
    }
    private void loadNotes() {
        selectedNote = null;
        noteContentArea.clear();
        imagePreview.setContent(null);
        noteImageView.setImage(null);
        noteListView.getSelectionModel().clearSelection();

        currentNotes.setAll(NoteDatabase.getAllNotes(currentFolderType));

        boolean isWords = currentFolderType.equals("words");

        if (isWords) {
            showNotesGroupedByCategory(currentNotes);
        } else {
            showNotesInListView(currentNotes);
        }

        accordion.setVisible(isWords);
        accordion.setManaged(isWords);
        noteListView.setVisible(!isWords);
        noteListView.setManaged(!isWords);
    }

    private void showNotesInListView(List<Note> notes) {
        ObservableList<String> titles = FXCollections.observableArrayList();
        for (Note note : notes) {
            titles.add(note.getTitle());
        }
        noteListView.setItems(titles);
    }


    private void showNotesGroupedByCategory(List<Note> wordNotes) {
        Map<String, List<Note>> grouped = new HashMap<>();

        // notları kategorilere göre grupluyoruz
        for (Note note : wordNotes) {
            String category = note.getCategory();
            grouped.putIfAbsent(category, new ArrayList<>());
            grouped.get(category).add(note);
        }

        accordion.getPanes().clear(); // varsa önce temizle

        for (String category : grouped.keySet()) {
            VBox contentBox = new VBox(10);
            contentBox.setPadding(new Insets(10));

            for (Note note : grouped.get(category)) {
                Hyperlink link = new Hyperlink(note.getTitle());
                link.setOnAction(e -> showNoteDetails(note)); // tıklanınca içeriği göster
                contentBox.getChildren().add(link);
            }

            TitledPane pane = new TitledPane(category, contentBox);
            accordion.getPanes().add(pane);
        }
    }

    private void showNoteDetails(Note note) {
        // Önce alanları temizle
        noteContentArea.clear();
        imagePreview.setContent(null);
        selectedNote = note;

        // Metin alanı
        if (note.getContent() != null && !note.getContent().isEmpty()) {
            noteContentArea.setText(note.getContent());
        }

        // Görsel alanı
        if (note.getImagePath() != null && !note.getImagePath().isEmpty()) {
            File file = new File(note.getImagePath());
            if (file.exists()) {
                Image image = new Image(file.toURI().toString(), false);
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imagePreview.setPannable(true);
                imagePreview.setContent(imageView);
            } else {
                // Görsel dosyası bulunamadıysa hiçbir şey gösterme
                imagePreview.setContent(null);
            }
        }
    }




    private void handleNoteSelection(MouseEvent event) {
        int index = noteListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < currentNotes.size()) {
            selectedNote = currentNotes.get(index);
            noteContentArea.setText(selectedNote.getContent());

            if (selectedNote.getImagePath() != null && !selectedNote.getImagePath().isEmpty()) {
                try {
                    File imageFile = new File(selectedNote.getImagePath());
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString(), 0, 0, true, true); // orijinal boyut
                        noteImageView.setImage(image);
                    } else {
                        noteImageView.setImage(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    noteImageView.setImage(null);
                }
            } else {
                noteImageView.setImage(null);
            }
        }
        }


    @FXML
    private void handleCreateNote() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/create/createDialog.fxml"));
            AnchorPane pane = loader.load();
            Stage dialog = new Stage();
            dialog.setScene(new Scene(pane));
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Yeni Not");
            dialog.showAndWait();
            loadNotes(); // not eklendiyse listeyi yenile
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteNote() {
        if (selectedNote == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Notu Sil");
        alert.setHeaderText(null);
        alert.setContentText("Bu notu silmek istediğinize emin misiniz?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NoteDatabase.deleteNote(selectedNote.getId());
            loadNotes();
            noteContentArea.clear();
            imagePreview.setContent(null);
            selectedNote = null;
        }
    }


    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            List<Note> results = NoteDatabase.searchNotes(query, currentFolderType);
            currentNotes.setAll(results);
            ObservableList<String> titles = FXCollections.observableArrayList();
            for (Note note : currentNotes) {
                titles.add(note.getTitle());
            }
            noteListView.setItems(titles);
        } else {
            loadNotes(); // boş arama = tüm notlar
        }
    }

    @FXML
    private void switchToNotes() {
        currentFolderType = "notes";
        loadNotes();
    }

    @FXML
    private void switchToWords() {
        currentFolderType = "words";
        loadNotes();
    }

    @FXML
    private void toggleDarkMode() {
        Scene scene = toggleMode.getScene();
        if (scene == null) return;

        isDarkMode = !isDarkMode;

        scene.getStylesheets().clear();

        if (isDarkMode) {
            scene.getStylesheets().add(getClass().getResource("/views/app/dark_theme.css").toExternalForm());
            toggleMode.setText("🌙");
        } else {
            scene.getStylesheets().add(getClass().getResource("/views/app/light_theme.css").toExternalForm());
            toggleMode.setText("🔅");
        }
    }

    @FXML
    private void handleEditNote() {
        if (selectedNote == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/edit/editDialog.fxml"));
            AnchorPane pane = loader.load();
            EditController controller = loader.getController();
            controller.setNote(selectedNote);

            Stage dialog = new Stage();
            dialog.setScene(new Scene(pane));
            dialog.setTitle("Notu Düzenle");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();

            loadNotes(); // güncel listeyi getir
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
