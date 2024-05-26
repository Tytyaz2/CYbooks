package main.controllers;

import javafx.application.Application;
import javafx.application.Platform;
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

public class ConnexionController extends Application {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Méthode pour activer/désactiver le mode plein écran
    public void toggleFullScreen() {
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }


    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Appeler la fonction d'authentification
        boolean isAuthenticated = Authentication.authenticate(username, password);

        // Vérifier si l'authentification est réussie
        if (isAuthenticated) {
            // Charger la nouvelle page
// Charger la nouvelle page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/MainPage.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Page Principale");

                // Ajouter un gestionnaire d'événements pour fermer l'application lorsque la fenêtre est fermée
                stage.setOnCloseRequest(event -> {
                    // Code à exécuter lors de la fermeture de la fenêtre
                    Platform.exit(); // Fermer l'application
                });

                stage.show();
            } catch (javafx.fxml.LoadException e) {
                e.printStackTrace(); // afficher un message d'erreur
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        Parent root = FXMLLoader.load(getClass().getResource("/main/views/connection.fxml"));

        // Créer une scène
        Scene scene = new Scene(root, 1280, 720);

        // Définir la scène et afficher la fenêtre principale
        primaryStage.setScene(scene);
        primaryStage.setTitle("connexion");
        primaryStage.show();
    }
}
