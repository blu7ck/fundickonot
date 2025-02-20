package com.blu4ck.fundickonot;

import com.gluonhq.charm.glisten.control.Icon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;

    @FXML
    private Icon close;

    // DATABASE TOOLS
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    @FXML
    private void loginAdmin(ActionEvent event) {
        // SQL sorgusu
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";

        // Giriş alanlarının boş olup olmadığını kontrol et
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hata Mesajı", null, "Kullanıcı adı veya şifre boş olamaz!");
            return;
        }

        try {
            connect = Database.connectionDb();
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, username.getText());
            prepare.setString(2, password.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                showAlert(Alert.AlertType.INFORMATION, "Bilgi Mesajı", null, "Giriş Başarılı!");

                // Yeni pencereyi yükle (dashboard.fxml örnek dosya adı, kendi dosya yolunuza göre düzenleyin)
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/src/main/resources/views/dashboard.fxmldashboard.fxml")));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();

                // Mevcut pencereyi kapat
                Stage currentStage = (Stage) loginBtn.getScene().getWindow();
                currentStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata Mesajı", null, "Kullanıcı adı veya şifre hatalı!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hata", null, "Bir hata oluştu: " + e.getMessage());
        } finally {
            // Kaynakların kapatılması
            try {
                if (result != null) result.close();
                if (prepare != null) prepare.close();
                if (connect != null) connect.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCloseAction(MouseEvent event) {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Başlangıç işlemleri veya olay dinleyici eklemeleri burada yapılabilir.
    }

    // Kullanıcıya alert mesajı göstermek için yardımcı metod
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
