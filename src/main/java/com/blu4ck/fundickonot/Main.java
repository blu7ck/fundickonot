package com.blu4ck.fundickonot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/views/main_layout.fxml");
        if (fxmlUrl == null) {
            throw new IllegalStateException("FXML dosyası bulunamadı!");
        }
        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Not Tutma Uygulaması");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
