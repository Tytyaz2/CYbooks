package main.API;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * OperationOnXMLFIle is a utility class for file operations, specifically for creating
 * an XML file and writing API responses to it.
 */
public class OperationOnXMLFIle {

    /**
     * Create a new XML file with the specified filename.
     * @param filename The name of the XML file to create.
     */
    public static void createFile(String filename) {
        try {
            File file = new File(filename);
            if (file.createNewFile()) {
                System.out.println("XML file created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the XML file.");
        }
    }

    /**
     * Write API response content to an XML file.
     * @param filename The name of the XML file to write to.
     * @param content The API response content to write to the file.
     */
    public static void writeFile(String filename, String content) {
        try {
            FileWriter writer = new FileWriter(filename);
            writer.write(content);
            writer.close();
            System.out.println("The API response is written to: " + filename);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the XML file.");
        }
    }
}
