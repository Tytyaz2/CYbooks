package main.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import main.models.DatabaseConnection;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;

import java.sql.SQLException;

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
        } else {
            // Ajouter l'adhérent à la base de données
            DatabaseConnection.insertUserData(nom, prenom, mail);
            System.out.println("Nouvel adhérent ajouté : " + nom + " " + prenom);

            // Fermer la fenêtre après l'ajout de l'adhérent
            Stage stage = (Stage) nomTextArea.getScene().getWindow();
            stage.close();

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
