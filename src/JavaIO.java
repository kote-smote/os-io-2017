import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class JavaIO {

    public static void main(String[] args) throws IOException {
        String from = "C:\\Users\\Martin\\Desktop\\from";
        String to = "C:\\Users\\Martin\\Desktop\\to";
        moveWritableTxtFiles(from, to);
        List<byte[]> data = new ArrayList<>();
        deserializeData(to+"\\mario.txt", data, 3);
        invertLargeFile(to+"\\mario.txt", from+"\\"+"updated.txt");
    }

    public static void moveWritableTxtFiles(String from, String to) {
        File file = new File(from);
        if (file.exists()) {
            File[] files = file.listFiles();
            File destFile = new File(to);
            if (!destFile.exists())
                file.mkdirs();
            for (File f : files) {
                if (f.getName().endsWith(".txt") && f.canWrite()) {
                    String newPath = to + "\\" + f.getName();
                    f.renameTo(new File(newPath));
                }
            }

        } else {
            System.out.println("Ne postoi");
        }
    }

    static void deserializeData(String source, List<byte[]> data, long elementLength) throws IOException {
        InputStream in = new FileInputStream(source);
        while (true) {
            byte[] arr = new byte[(int)elementLength];
            if(in.read(arr, 0, (int)elementLength)==-1) {
                break;
            }
            data.add(arr);
        }
        for (byte[] a : data) {
            for (byte b : a) {
                System.out.print((char)b);
            }
            System.out.println();
        }
    }

    static void invertLargeFile(String source, String destination) throws IOException {
        System.setProperty("line.separator", "\r\n");
        File fromFile = new File(source);
        RandomAccessFile raf = new RandomAccessFile(fromFile, "r");
        OutputStream out = new FileOutputStream(destination);
        int position = (int)fromFile.length()-1;
        while (position>=0) {
            raf.seek(position--);
            out.write(raf.read());
        }
    }
}
