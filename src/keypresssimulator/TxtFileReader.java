package keypresssimulator;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TxtFileReader {

    public static String readFile(String fileName) {

        StringBuilder text = new StringBuilder();
        try {
            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                text.append(line).append("\r\n");
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
        return text.toString();
    }
}
