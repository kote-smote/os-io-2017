import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.Date;
import java.util.*;

/**
 *
 * @author ristes
 */
public class ProducerConsumer {

    public static int NUM_RUNS = 10;

    // TODO: Define all semaphores and other variables here
    static Semaphore fillSem;
    static Semaphore[] items;
    static final Object bufferAccess = new Object();

    /**
     * A method for initializing the values of the semaphores
     * and the other variables necessary.
     */
    public static void init() {
        fillSem = new Semaphore(1);
        int numberOfCustomers = state.getBufferCapacity();
        items = new Semaphore[numberOfCustomers];
        for (int i = 0; i < numberOfCustomers; i++)
            items[i] = new Semaphore(0);
    }

    static class Producer extends TemplateThread {

        public Producer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            fillSem.acquire();
            state.fillBuffer();
            // signal to the consumers that the buffer is filled
            for (Semaphore s : items)
                s.release();
        }
    }

    static class Consumer extends TemplateThread {
        private int cId;

        public Consumer(int numRuns, int id) {
            super(numRuns);
            cId = id;
        }

        @Override
        public void execute() throws InterruptedException {
            items[cId].acquire();
            state.getItem(cId);
            synchronized (bufferAccess) {
                state.decrementNumberOfItemsLeft();
                if (state.isBufferEmpty()) {
                    // signal the producer to fill the buffer
                    fillSem.release();
                }
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="This is the template code" >
    static State state;

    static class State extends AbstractState {

        private static final String _10_DVAJCA_ISTOVREMENO_PROVERUVAAT = "Two threads are checking the buffer at the same time. Only one is allowed to do that at a given time.";
        private static final String _10_KONZUMIRANJETO_NE_E_PARALELIZIRANO = "The consuming is not parallel..";
        private int bufferCapacity = 15;

        private BoundCounterWithRaceConditionCheck[] items;
        private BoundCounterWithRaceConditionCheck counter = new BoundCounterWithRaceConditionCheck(
                0);
        private BoundCounterWithRaceConditionCheck raceConditionTester = new BoundCounterWithRaceConditionCheck(
                0);
        private BoundCounterWithRaceConditionCheck bufferFillCheck = new BoundCounterWithRaceConditionCheck(
                0, 1, 10, "", null, 0, null);

        public int getBufferCapacity() {
            return bufferCapacity;
        }

        private int itemsLeft = 0;

        public State(int capacity) {
            bufferCapacity = capacity;
            items = new BoundCounterWithRaceConditionCheck[bufferCapacity];
            for (int i = 0; i < bufferCapacity; i++) {
                items[i] = new BoundCounterWithRaceConditionCheck(0, null, 0,
                        null, 0, 10, "You cannot get an item from an empty buffer.");
            }
        }

        public boolean isBufferEmpty() throws RuntimeException {
            log(raceConditionTester.incrementWithMax(), "Checking buffer state");
            boolean empty = (itemsLeft == 0);
            log(raceConditionTester.decrementWithMin(), null);
            return empty;
        }

        public void getItem(int index) {
            counter.incrementWithMax(false);
            log(items[index].decrementWithMin(), "Getting item");
            counter.decrementWithMin(false);
        }

        public void decrementNumberOfItemsLeft() {
            counter.incrementWithMax(false);
            synchronized (this) {
                itemsLeft--;
            }
            counter.decrementWithMin(false);
        }

        public void fillBuffer() {
            log(bufferFillCheck.incrementWithMax(), "Filling buffer");
            if (isBufferEmpty()) {
                for (int i = 0; i < bufferCapacity; i++) {
                    items[i].incrementWithMax();

                }
            } else {
                logException(new PointsException(10, "Filling non-empty buffer"));
            }
            synchronized (this) {
                itemsLeft = bufferCapacity;
            }
            log(bufferFillCheck.decrementWithMin(), null);
        }

        public void finalize() {
            if (counter.getMax() == 1) {
                logException(new PointsException(10,
                        _10_KONZUMIRANJETO_NE_E_PARALELIZIRANO));
            }
        }
    }

    public static void main(String[] args) {
        try {
            Scanner s = new Scanner(System.in);
            int brKonzumeri = s.nextInt();
            int numIterations = s.nextInt();
            s.close();

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < brKonzumeri; i++) {
                Consumer c = new Consumer(numIterations, i);
                threads.add(c);
            }
            Producer p = new Producer(numIterations);
            threads.add(p);

            state = new State(brKonzumeri);

            init();

            ProblemExecution.start(threads, state);

            // state.printLog();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // </editor-fold>
}

abstract class TemplateThread extends Thread {

    static boolean hasException = false;
    int numRuns = 1;
    public int iteration = 0;
    protected Exception exception = null;

    public TemplateThread(int numRuns) {
        this.numRuns = numRuns;
    }

    public abstract void execute() throws InterruptedException;

    @Override
    public void run() {
        try {
            for (int i = 0; i < numRuns && !hasException; i++) {
                execute();
                iteration++;

            }
        } catch (InterruptedException e) {
            // Do nothing
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
            hasException = true;
        }
    }

    public void setException(Exception exception) {
        this.exception = exception;
        hasException = true;
    }

    @Override
    public String toString() {
        Thread current = Thread.currentThread();
        if (numRuns > 1) {
            return String.format("[%d]%s\t%d\t%d", new Date().getTime(), ""
                            + current.getClass().getSimpleName().charAt(0), getId(),
                    iteration);
        } else {
            return String.format("[%d]%s\t%d\t", new Date().getTime(), ""
                    + current.getClass().getSimpleName().charAt(0), getId());
        }
    }
}

/**
 * This class should be extended in order to preserve the state of the executing
 * scenario
 *
 * @author ristes
 *
 */
abstract class AbstractState {

    /**
     * Method called after threads ended their execution to validate the
     * correctness of the scenario
     */
    public abstract void finalize();

    /**
     * List of logged actions
     */
    private List<String> actions = new ArrayList<String>();

    /**
     *
     * @return if the current thread is instance of TemplateThread it is
     *         returned, and otherwise null is returned
     */
    protected TemplateThread getThread() {
        Thread current = Thread.currentThread();
        if (current instanceof TemplateThread) {
            TemplateThread t = (TemplateThread) current;
            return t;
        } else {
            return null;
        }
    }

    /**
     * Log this exception or action
     *
     * @param e
     *            occurred exception (null if no exception)
     * @param action
     *            Description of the occurring action
     */
    public synchronized void log(PointsException e, String action) {
        TemplateThread t = (TemplateThread) Thread.currentThread();
        if (e != null) {
            t.setException(e);
            actions.add(t.toString() + "\t(e): " + e.getMessage());
            throw e;
        } else if (action != null) {
            actions.add(t.toString() + "\t(a): " + action);
        }
    }

    /**
     * Logging exceptions
     *
     * @param e
     */
    protected synchronized void logException(PointsException e) {
        Thread t = Thread.currentThread();
        if (e != null) {
            if (t instanceof TemplateThread) {
                ((TemplateThread) t).setException(e);
            }
            TemplateThread.hasException = true;
            actions.add("\t(e): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Printing of the actions and exceptions that has occurred
     */
    public synchronized void printLog() {
        System.out
                .println("Poradi konkurentnosta za pristap za pecatenje, mozno e nekoja od porakite da ne e na soodvetnoto mesto.");
        System.out.println("Log na izvrsuvanje na akciite:");
        System.out.println("=========================");
        System.out.println("tip\tid\titer\takcija/error");
        System.out.println("=========================");
        for (String l : actions) {
            System.out.println(l);
        }
    }

    /**
     * Prints the status of the execution, with the exceptions that has occur
     */
    public void printStatus() {
        try {
            finalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TemplateThread.hasException) {
            int poeni = 25;
            if (PointsException.getTotalPoints() == 0) {
                System.out
                        .println("Procesot e uspesno sinhroniziran. Osvoeni 25 poeni.");
            } else {
                poeni -= PointsException.getTotalPoints();
                PointsException.printErrors();
                System.out.println("Maksimalni osvoeni poeni: " + poeni);
            }

        } else {
            System.out
                    .println("Procesot ne e sinhroniziran spored uslovite na zadacata");
            printLog();
            System.out
                    .println("====================================================");
            PointsException.printErrors();
            int total = (25 - PointsException.getTotalPoints());
            if (total < 0) {
                total = 0;
            }
            System.out.println("Maksimum Poeni: " + total);
        }

    }
}

class PointsException extends RuntimeException {

    private static HashMap<String, PointsException> exceptions = new HashMap<String, PointsException>();
    private int points;

    public PointsException(int points, String message) {
        super(message);
        this.points = points;
        exceptions.put(message, this);
    }

    public static int getTotalPoints() {
        int sum = 0;
        for (PointsException e : exceptions.values()) {
            sum += e.getPoints();
        }
        return sum;
    }

    public static void printErrors() {
        if (!exceptions.isEmpty()) {
            System.out.println("Gi imate slednite greski: ");
            for (Map.Entry<String, PointsException> e : exceptions.entrySet()) {
                System.out.println(String.format("[%s] : (-%d)", e.getKey(), e
                        .getValue().getPoints()));
            }
        }
    }

    public int getPoints() {
        return points;
    }
}

abstract class ProblemExecution {

    public static void start(HashSet<Thread> threads, AbstractState state)
            throws Exception {

        startWithoutDeadlock(threads, state);

        checkDeadlock(threads, state);
    }

    public static void startWithoutDeadlock(HashSet<Thread> threads,
                                            AbstractState state) throws Exception {

        // start the threads
        for (Thread t : threads) {
            t.start();
        }

        // wait threads to finish
        for (Thread t : threads) {
            t.join(1000);
        }

    }

    private static void checkDeadlock(HashSet<Thread> threads,
                                      AbstractState state) {
        // check for deadlock
        for (Thread t : threads) {
            if (t.isAlive()) {
                t.interrupt();
                if (t instanceof TemplateThread) {
                    TemplateThread tt = (TemplateThread) t;
                    tt.setException(new PointsException(25, "DEADLOCK"));
                }
            }
        }

        // print the status
        state.printStatus();
    }

}

/**
 * This is helper class for incrementing and decrementing a integer variable,
 * with range validation. It also checks for race condition occurrence if needed
 *
 * @author ristes
 *
 */
class BoundCounterWithRaceConditionCheck {

    private static final int RACE_CONDITION_POINTS = 25;
    private static final String RACE_CONDITION_MESSAGE = "Race condition occured";

    private int value;
    private Integer maxAllowed;
    private Integer minAllowed;
    private int maxErrorPoints;
    private int minErrorPoints;
    private String maxErrorMessage;
    private String minErrorMessage;

    public static int raceConditionDefaultTime = 3;

    private int max;

    /**
     *
     * @param value
     */
    public BoundCounterWithRaceConditionCheck(int value) {
        super();
        this.value = value;
        this.max = value;
    }

    /**
     *
     * @param value
     *            initial value
     * @param maxAllowed
     *            upper bound of the value
     * @param maxErrorPoints
     *            how many points are lost with the max value constraint
     *            violation
     * @param maxErrorMessage
     *            message shown when the upper bound constrain is violated
     * @param minAllowed
     *            lower bound of the value
     * @param minErrorPoints
     *            how many points are lost with the min value constraint
     *            violation
     * @param minErrorMessage
     *            message shown when the lower bound constrain is violated
     */
    public BoundCounterWithRaceConditionCheck(int value, Integer maxAllowed,
                                              int maxErrorPoints, String maxErrorMessage, Integer minAllowed,
                                              int minErrorPoints, String minErrorMessage) {
        super();
        this.value = value;
        this.max = value;
        this.maxAllowed = maxAllowed;
        this.minAllowed = minAllowed;
        this.maxErrorPoints = maxErrorPoints;
        this.minErrorPoints = minErrorPoints;
        this.maxErrorMessage = maxErrorMessage;
        this.minErrorMessage = minErrorMessage;
    }

    /**
     *
     * @return the maximum value of the integer variable that occurred at some
     *         point of the execution
     */
    public int getMax() {
        return max;
    }

    /**
     *
     * @return the current value
     */
    public synchronized int getValue() {
        return value;
    }

    public synchronized void setValue(int value) {
        this.value = value;
    }

    /**
     * Throws exception when the val is different than the value of the counter.
     *
     * @param val
     * @param points
     * @param errorMessage
     * @return
     */
    public synchronized PointsException assertEquals(int val, int points,
                                                     String errorMessage) {
        if (this.value != val) {
            PointsException e = new PointsException(points, errorMessage);
            return e;
        } else {
            return null;
        }
    }

    public synchronized PointsException assertNotEquals(int val, int points,
                                                        String errorMessage) {
        if (this.value == val) {
            PointsException e = new PointsException(points, errorMessage);
            return e;
        } else {
            return null;
        }
    }

    /**
     * Testing for race condition. NOTE: there are no guarantees that the race
     * condition will be detected
     *
     * @return
     */
    public PointsException checkRaceCondition() {
        return checkRaceCondition(raceConditionDefaultTime,
                RACE_CONDITION_MESSAGE);
    }

    /**
     * Testing for race condition. NOTE: there are no guarantees that the race
     * condition will be detected, but higher the time argument is, the
     * probability for race condition occurrence is higher
     *
     * @return
     */
    public PointsException checkRaceCondition(int time, String message) {
        int val;

        synchronized (this) {
            val = value;
        }
        Switcher.forceSwitch(time);
        if (val != value) {
            PointsException e = new PointsException(RACE_CONDITION_POINTS,
                    message);
            return e;
        }
        return null;

    }

    public PointsException incrementWithMax() {
        return incrementWithMax(true);
    }

    public PointsException incrementWithMax(boolean checkRaceCondition) {
        if (checkRaceCondition) {
            PointsException raceCondition = checkRaceCondition();
            if (raceCondition != null) {
                return raceCondition;
            }
        }
        synchronized (this) {
            value++;

            if (value > max) {
                max = value;
            }
            if (maxAllowed != null) {
                if (value > maxAllowed) {
                    PointsException e = new PointsException(maxErrorPoints,
                            maxErrorMessage);
                    return e;
                }
            }
        }

        return null;
    }

    public PointsException decrementWithMin() {
        return decrementWithMin(true);
    }

    public PointsException decrementWithMin(boolean checkRaceCondition) {
        if (checkRaceCondition) {
            PointsException raceCondition = checkRaceCondition();
            if (raceCondition != null) {
                return raceCondition;
            }
        }

        synchronized (this) {
            value--;
            if (minAllowed != null) {
                if (value < minAllowed) {
                    PointsException e = new PointsException(minErrorPoints,
                            minErrorMessage);
                    return e;
                }
            }
        }
        return null;
    }

}

class Switcher {
    private static final Random RANDOM = new Random();

    /*
     * This method pauses the current thread i.e. changes its state to be
     * Blocked. This should force thread switch if there are threads waiting
     */
    public static void forceSwitch(int range) {
        try {
            Thread.sleep(RANDOM.nextInt(range));
        } catch (InterruptedException e) {
        }
    }
}