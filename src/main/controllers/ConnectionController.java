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

/**
 * Controller class for handling user login and transitioning to the main page.
 */
public class ConnectionController extends Application {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private Stage primaryStage;

    /**
     * Sets the primary stage of the application.
     *
     * @param primaryStage the primary stage
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Toggles full-screen mode.
     */
    public void toggleFullScreen() {
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }

    /**
     * Handles the login process.
     * Authenticates the user and transitions to the main page if successful.
     */
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Call the authentication function
        boolean isAuthenticated = Authentication.authenticate(username, password);

        // Check if authentication is successful
        if (isAuthenticated) {
            // Load the new page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/MainPage.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Main Page");

                // Add event handler to close the application when the window is closed
                stage.setOnCloseRequest(event -> {
                    // Code to execute when the window is closed
                    Platform.exit(); // Close the application
                });

                stage.show();
            } catch (javafx.fxml.LoadException e) {
                e.printStackTrace(); // Display an error message
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Close the current login window
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            currentStage.close();
        } else {
            // Display a failure message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Incorrect username or password.");
            alert.showAndWait();
        }
    }

    /**
     * Starts the JavaFX application.
     *
     * @param primaryStage the primary stage
     * @throws Exception if an error occurs during loading
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("/main/views/connection.fxml"));

        // Create a scene
        Scene scene = new Scene(root, 1280, 720);

        // Set the scene and display the main window
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
