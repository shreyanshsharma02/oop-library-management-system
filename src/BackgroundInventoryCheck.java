/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 15: Multithreading:
 *              - Extending the java.lang.Thread class.
 *              - Thread lifecycle (NEW, RUNNABLE, TIMED_WAITING, TERMINATED).
 *              - start() vs run() difference (start allocates a new call stack; run runs on caller's thread).
 *              - join() to wait for thread termination before proceeding.
 *              - Thread priorities (Thread.MIN_PRIORITY to Thread.MAX_PRIORITY).
 *              - Synchronization with 'synchronized' block on shared LibraryService.
 *              - Safe cooperative termination using a 'volatile boolean' flag and interruption.
 */
public class BackgroundInventoryCheck extends Thread {

    /**
     * Callback interface to notify GUI or console when an inventory check cycle completes.
     */
    public interface InventoryListener {
        void onStockAlert(String message, boolean isCritical);
        void onCheckCompleted(int outOfStockCount, int lowStockCount, long durationMillis);
    }

    private final LibraryService libraryService;
    private final InventoryListener listener;
    private final int checkIntervalMillis;

    // Module 15: Safe cooperative thread cancellation flag
    // 'volatile' guarantees thread visibility across CPU cores without caching
    private volatile boolean running = true;

    // Optional shared alert task for inter-thread communication
    private BackgroundAlertTask alertTask;

    /**
     * Parameterized constructor.
     * Demonstrates naming the thread for easier debugging and lifecycle tracking.
     */
    public BackgroundInventoryCheck(LibraryService libraryService, InventoryListener listener, int checkIntervalMillis) {
        super("BackgroundInventoryCheck-Thread"); // Sets thread name
        this.libraryService = libraryService;
        this.listener = listener;
        this.checkIntervalMillis = checkIntervalMillis > 0 ? checkIntervalMillis : 10000;

        // Module 15: Thread priority demonstration
        // Set priority to slightly above normal for background inventory monitoring
        this.setPriority(Thread.NORM_PRIORITY);
    }

    public void setAlertTask(BackgroundAlertTask alertTask) {
        this.alertTask = alertTask;
    }

    /**
     * Module 15: Overriding the run() method.
     * Contains the core execution logic executed concurrently when start() is invoked.
     */
    @Override
    public void run() {
        System.out.println("[Thread: " + getName() + "] Started with Priority=" + getPriority() +
                           ", State=" + getState());

        while (running && !isInterrupted()) {
            long startTime = System.currentTimeMillis();
            int outOfStockCount = 0;
            int lowStockCount = 0;

            // Module 15: Thread Synchronization
            // Protect shared inventory array using a synchronized block on libraryService
            synchronized (libraryService) {
                Book[] allBooks = libraryService.getAllBooks();
                for (int i = 0; i < allBooks.length; i++) {
                    Book b = allBooks[i];
                    if (b != null) {
                        int available = b.getAvailableCopies();
                        if (available == 0) {
                            outOfStockCount++;
                            String alert = "OUT OF STOCK ALERT: [" + b.getBookId() + "] '" + b.getTitle() + "' has 0 copies available!";
                            if (listener != null) {
                                listener.onStockAlert(alert, true);
                            }
                            // Notify alert task via inter-thread communication
                            if (alertTask != null) {
                                alertTask.postAlert(alert);
                            }
                        } else if (available < 2) {
                            lowStockCount++;
                            String alert = "LOW STOCK WARNING: [" + b.getBookId() + "] '" + b.getTitle() + "' has only " + available + " copy left!";
                            if (listener != null) {
                                listener.onStockAlert(alert, false);
                            }
                            if (alertTask != null) {
                                alertTask.postAlert(alert);
                            }
                        }
                    }
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;
            if (listener != null) {
                listener.onCheckCompleted(outOfStockCount, lowStockCount, elapsed);
            }

            // Sleep until next scheduled interval
            try {
                // Thread enters TIMED_WAITING state
                Thread.sleep(checkIntervalMillis);
            } catch (InterruptedException e) {
                // Module 15: Proper interruption handling
                System.out.println("[Thread: " + getName() + "] Interrupted during sleep. Terminating cooperatively.");
                running = false;
                break;
            }
        }

        System.out.println("[Thread: " + getName() + "] Finished. Final State=" + getState());
    }

    /**
     * Module 15: Safe cooperative thread termination.
     * Avoids deprecated Thread.stop()! Sets the volatile boolean flag and interrupts the thread.
     */
    public void stopCheck() {
        this.running = false;
        this.interrupt(); // Wakes thread if it is currently sleeping
    }

    /**
     * Module 15: Demonstrating start() vs run() difference.
     * - Calling start() creates a new thread in the JVM and executes run() asynchronously.
     * - Calling run() directly merely executes the method synchronously on the current thread,
     *   without spawning any new OS or JVM thread!
     */
    public static String explainStartVsRun() {
        return "start() vs run():\n" +
               "1. start() creates a new JVM call stack and switches the thread state from NEW to RUNNABLE.\n" +
               "2. run() is just a normal method call that executes synchronously on the calling thread,\n" +
               "   blocking the caller and providing NO concurrency.";
    }
}
