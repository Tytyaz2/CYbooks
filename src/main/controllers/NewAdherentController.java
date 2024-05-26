package main.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import main.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import main.dataBase.DatabaseConnection;
import javafx.scene.control.Label;
import java.io.IOException;
import java.sql.SQLException;

public class NewAdherentController {

    @FXML
    private TextArea lastNameTextArea;

    @FXML
    private TextArea firstNameTextArea;

    @FXML
    private TextArea mailTextArea;

    @FXML
    private Label errorMessageLabel;

    private MainControllers mainController;


    /**
     * Handles the event when a new user is added.
     *
     * @param event the ActionEvent triggered by adding a new user
     * @throws SQLException if a database access error occurs
     */
    @FXML
    void handleAddNewUser(ActionEvent event) throws SQLException {
        // Get the values from text fields
        String lastname = lastNameTextArea.getText();
        String firstname = firstNameTextArea.getText();
        String mail = mailTextArea.getText();


        // Validate the data
        if (lastname.isEmpty() || firstname.isEmpty() || mail.isEmpty()) {
            // Display error message if fields are empty
            errorMessageLabel.setText("Veuillez remplir tous les champs.");
        } else if (!User.isValidEmail(mail)) {
            // Display error message if email format is invalid
            errorMessageLabel.setText("Veuillez entrer une adresse email valide.");
        } else {
            try {
                // Add the user to the database
                DatabaseConnection.insertUserData(new User(mail, firstname, lastname, 0,5));
                System.out.println("Nouvel adhérent ajouté : " + lastname + " " + firstname);

                // Refresh user data in the main table
                if (mainController != null) {
                    mainController.refreshUserData();
                } else {
                    System.out.println("MainController est null. Impossible de rafraîchir les données.");
                }

                // Redirect to the main page after adding the user
                changeScene(event, "/main/views/MainPage.fxml", "Page Principale");

            } catch (SQLException e) {
                // Display failure message
                errorMessageLabel.setText("User déjà existant.");
            }
        }
    }

    /**
     * Sets the main controller for this controller.
     *
     * @param mainController the MainControllers instance
     */

    public void setMainController(MainControllers mainController) {
        this.mainController = mainController;
    }


    /**
     * Handles the event when the return button is clicked.
     *
     * @param event the ActionEvent triggered by clicking the return button
     */
    @FXML
    private void handleReturnButtonClick(ActionEvent event) {
        changeScene(event, "/main/views/MainPage.fxml", "Page Principale");
    }

    /**
     * Utility method to change the scene.
     *
     * @param event        the ActionEvent triggered by changing the scene
     * @param fxmlFilePath the path of the FXML file for the new scene
     * @param title        the title of the new scene
     */
    private void changeScene(ActionEvent event, String fxmlFilePath, String title) {
        try {
            // Load the view of the main page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Parent root = loader.load();

            // Get the current stage from any component of the scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene with the loaded root
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Optional: set the window title
            stage.setTitle(title);

            // Show the main scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
