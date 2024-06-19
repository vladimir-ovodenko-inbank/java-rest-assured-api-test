package ee.inbank.rd.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtil {

    // Method to read JSON file as String
    public static String readJsonFromFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            System.out.println("Error while reading file" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
