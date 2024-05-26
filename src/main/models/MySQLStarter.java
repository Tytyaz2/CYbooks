package main.models;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

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
        String[] commandMySQL = {"cmd.exe", "/c", "net start wampmysqld64"};
        // Command to start Apache service
        String[] commandApache = {"cmd.exe", "/c", "net start wampapache64"};

        // Check and start Apache service
        if (!isServiceRunning("wampapache64")) {
            startService(commandApache, "Apache");
        } else {
            System.out.println("Apache service is already running.");
        }

        // Check and start MySQL service
        if (!isServiceRunning("wampmysqld64")) {
            startService(commandMySQL, "MySQL");
        } else {
            System.out.println("MySQL service is already running.");
        }
    }

    /**
     * Checks if a service is running.
     *
     * @param serviceName the name of the service to check
     * @return true if the service is running, false otherwise
     */
    private static boolean isServiceRunning(String serviceName) {
        String[] command = {"cmd.exe", "/c", "sc query " + serviceName};
        try {
            Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("RUNNING")) {
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Starts a service.
     *
     * @param command     the command to start the service
     * @param serviceName the name of the service
     */
    private static void startService(String[] command, String serviceName) {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        try {
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(serviceName + ": " + line);
            }
            reader.close();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println(serviceName + " service started successfully.");
            } else {
                System.err.println(serviceName + " service failed to start with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to start " + serviceName + " service.");
            e.printStackTrace();
        }
    }
}