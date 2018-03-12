import java.util.HashSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by martin on 3/4/17.
 */
public class ThreadsScopeAndRaceConditions {

    public static void main(String[] args) {
        HashSet<ExampleThread> threads = new HashSet<>();
        IntegerWrapper sharedWrapper = new IntegerWrapper();

        // Shuffle the threads using HashSet
        for (int i = 0; i < 100; i++) {
            ExampleThread t = new ExampleThread(0, sharedWrapper);
            threads.add(t);
        }

        // Execute each threads in background
        for (Thread t : threads)
            t.start();

        // Modify thread variables
        for (ExampleThread t : threads) {
            t.publicFieldIncrement();
            t.wrapperIncrement();
//            t.safePublicFieldIncrement(); // Also uncomment in ExampleThread.run()
//            t.safeWrapperIncrement(); // Also uncomment in ExampleThread.run()
        }
    }
}

class ExampleThread extends Thread {
    private IntegerWrapper wrapper; // will get reference to some object, which becomes shared
    private int threadLocalField = 0; // visible by this thread only and is not shared. No need for protection
    public int threadPublicField = 0; // can be accessed from other threads and shoud be protected when used

    public ExampleThread(int init, IntegerWrapper iw) {
        this.threadLocalField = init; // init is of primitive type and thus is not shared
        this.wrapper = iw; // even though wrapper is private, it can be shared, since iw is reference
    }

    private void privateFieldIncrement() {
        threadLocalField++; // only this thread can acces this field
        int localVar = threadLocalField; // localVar is visible only in this method (not shared)
        forceSwitch(30);
        // check for race condition (Will it ever occur?)
        if (localVar != threadLocalField) {
            System.err.format("private-mismatch-%d\n", getId());
        } else {
            System.out.format("[private-%d] %d\n", getId(), threadLocalField);
        }
    }

    public void publicFieldIncrement() {
        int localVar = ++threadPublicField;
        forceSwitch(10);
        // Check for race condition
        if (localVar != threadPublicField) {
            System.err.format("public-mismatch-%d\n", getId());
        } else {
            System.out.format("[public-%d] %d\n", getId(), threadPublicField);
        }
    }

    public void wrapperIncrement() {
        this.wrapper.increment();
        int localVar = wrapper.getVal();
        forceSwitch(3);
        // Check for race condition (It will be common)
        if (localVar != wrapper.getVal()) {
            System.err.format("wrapper-mismatch-%d\n", getId());
        } else {
            System.out.format("[wrapper-%d] %d\n", getId(), wrapper.getVal());
        }
    }

    private void forceSwitch(int sleepTime) {
        try {
            Thread.sleep(sleepTime); // force thread switching
        } catch (InterruptedException e) {/* Do Nothing */}
    }

    // Protecting Class Fields
    public Lock lock = new ReentrantLock();
    public Semaphore binarySemaphore = new Semaphore(1);
    public void safePublicFieldIncrement() {
        // Solution #1
        synchronized (this) {
            publicFieldIncrement();
        }
        /*
        //Solution #2
        lock.lock();
        publicFieldIncrement();
        lock.unlock();
        */
        /*
        // Solution #3
        try {
            binarySemaphore.acquire();
            publicFieldIncrement();
        } catch (InterruptedException e) {
        } finally {
            binarySemaphore.release();
        }
        */
    }

    //Protecting Shared Objects
    public static Lock staticLock = new ReentrantLock();
    public static Semaphore stacicBinarySemapthore = new Semaphore(1);
    public void safeWrapperIncrement() {
        // Solution #1
//        synchronized (wrapper) {
//            wrapperIncrement();
//        }
        /*
        //Solution #2
        staticLock.lock();
        wrapperIncrement();
        staticLock.unlock();
        */

        // Solution #3
        try {
            stacicBinarySemapthore.acquire();
            wrapperIncrement();
        } catch (InterruptedException e) {
        } finally {
            stacicBinarySemapthore.release();
        }

    }

    @Override
    public void run() {
        privateFieldIncrement();
        publicFieldIncrement();
        wrapperIncrement();
//        safePublicFieldIncrement();
//        safeWrapperIncrement();
    }
}

class IntegerWrapper {
    private int val = 0;
    public void increment() { this.val++; }
    public int getVal() { return this.val; }
}
