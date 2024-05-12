package main.models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public class Authentication {

    // Méthode pour vérifier l'authentification à partir d'un fichier texte
    public static boolean authenticate(String username, String password) {
        // Chemin vers le fichier contenant les informations d'identification
        String filePath = "/main/ressources/users.txt";

        try (InputStream inputStream = Authentication.class.getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = br.readLine()) != null) {
                // Divise la ligne en utilisant le caractère ":" comme séparateur
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];

                    // Vérifie si l'utilisateur et le mot de passe correspondent
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return true; // Authentification réussie
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // Authentification échouée
    }

}
