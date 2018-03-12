import java.io.*;

/**
 * Created by martin on 3/6/17.
 */
public class HW01_3 {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("data/izvor.txt"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("destinacija.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            line = new StringBuilder(line).reverse().toString();
            writer.write(line);
        }
        writer.flush();
        reader.close();
        writer.close();
    }
}
