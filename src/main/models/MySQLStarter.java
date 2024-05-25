package main.models;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility class to start the MySQL and Apache services of WampServer.
 * This class provides a method to start MySQL and Apache services using command-line commands.
 */
public class MySQLStarter {

    /**
     * Starts the MySQL and Apache services of WampServer.
     * Executes command-line commands to start the MySQL and Apache services.
     */
    public static void startMySQL() {
        // Command to start MySQL service
        String[] command = {"cmd.exe", "/c", "net start wampmysqld64"};
        // Command to start Apache service
        String[] command2 = {"cmd.exe", "/c", "net start wampapache64"};

        try {
            // Create ProcessBuilder instances for executing commands
            ProcessBuilder pb2 = new ProcessBuilder(command2);
            ProcessBuilder pb = new ProcessBuilder(command);
            // Redirect error stream for both processes
            pb2.redirectErrorStream(true);
            pb.redirectErrorStream(true);
            // Start the Apache service process
            Process process2 = pb2.start();
            // Start the MySQL service process
            Process process = pb.start();

            // Read and print the output of the Apache service process
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
            String line2;
            while ((line2 = reader2.readLine()) != null) {
                System.out.println(line2);
            }

            // Read and print the output of the MySQL service process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for both processes to complete
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
