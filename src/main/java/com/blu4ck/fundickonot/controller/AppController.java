package com.blu4ck.fundickonot.controller;

import com.blu4ck.fundickonot.model.Note;
import com.blu4ck.fundickonot.remote.NoteService;
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

    @FXML private Accordion accordion;
    @FXML private ListView<String> noteListView;
    @FXML private TextArea noteContentArea;
    @FXML private ScrollPane imagePreview;
    @FXML private ImageView noteImageView;
    @FXML private TextField searchField;
    @FXML private ToggleButton toggleMode;

    private boolean isDarkMode = false;
    private String currentFolderType = "notes";
    private ObservableList<Note> currentNotes = FXCollections.observableArrayList();
    private Note selectedNote;
    private String userId;
    private String accessToken;

    public void setUserContext(String userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        System.out.println("✅ AppController - Kullanıcı ID ve Token alındı");
        loadNotes();
    }

    @FXML
    public void initialize() {
        noteListView.setOnMouseClicked(this::handleNoteSelection);
        searchField.setOnKeyReleased(e -> handleSearch());
    }

    private void loadNotes() {
        selectedNote = null;
        noteContentArea.clear();
        imagePreview.setContent(null);
        noteListView.getSelectionModel().clearSelection();

        if (userId == null || accessToken == null) return;

        List<Note> notes = NoteService.getNotes(userId, accessToken);
        if (notes == null || notes.isEmpty()) {
            noteListView.setItems(FXCollections.observableArrayList("Not bulunamadı"));
            accordion.getPanes().clear();
            return;
        }

        currentNotes.setAll(notes.stream()
                .filter(n -> currentFolderType.equals(n.getFolderType()))
                .toList());

        if (currentFolderType.equals("words")) {
            showNotesGroupedByCategory(currentNotes);
        } else {
            showNotesInListView(currentNotes);
        }

        accordion.setVisible(currentFolderType.equals("words"));
        noteListView.setVisible(currentFolderType.equals("notes"));
    }

    private void showNotesInListView(List<Note> notes) {
        ObservableList<String> titles = FXCollections.observableArrayList();
        for (Note note : notes) {
            titles.add(note.getTitle());
        }
        if (titles.isEmpty()) {
            titles.add("Not bulunamadı");
        }
        noteListView.setItems(titles);
    }

    private void showNotesGroupedByCategory(List<Note> wordNotes) {
        Map<String, List<Note>> grouped = new HashMap<>();
        for (Note note : wordNotes) {
            String category = Optional.ofNullable(note.getCategory()).orElse("Kategori Yok");
            grouped.putIfAbsent(category, new ArrayList<>());
            grouped.get(category).add(note);
        }

        accordion.getPanes().clear();

        for (Map.Entry<String, List<Note>> entry : grouped.entrySet()) {
            VBox contentBox = new VBox(10);
            contentBox.setPadding(new Insets(10));

            for (Note note : entry.getValue()) {
                Hyperlink link = new Hyperlink(note.getTitle());
                link.setOnAction(e -> showNoteDetails(note));
                contentBox.getChildren().add(link);
            }

            TitledPane pane = new TitledPane(entry.getKey(), contentBox);
            accordion.getPanes().add(pane);
        }
    }

    private void showNoteDetails(Note note) {
        selectedNote = note;
        noteContentArea.clear();
        imagePreview.setContent(null);

        if (note.getContent() != null) {
            noteContentArea.setText(note.getContent());
        }

        String imagePath = note.getImageUrl();
        if (imagePath != null && !imagePath.isEmpty()) {
            Image image;
            File file = new File(imagePath);
            if (file.exists()) {
                image = new Image(file.toURI().toString());
            } else {
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/assets/default_image.png")));
            }
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(300);
            imageView.setSmooth(true);
            imagePreview.setContent(imageView);
        }
    }

    private void handleNoteSelection(MouseEvent event) {
        int index = noteListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < currentNotes.size()) {
            selectedNote = currentNotes.get(index);
            showNoteDetails(selectedNote);
        }
    }

    @FXML
    private void handleCreateNote() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/create/createDialog.fxml"));
            AnchorPane pane = loader.load();
            CreateController controller = loader.getController();
            controller.setAccessToken(accessToken); // 🔑 token'ı ilet
            Stage dialog = new Stage();
            dialog.setScene(new Scene(pane));
            dialog.setTitle("Yeni Not");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
            loadNotes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditNote() {
        if (selectedNote == null) return;
        try {
            System.out.println("✏️ Düzenlenmek üzere seçilen not ID: " + selectedNote.getId()); // 🔍 Log eklendi

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/edit/editDialog.fxml"));
            AnchorPane pane = loader.load();
            EditController controller = loader.getController();
            controller.setNote(selectedNote); // 👈 id burada null olabilir
            controller.setAccessToken(accessToken);

            Stage dialog = new Stage();
            dialog.setScene(new Scene(pane));
            dialog.setTitle("Notu Düzenle");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
            loadNotes();
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
        alert.setContentText("Seçilen notu silmek istediğinize emin misiniz?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            NoteService.deleteNote(selectedNote.getId(), accessToken); // 🔑 token'lı çağrı
            loadNotes();
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            List<Note> results = NoteService.searchNotes(userId, query, currentFolderType, accessToken); // 🔑 token'lı çağrı
            if (currentFolderType.equals("words")) {
                showNotesGroupedByCategory(results);
            } else {
                showNotesInListView(results);
            }
        } else {
            loadNotes();
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

        String style = isDarkMode ? "dark_theme.css" : "light_theme.css";
        scene.getStylesheets().add(getClass().getResource("/styles/" + style).toExternalForm());
        toggleMode.setText(isDarkMode ? "🌙" : "🔅");
    }
}
