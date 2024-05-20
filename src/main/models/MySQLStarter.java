package main.models;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MySQLStarter {

    public static void startMySQL() {
        String[] command = {"cmd.exe", "/c", "net start mysql"};

        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}