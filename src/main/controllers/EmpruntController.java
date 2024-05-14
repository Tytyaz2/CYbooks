package main.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EmpruntController {

    @FXML
    private TextField textFieldTitre;

    @FXML
    private TextField textFieldAuteur;

    @FXML
    private TextField textFieldDateEmprunt;

    @FXML
    private Button buttonEmprunter;

    // Méthode appelée lors du clic sur le bouton "Emprunter"
    @FXML
    private void handleEmpruntButtonClick() {
        // Insérez ici le code pour gérer l'emprunt du livre
        System.out.println("Bouton Emprunter cliqué !");
    }

    // Autres méthodes et fonctionnalités de votre contrôleur
}
