package com.blu4ck.fundickonot;

import com.blu4ck.fundickonot.data.*;
import com.blu4ck.fundickonot.model.Folder;
import com.blu4ck.fundickonot.model.Note;
import com.blu4ck.fundickonot.model.SubFolder;
import com.blu4ck.fundickonot.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.List;

public class Fundickonot extends Application {

    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("/views/login/login.fxml"));
        Scene scene = new Scene(root);

        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
            stage.setOpacity(0.85);
        });

        root.setOnMouseReleased(event -> stage.setOpacity(1));

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Database.initializeDatabase();
        UserDatabase.createUsersTable();
        UserDatabase.ensureDefaultUser();
        launch(args);

    }
}