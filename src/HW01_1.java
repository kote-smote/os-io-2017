import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by martin on 3/2/17.
 */
public class HW01_1 {

    public static void main(String[] args) {
        if (args.length > 0) {
            String path = args[0];
            try {
                double avgSize = getFileAverageSize(path);
                System.out.println(avgSize);
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static double getFileAverageSize(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles((File f, String s) -> s.indexOf(".txt") == (s.length() - 4));

            return Arrays.stream(files)
                    .mapToLong(f -> f.length())
                    .average()
                    .getAsDouble();
        }
        throw new IOException("The file '" + path + "' does not exist");
    }
}
