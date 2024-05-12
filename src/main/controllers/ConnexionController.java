package main.controllers;

import main.models.Authentication;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ConnexionController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Appeler la fonction d'authentification
        boolean isAuthenticated = Authentication.authenticate(username, password);

        // Vérifier si l'authentification est réussie
        if (isAuthenticated) {
            // Charger la nouvelle page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/pageprincipal.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Page Principale");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Fermer la fenêtre de connexion actuelle
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
        } else {
            // Afficher un message d'échec
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Échec de la connexion");
            alert.setHeaderText(null);
            alert.setContentText("Identifiant ou mot de passe incorrect.");
            alert.showAndWait();
        }
    }
}
