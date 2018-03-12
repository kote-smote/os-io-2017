import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by martin on 3/5/17.
 */
public class HW01_2 {
    public static void main(String[] args) throws IOException {
        Stack<Integer> stack = new Stack<>();
        InputStream in = new FileInputStream("data/izvor.txt");
        OutputStream out = new FileOutputStream("destinacija.txt");
        try {
            int c;
            while ((c = in.read()) != -1) {
                stack.push(c);
            }
            while (!stack.isEmpty()) {
                out.write((char)(int)stack.pop());
            }
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }
}

class Stack<T> {
    private List<T> ls;

    public Stack() {
        ls = new LinkedList<T>();
    }

    public void push(T item) {
        if (item != null)
            ls.add(ls.size(), item);
        else
            throw new NullPointerException();
    }

    public T pop() {
        if (ls.size() > 0) {
            return ls.remove(ls.size()-1);
        }
        else
            return null;
    }

    public boolean isEmpty() {
        return ls.size() == 0;
    }

}
