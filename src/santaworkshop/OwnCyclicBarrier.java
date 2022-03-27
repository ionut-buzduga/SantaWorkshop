package santaworkshop;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * implementation o barrier with locks 
 */

public class OwnCyclicBarrier {
    private final int parties;
    private final Lock lock = new ReentrantLock();;
    private int arrived = 0;

    /**
     * Constructor. Initializes fields
     * @param parties the total elves that must wait
     */
    public OwnCyclicBarrier(int parties) {
        this.parties = parties;
    }

    /**
     * If an elf calls await method he will have to wait untill other n-1 elves call it or the app stops
     */
    public void await() {
        try {
            lock.lock();
            arrived++;
            doWait();
        } finally {
            lock.unlock();
        }
    }

    /**
     * This is the function that does the actual waiting. When it finishes, it resets the barrier.
     */
    private synchronized void doWait() {
        while (arrived != parties && Workshop.CURRENT_NO_OF_PRESENTS<Workshop.NO_OF_PRESENTS_TO_ACHIEVE_GOAL) {
            try {
                wait(100, 100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        notifyAll();
        arrived = 0;

    }

}
