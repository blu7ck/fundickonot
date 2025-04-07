package com.blu4ck.fundickonot.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class LoginController {

    @FXML private VBox loginPane;
    @FXML private VBox registerPane;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private Label statusLabel;

    @FXML private TextField regEmail;
    @FXML private PasswordField regPassword;
    @FXML private PasswordField regRepeatPassword;
    @FXML private Label registerStatusLabel;

    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ1bmdvbHdsa2Z3a3lmZHlldnJxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM1MDAxNDAsImV4cCI6MjA1OTA3NjE0MH0.VwQ8xuUcGspJX_KijtNcWQ_HCTE1LgrO6JEBLuKTELI";
    private static final String PROJECT_URL = "https://fungolwlkfwkyfdyevrq.supabase.co";

    // Giriş ekranı - kullanıcı giriş butonu
    @FXML
    private void loginAdmin(ActionEvent event) {
        String email = username.getText().trim();
        String pass = password.getText().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Kullanıcı adı veya şifre boş bırakılamaz.");
            return;
        }

        try {
            String json = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, pass);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROJECT_URL + "/auth/v1/token?grant_type=password"))
                    .header("Content-Type", "application/json")
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.body());

            if (response.statusCode() == 200) {
                String accessToken = jsonResponse.get("access_token").asText();
                String userId = jsonResponse.get("user").get("id").asText();
                JsonNode confirmedAt = jsonResponse.get("user").get("email_confirmed_at");

                if (confirmedAt == null || confirmedAt.isNull()) {
                    statusLabel.setText("Lütfen e-posta adresinizi onaylayın.");
                    return;
                }

                openAppWindow(userId, accessToken); // ✅ Güncellendi
            }
            else {
                String message = jsonResponse.has("message") ? jsonResponse.get("message").asText() : "Giriş başarısız.";
                statusLabel.setText("Hata: " + message);
            }

        } catch (Exception e) {
            statusLabel.setText("Hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Kayıt işlemi
    @FXML
    private void registerUser(ActionEvent event) {
        String email = regEmail.getText().trim();
        String pass = regPassword.getText().trim();
        String repeat = regRepeatPassword.getText().trim();

        if (email.isEmpty() || pass.isEmpty() || repeat.isEmpty()) {
            registerStatusLabel.setText("Lütfen tüm alanları doldurun.");
            return;
        }

        if (!pass.equals(repeat)) {
            registerStatusLabel.setText("Şifreler eşleşmiyor.");
            return;
        }

        try {
            String json = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, pass);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(PROJECT_URL + "/auth/v1/signup"))
                    .header("Content-Type", "application/json")
                    .header("apikey", API_KEY)
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                registerStatusLabel.setText("Kayıt başarılı. Lütfen e-postanızı onaylayın.");
            } else {
                registerStatusLabel.setText("Kayıt başarısız: " + response.body());
            }

        } catch (Exception e) {
            registerStatusLabel.setText("Hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openAppWindow(String userId, String accessToken) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/app/newApp.fxml"));
            Parent root = loader.load();

            AppController appController = loader.getController();
            appController.setUserContext(userId, accessToken); // ✅ Güncellendi

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Fundickonot");
            stage.show();

            Stage loginStage = (Stage) loginPane.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Giriş ve kayıt geçiş animasyonları
    @FXML private void switchToRegister(ActionEvent event) {
        fadeOut(loginPane);
        fadeIn(registerPane);
    }

    @FXML private void switchToLogin(ActionEvent event) {
        fadeOut(registerPane);
        fadeIn(loginPane);
    }

    private void fadeOut(VBox pane) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), pane);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> pane.setVisible(false));
        fade.play();
    }

    private void fadeIn(VBox pane) {
        pane.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(300), pane);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }
}
