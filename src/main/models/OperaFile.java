package main.models;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * OperaFile is a utility class for file operations.
 */
public class OperaFile {

    /**
     * Create a new file with the specified filename.
     * @param filename The name of the file to create.
     */
    public static void createFile(String filename) {
        try {
            File file = new File(filename);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
        }
    }

    /**
     * Write content to a file.
     * @param filename The name of the file to write to.
     * @param content The content to write to the file.
     */
    public static void writeFile(String filename, String content) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(content);
            writer.close();
            System.out.println("Successfully wrote to the file: " + filename);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
