/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 15: Multithreading:
 *              - Implementing the java.lang.Runnable interface.
 *              - Inter-thread communication using wait() and notifyAll().
 *              - Synchronized blocks on a shared monitor object.
 *              - Safe cooperative shutdown.
 */
public class BackgroundAlertTask implements Runnable {

    // Module 15: Shared monitor object for inter-thread communication
    private final Object alertLock = new Object();

    // In-memory alert buffer (demonstrating simple array queue without external collections)
    private final String[] alertQueue = new String[50];
    private int head = 0;
    private int tail = 0;
    private int alertCount = 0;

    private volatile boolean running = true;
    private String lastProcessedAlert = "None";

    /**
     * Module 15: Producer method called by BackgroundInventoryCheck or LibraryService.
     * Pushes an alert and calls notifyAll() to wake up the waiting consumer thread.
     */
    public void postAlert(String alertMessage) {
        synchronized (alertLock) {
            if (alertCount < alertQueue.length) {
                alertQueue[tail] = alertMessage;
                tail = (tail + 1) % alertQueue.length;
                alertCount++;
                // Module 15: Inter-thread communication - notify waiting consumer thread
                alertLock.notifyAll();
                System.out.println("[AlertTask Monitor] Alert posted and waiting threads notified: " + alertMessage);
            }
        }
    }

    /**
     * Module 15: Runnable's run() method.
     * Consumer loop that waits on the monitor until an alert is posted or thread is stopped.
     */
    @Override
    public void run() {
        System.out.println("[AlertTask-Consumer] BackgroundAlertTask started on thread: " +
                           Thread.currentThread().getName());

        while (running) {
            String currentAlert = null;

            // Module 15: Synchronized block protecting the shared queue
            synchronized (alertLock) {
                // While no alerts are available, wait on the monitor object
                while (alertCount == 0 && running) {
                    try {
                        // Module 15: wait() releases the lock and suspends the thread
                        alertLock.wait();
                    } catch (InterruptedException e) {
                        System.out.println("[AlertTask-Consumer] Interrupted while waiting on monitor.");
                        running = false;
                        break;
                    }
                }

                if (!running) {
                    break;
                }

                if (alertCount > 0) {
                    currentAlert = alertQueue[head];
                    head = (head + 1) % alertQueue.length;
                    alertCount--;
                }
            }

            // Process the alert outside the synchronized block to minimize lock contention
            if (currentAlert != null) {
                this.lastProcessedAlert = currentAlert;
                System.out.println("[AlertTask-Consumer] Processed and dispatched alert: " + currentAlert);
            }
        }

        System.out.println("[AlertTask-Consumer] BackgroundAlertTask stopped cleanly.");
    }

    /**
     * Module 15: Safe cooperative shutdown.
     */
    public void stopTask() {
        this.running = false;
        synchronized (alertLock) {
            // Wake any thread stuck in wait()
            alertLock.notifyAll();
        }
    }

    public String getLastProcessedAlert() {
        return lastProcessedAlert;
    }

    public boolean isRunning() {
        return running;
    }
}
