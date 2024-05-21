package main.models;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MySQLStarter {

    public static void startMySQL() {
        String[] command = {"cmd.exe", "/c", "net start wampmysqld64 "};
        String[] command2 = {"cmd.exe", "/c", "net start wampapache64 "};

        try {
            ProcessBuilder pb2 = new ProcessBuilder(command2);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb2.redirectErrorStream(true);
            pb.redirectErrorStream(true);
            Process process2 = pb2.start();
            Process process = pb.start();

            BufferedReader reader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line2;
            while ((line2 = reader2.readLine()) != null){
                System.out.println(line2);

            }

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