package main.controllers;

import main.models.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import main.models.DatabaseConnection;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;

import java.sql.SQLException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class NewAdherentController {

    @FXML
    private TextArea nomTextArea;

    @FXML
    private TextArea prenomTextArea;

    @FXML
    private TextArea mailTextArea;

    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label mailLabel;

    @FXML
    private Label nbEmpruntsLabel;

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
            System.out.println("Veuillez remplir tous les champs.");
        } else if (!Utilisateur.isValidEmail(mail)) {
            // Afficher un message d'erreur si le format de l'email est invalide
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer une adresse email valide.");
            alert.showAndWait();
            System.out.println("Format d'email invalide.");
        } else {
            try {
                // Ajouter l'adhérent à la base de données
                DatabaseConnection.insertUserData(mail, prenom, nom);
                System.out.println("Nouvel adhérent ajouté : " + nom + " " + prenom);

                // Fermer la fenêtre après l'ajout de l'adhérent
                Stage stage = (Stage) nomTextArea.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                // Afficher un message d'échec
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERREUR");
                alert.setHeaderText(null);
                alert.setContentText("Utilisateur deja existant.");
                alert.showAndWait();
                System.out.println("Utilisateur deja existant");
            }

            // Rafraîchissez les données des adhérents dans la table principale
            if (mainController != null) {
                mainController.refreshUserData();
            } else {
                System.out.println("MainController est null. Impossible de rafraîchir les données.");
            }
        }
    }



    public void setMainController(MainControllers mainController) {
        this.mainController = mainController;
    }

}

