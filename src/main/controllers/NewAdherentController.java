package main.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import main.models.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import main.models.DatabaseConnection;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.SQLException;

public class NewAdherentController {

    @FXML
    private TextArea nomTextArea;

    @FXML
    private TextArea prenomTextArea;

    @FXML
    private TextArea mailTextArea;

    @FXML
    private Label errorMessageLabel;

    private MainControllers mainController;

    @FXML
    void handleAjouterAdherent(ActionEvent event) throws SQLException {
        // Récupérer les valeurs des champs texte
        String nom = nomTextArea.getText();
        String prenom = prenomTextArea.getText();
        String mail = mailTextArea.getText();

        // Valider les données
        if (nom.isEmpty() || prenom.isEmpty() || mail.isEmpty()) {
            // Afficher un message d'erreur si les champs sont vides
            errorMessageLabel.setText("Veuillez remplir tous les champs.");
        } else if (!Utilisateur.isValidEmail(mail)) {
            // Afficher un message d'erreur si le format de l'email est invalide
            errorMessageLabel.setText("Veuillez entrer une adresse email valide.");
        } else {
            try {
                // Ajouter l'adhérent à la base de données
                DatabaseConnection.insertUserData(mail, prenom, nom);
                System.out.println("Nouvel adhérent ajouté : " + nom + " " + prenom);

                // Rafraîchissez les données des adhérents dans la table principale
                if (mainController != null) {
                    mainController.refreshUserData();
                } else {
                    System.out.println("MainController est null. Impossible de rafraîchir les données.");
                }

                // Rediriger vers la page principale après l'ajout de l'adhérent
                changeScene(event, "/main/views/pageprincipal.fxml", "Page Principale");

            } catch (SQLException e) {
                // Afficher un message d'échec
                errorMessageLabel.setText("Utilisateur déjà existant.");
            }
        }
    }

    public void setMainController(MainControllers mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleReturnButtonClick(ActionEvent event) {
        changeScene(event, "/main/views/pageprincipal.fxml", "Page Principale");
    }

    // Méthode utilitaire pour changer de scène
    private void changeScene(ActionEvent event, String fxmlFilePath, String title) {
        try {
            // Charge la vue de la page principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            // Obtient le stage actuel à partir de n'importe quel composant de la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Définit la nouvelle scène avec la racine chargée
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Optionnel : redéfinir le titre de la fenêtre
            stage.setTitle(title);

            // Affiche la scène principale
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
