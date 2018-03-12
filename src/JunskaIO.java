import java.io.*;

/**
 * Created by Martin on 3/30/2017.
 */
public class JunskaIO {

    public static void main(String[] args) throws IOException {
        String fromPath = "C:\\Users\\Martin\\Desktop\\in";
        String toPath = "C:\\Users\\Martin\\Desktop\\out";
        manage(fromPtah, toPath);
    }

    public static void manage(String in, String out) throws IOException {
        File fromFile = new File(in);

        if (fromFile.exists()) {
            File toFile = new File(out);
            if (toFile.exists()) {
                for(File fi : toFile.listFiles())
                    fi.delete();
            }

            File[] files = fromFile.listFiles((file, name) -> name.endsWith(".txt"));
            for (File file : files) {
                System.out.println(file.getName());
                if (file.canWrite()) {
                    String newPath = out + "\\" + file.getName();
                    file.renameTo(new File(newPath));
                    System.out.println("Pomestuvam " + file.getAbsolutePath());
                } else if (!file.canWrite()) {
                    String noPriviledgesFile = "C:\\Users\\Martin\\Desktop\\resource\\writable-content.txt";
                    InputStream reader = new FileInputStream(file);
                    OutputStream writer = new FileOutputStream(noPriviledgesFile, true);
                    int c;
                    while ((c=reader.read()) != -1) {
                        writer.write((char)c);
                    }
                    System.out.println("Dopisuvam " + file.getAbsolutePath());
                } else if (file.isHidden()) {
                    file.delete();
                    System.out.println("Zbunet sum");
                }
            }
        } else {
            System.out.println("Ne postoi");
        }
    }

}
