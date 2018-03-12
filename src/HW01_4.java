import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by martin on 3/2/17.
 */
public class HW01_4 {
    public static void main(String[] args) {
        if (args.length == 2) {
            String path = args[0];
            String query = args[1];
            try {
                long count = countWordRepeatancesInFile(path, query);
                System.out.println(count);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static long countWordRepeatancesInFile(String path, String word) throws IOException {
        long count = 0;
        BufferedReader bf = new BufferedReader(new FileReader(path));
        String line = null;
        while ((line = bf.readLine()) != null) {
            String[] words = line.split(" ");
            for (String wd : words) {
                if (wd.equals(word))
                    count++;
            }
        }
        bf.close();
        return count;
    }
}
