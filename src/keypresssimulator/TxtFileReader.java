package keypresssimulator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TxtFileReader {
	
    public static String readFile(String fileName) {
        
        StringBuilder text = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(fileName);
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
