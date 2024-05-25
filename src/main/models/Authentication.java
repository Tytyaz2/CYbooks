package main.models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * The Authentication class provides a method to verify user authentication
 * using credentials stored in a text file.
 */
public class Authentication {

    /**
     * Verifies authentication from a text file.
     *
     * @param username the username to authenticate
     * @param password the password to authenticate
     * @return true if the username and password match an entry in the text file, false otherwise
     */
    public static boolean authenticate(String username, String password) {
        // Path to the file containing authentication information
        String filePath = "/main/resources/users.txt";

        try (InputStream inputStream = Authentication.class.getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = br.readLine()) != null) {
                // Split the line using ":" as a separator
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String storedUsername = parts[0];
                    String storedPassword = parts[1];

                    // Check if the username and password match
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        return true; // Authentication successful
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false; // Authentication failed
    }

}
