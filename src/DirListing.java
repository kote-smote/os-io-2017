import java.io.File;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by martin on 3/2/17.
 */
public class DirListing {

    public static void main(String[] args) {
        File f = new File("."); // gets the current folder
        listFile("/home/martin/Dropbox/Finki Semestar 4", "\t");


    }

    public static void listFile(String absolutePath, String prefix) {
        File file = new File(absolutePath);

        if (file.exists()) {
            File[] subfiles = file.listFiles();
            for (File f : subfiles) {
                System.out.println(prefix + getPermissions(f) + "\t" + f.getName());
                // Recursively show the content of sub-directories
                if (f.isDirectory())
                    listFile(f.getAbsolutePath(), prefix + "\t");
            }
        }
    }

    /*
     * Get the permissions of a file in a unix-like format
     */
    public static String getPermissions(File f) {
        return String.format("%s%s%s", f.canRead() ? "r" : "-", f.canWrite() ? "w" : "-",
                f.canExecute() ? "x" : "-");
    }
}
