package com.blu4ck.fundickonot;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML
    private HBox topicsHBox;

    @FXML
    private ListView<String> explanationListView;

    @FXML
    private ListView<Note> notesListView;

    @FXML
    private StackPane mainContentArea;

    @FXML
    private ListView<Category> categoryListView;

    private ObservableList<Folder> folders = FXCollections.observableArrayList();
    private ObservableList<Category> categories = FXCollections.observableArrayList();
    private int selectedFolderId = -1;

    @FXML
    public void initialize() {
        DatabaseHelper.createTables();
        loadFolders();
        updateCategoryList();
        if (!folders.isEmpty()) {
            selectFolder(folders.get(0));
        }
    }

    private void loadFolders() {
        folders.clear();
        List<Folder> folderList = DatabaseHelper.getAllFolders();
        folders.addAll(folderList);

        topicsHBox.getChildren().clear();
        for (Folder folder : folders) {
            Button btn = new Button(folder.getName());
            btn.setOnAction(e -> selectFolder(folder));
            topicsHBox.getChildren().add(btn);
        }
    }

    private void selectFolder(Folder folder) {
        selectedFolderId = folder.getId();
        updateNotesList();
    }

    private void updateNotesList() {
        if (selectedFolderId == -1) {
            notesListView.setItems(FXCollections.observableArrayList());
            return;
        }
        List<Note> noteList = DatabaseHelper.getNotesByFolder(selectedFolderId);
        ObservableList<Note> obsNotes = FXCollections.observableArrayList(noteList);
        notesListView.setItems(obsNotes);

        // Resimli notlar için ilk notun açıklamalarını göster
        obsNotes.stream().filter(n -> n.getType().equalsIgnoreCase("image"))
                .findFirst().ifPresent(n -> {
                    ObservableList<String> explanations = FXCollections.observableArrayList();
                    if (n.getExplanation() != null) {
                        String[] lines = n.getExplanation().split("\n");
                        explanations.addAll(lines);
                    }
                    explanationListView.setItems(explanations);
                });
    }

    private void updateCategoryList() {
        categories.setAll(DatabaseHelper.getAllCategories());
        categoryListView.setItems(categories);
    }

    @FXML
    private void handleNewFolder() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Yeni Klasör Oluştur");
        dialog.setHeaderText(null);
        dialog.setContentText("Klasör adı:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            DatabaseHelper.insertFolder(name);
            loadFolders();
        });
    }

    @FXML
    private void handleDeleteFolder() {
        if (selectedFolderId == -1) {
            showAlert("Klasör Seçilmedi", "Lütfen silmek için bir klasör seçin.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Klasör Sil");
        alert.setHeaderText(null);
        alert.setContentText("Seçili klasörü silmek istediğinize emin misiniz?\n(Not: Bu klasöre ait tüm notlar da silinecektir.)");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseHelper.deleteFolder(selectedFolderId);
            selectedFolderId = -1;
            loadFolders();
            updateNotesList();
        }
    }

    @FXML
    private void handleImageNotes() {
        if (selectedFolderId == -1) return;
        List<Note> noteList = DatabaseHelper.getNotesByFolder(selectedFolderId);
        ObservableList<Note> obsNotes = FXCollections.observableArrayList();
        for (Note n : noteList) {
            if (n.getType().equalsIgnoreCase("image")) {
                obsNotes.add(n);
            }
        }
        notesListView.setItems(obsNotes);
    }

    @FXML
    private void handleTextNotes() {
        if (selectedFolderId == -1) return;
        List<Note> noteList = DatabaseHelper.getNotesByFolder(selectedFolderId);
        ObservableList<Note> obsNotes = FXCollections.observableArrayList();
        for (Note n : noteList) {
            if (n.getType().equalsIgnoreCase("text")) {
                obsNotes.add(n);
            }
        }
        notesListView.setItems(obsNotes);
    }

    @FXML
    private void handleCreateCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Yeni Kategori Oluştur");
        dialog.setHeaderText(null);
        dialog.setContentText("Kategori adı:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            DatabaseHelper.insertCategory(name);
            updateCategoryList();
        });
    }

    @FXML
    private void handleAddNote() {
        if (selectedFolderId == -1) return;

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Yeni Not Ekle");
        dialog.setHeaderText("Not Başlığı ve İçeriği Giriniz");

        ButtonType addButtonType = new ButtonType("Ekle", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Başlık");
        TextField contentField = new TextField();
        contentField.setPromptText("İçerik");

        grid.add(new Label("Başlık:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("İçerik:"), 0, 1);
        grid.add(contentField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> titleField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair<>(titleField.getText(), contentField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String title = pair.getKey();
            String content = pair.getValue();
            DatabaseHelper.insertNote(selectedFolderId, "text", title, content, "", "");
            updateNotesList();
        });
    }


    @FXML
    private void handleAddImageNote() {
        if (selectedFolderId == -1) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Resim Seçin");
        // İstenirse, resim filtreleri eklenebilir:
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            // Resim seçildiyse, resim notu için başlık ve açıklama girişi alalım.
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Resimli Not Ekle");
            dialog.setHeaderText("Not Başlığı ve Açıklaması Giriniz");

            ButtonType addButtonType = new ButtonType("Ekle", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField titleField = new TextField();
            titleField.setPromptText("Başlık");
            TextField explanationField = new TextField();
            explanationField.setPromptText("Açıklama (satır başı için \\n kullanın)");

            grid.add(new Label("Başlık:"), 0, 0);
            grid.add(titleField, 1, 0);
            grid.add(new Label("Açıklama:"), 0, 1);
            grid.add(explanationField, 1, 1);

            dialog.getDialogPane().setContent(grid);
            Platform.runLater(() -> titleField.requestFocus());

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return new Pair<>(titleField.getText(), explanationField.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(pair -> {
                String title = pair.getKey();
                String explanation = pair.getValue();
                // Resimli not eklenirken, "content" alanına resim dosyasının yolunu ekliyoruz.
                DatabaseHelper.insertNote(selectedFolderId, "image", title, selectedFile.getAbsolutePath(), explanation, "");
                updateNotesList();
            });
        }
    }

    @FXML
    private void handleEditNote() {
        Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            showAlert("Not Seçilmedi", "Lütfen düzenlemek için bir not seçin.");
            return;
        }
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Not Düzenle");
        dialog.setHeaderText("Not Başlığı ve İçeriğini Güncelleyin");

        ButtonType updateButtonType = new ButtonType("Güncelle", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setText(selectedNote.getTitle());
        TextField contentField = new TextField();
        contentField.setText(selectedNote.getContent());

        grid.add(new Label("Başlık:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("İçerik:"), 0, 1);
        grid.add(contentField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> titleField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                return new Pair<>(titleField.getText(), contentField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String newTitle = pair.getKey();
            String newContent = pair.getValue();
            DatabaseHelper.updateNote(selectedNote.getId(), newTitle, newContent, selectedNote.getExplanation(), selectedNote.getCategory());
            updateNotesList();
        });
    }

    @FXML
    private void handleDeleteNote() {
        Note selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            showAlert("Not Seçilmedi", "Lütfen silmek için bir not seçin.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Not Sil");
        alert.setHeaderText(null);
        alert.setContentText("Seçili notu silmek istediğinize emin misiniz?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseHelper.deleteNote(selectedNote.getId());
            updateNotesList();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private TextField categoryNameField;

    @FXML
    private void handleAddCategory() {
        String categoryName = categoryNameField.getText().trim();
        if (!categoryName.isEmpty()) {
            DatabaseHelper.insertCategory(categoryName);
            categoryNameField.clear();
            loadCategories();  // Kategorileri güncelle
        }
    }

    private void loadCategories() {
        List<Category> categories = DatabaseHelper.getAllCategories();
        ObservableList<Category> categoryObservableList = FXCollections.observableArrayList(categories);
        categoryListView.setItems(categoryObservableList);
    }
    @FXML
    private void handleDeleteCategory() {
        Category selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            DatabaseHelper.deleteCategory(selectedCategory.getId());
            loadCategories();
        }
    }


}
