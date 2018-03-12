import java.util.*;

/**
 * So pomos na sinhronizaciskite metodi da se resi problemot za opredeluvanje na brojot na pojavuvanja na brojot 3 vo
 * ogromna niza i negovo zapisuvanje vo globana promenliva count.
 *
 * Sekvencijalnoto resenie ne e prifatlivo poradi toa sto trae mnogu dolgo vreme (poradi goleminata na nizata).
 * Za taa cel, potrebno e da se parallelizira ovoj proces, pri sto treba da se napise metoda koja ke gi broi
 * pojavuvanjata na brojot 3 vo pomal fragment od nizata, pri sto rezultato povtorno se cuva vo globalnata promenliva
 * count.
 *
 * Napomena: Pocetniot kod e daden vo pocention kod CountTree.java. Zadacata da se testira nad niza od minimun
 * 1000 elementi.
 */
public class CountThree {

    public static int NUM_RUNS = 100;
    /**
     * Promenlivata koja treba da go sodrzi brojot na pojavuvanja na elementot 3
     */
    int count = 0;
    /**
     * TODO: definirajte gi potrebnite elementi za sinhronizacija
     */
    Object lock = new Object();

    public void init() {
    }

    class Counter extends Thread {

        public void count(int[] data) throws InterruptedException {
            // Implementiraj go metodot
            long localCount = Arrays.stream(data).filter(x -> x == 3).count();
            synchronized (lock) {
                count += localCount;
            }
        }
        private int[] data;

        public Counter(int[] data) {
            this.data = data;
        }

        @Override
        public void run() {
            try {
                count(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            CountThree environment = new CountThree();
            environment.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void start() throws Exception {
        init();

        HashSet<Thread> threads = new HashSet<Thread>();
        Scanner s = new Scanner(System.in);
        int total=s.nextInt();
        Random rand = new Random();
        List<Integer> ls = new ArrayList<>();

        for (int i = 0; i < NUM_RUNS; i++) {
            int[] data = new int[total];
            for (int j = 0; j < total; j++) {
//                data[j] = s.nextInt();
                data[j] = rand.nextInt(50);
                ls.add(data[j]);
            }
            Counter c = new Counter(data);
            threads.add(c);
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println(count);
    }
}
