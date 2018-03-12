import java.io.*;

/**
 * Created by martin on 3/2/17.
 */
public class DataReadWrite {

    public static void main(String[] args) throws IOException {
        String path = "/home/martin/Dropbox/Projects/IntelliJ Projects/Operating_Systems_Finki_2017/data/Data.txt";
        dataReadWrite(path);
    }

    public static void dataReadWrite(String path) throws IOException {
        DataInputStream in = null;
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
            out.writeDouble(3.14);
            out.writeUTF("Ova e teks");
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
//            System.out.println(in.readDouble()); // TODO doesn't work!!
            // Only readUTF() will recover the Java-UTF String properly
//            System.out.println(in.readUTF()); // TODO doesn't work
        } finally {
            if (out != null) out.close();
            if (in != null) in.close();
        }
    }
}
