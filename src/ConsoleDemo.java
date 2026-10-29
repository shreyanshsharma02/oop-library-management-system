import java.util.Scanner;

/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 3: Control flow - 'do-while' loop displaying the interactive console menu at least once,
 *              'switch-case' for command selection, 'if-else' for validation.
 * - Module 4: Type conversion - Integer.parseInt() parsing numeric choices.
 * - Module 7: Method invocation, recursive search demonstration.
 * - Module 11: Runtime polymorphism demonstration over Member references.
 * - Module 13: Console I/O - java.util.Scanner for user input and System.out.println() for output.
 * - Module 14: Exception handling - try-catch-finally blocks around user inputs and library operations.
 * - Module 15: Multithreading - Demonstrating Thread start(), join(), and priorities from console.
 */
public class ConsoleDemo {

    public static void runConsoleMenu(LibraryService service) {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        System.out.println("\n=======================================================");
        System.out.println("  MANIPAL UNIVERSITY JAIPUR - CSC2104 OOP MINI-PROJECT ");
        System.out.println("            Interactive Console Demonstration          ");
        System.out.println("=======================================================");

        // Module 3: 'do-while' loop.
        // Guarantees that the menu is presented to the user at least once before exit condition check.
        do {
            System.out.println("\n--- MAIN CONSOLE MENU (Module 3: do-while loop) ---");
            System.out.println("1. View All Books (Module 5: 1D Array Traversal)");
            System.out.println("2. Search Book by Keyword (Module 7: Method Overloading)");
            System.out.println("3. Recursive Binary Search by Book ID (Module 7: Recursion)");
            System.out.println("4. View Members & Demonstrate Runtime Polymorphism (Module 10 & 11)");
            System.out.println("5. Issue Book (Module 14: Custom BookUnavailableException)");
            System.out.println("6. Return Book (Module 2: Relational & Arithmetic Operators)");
            System.out.println("7. Autoboxing, Unboxing & Casting Demo (Module 4)");
            System.out.println("8. Run Multithreading Inventory Audit Demo (Module 15: start & join)");
            System.out.println("9. Demonstrate Pass-by-Value (Module 7)");
            System.out.println("0. Return to GUI / Exit Console Mode");
            System.out.print("Enter your choice (0-9): ");

            String input = scanner.nextLine().trim();

            try {
                // Module 4: Wrapper class Integer.parseInt converts String input to int
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // Module 14: Exception handling for non-numeric input
                System.out.println("[Error] Invalid input! Please enter a valid number from 0 to 9.");
                choice = -1;
                continue;
            }

            // Module 3: 'switch-case' construct for multi-branch operation selection
            switch (choice) {
                case 1:
                    printAllBooks(service);
                    break;
                case 2:
                    System.out.print("Enter search keyword (title/author/id): ");
                    String kw = scanner.nextLine().trim();
                    Book[] results = service.searchBooks(kw);
                    System.out.println("\n--- Search Results (" + results.length + " books found) ---");
                    for (int i = 0; i < results.length; i++) {
                        System.out.println((i + 1) + ". " + results[i]);
                    }
                    break;
                case 3:
                    System.out.print("Enter exact Book ID for Recursive Binary Search (e.g., B101): ");
                    String targetId = scanner.nextLine().trim();
                    Book[] allBooks = service.getAllBooks();
                    // Sort books by ID for binary search
                    sortBooksById(allBooks);
                    Book foundBook = service.recursiveBinarySearch(allBooks, 0, allBooks.length - 1, targetId);
                    if (foundBook != null) {
                        System.out.println("[Recursive Search Found]: " + foundBook);
                    } else {
                        System.out.println("[Recursive Search]: Book ID '" + targetId + "' not found.");
                    }
                    break;
                case 4:
                    System.out.println("\n--- Member Hierarchy & Runtime Polymorphism (Module 10 & 11) ---");
                    String[] polyReport = service.demonstratePolymorphism();
                    for (int i = 0; i < polyReport.length; i++) {
                        System.out.println((i + 1) + ". " + polyReport[i]);
                    }
                    break;
                case 5:
                    System.out.print("Enter Member ID (e.g. S301, M201): ");
                    String memId = scanner.nextLine().trim();
                    System.out.print("Enter Book ID (e.g. B101): ");
                    String bId = scanner.nextLine().trim();
                    try {
                        IssueTransaction tx = service.issueBook(memId, bId);
                        System.out.println("[SUCCESS] Book issued: " + tx);
                    } catch (BookUnavailableException bue) {
                        System.out.println("[CUSTOM EXCEPTION CAUGHT] " + bue.getMessage());
                    } catch (Exception ex) {
                        System.out.println("[ERROR] " + ex.getMessage());
                    }
                    break;
                case 6:
                    System.out.print("Enter Transaction ID to return (e.g. TX-1001): ");
                    String txId = scanner.nextLine().trim();
                    try {
                        boolean ok = service.returnBook(txId);
                        if (ok) {
                            System.out.println("[SUCCESS] Book returned successfully.");
                        }
                    } catch (Exception ex) {
                        System.out.println("[ERROR] " + ex.getMessage());
                    }
                    break;
                case 7:
                    System.out.println("\n--- Type Conversion, Autoboxing & Unboxing (Module 4) ---");
                    System.out.println(service.demonstrateAutoboxingAndUnboxing());
                    break;
                case 8:
                    runMultithreadingDemo(service);
                    break;
                case 9:
                    System.out.println("\n--- Pass-by-Value Demonstration (Module 7) ---");
                    Book sample = service.findBook("B101");
                    int primitiveTest = 100;
                    String report = service.demonstratePassByValue(primitiveTest, sample);
                    System.out.println(report);
                    System.out.println("Caller's primitive variable after method call: " + primitiveTest + " (UNCHANGED)");
                    break;
                case 0:
                    System.out.println("\nExiting interactive console demo. Returning...");
                    break;
                default:
                    System.out.println("[Warning] Choice out of range! Please choose between 0 and 9.");
                    break;
            }

        } while (choice != 0); // Module 3: Loop termination condition
    }

    private static void printAllBooks(LibraryService service) {
        Book[] books = service.getAllBooks();
        System.out.println("\n--- Library Books Inventory (" + books.length + " titles) ---");
        System.out.printf("%-8s %-32s %-20s %-15s %-6s %-6s\n", "ID", "TITLE", "AUTHOR", "CATEGORY", "AVAIL", "TOTAL");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (int i = 0; i < books.length; i++) {
            Book b = books[i];
            if (b != null) {
                System.out.printf("%-8s %-32s %-20s %-15s %-6d %-6d\n",
                        b.getBookId(),
                        (b.getTitle().length() > 30 ? b.getTitle().substring(0, 29) + "…" : b.getTitle()),
                        b.getAuthor(),
                        b.getCategory(),
                        b.getAvailableCopies(),
                        b.getTotalCopies());
            }
        }
    }

    /**
     * Module 15: Multithreading demonstration from console.
     * Starts BackgroundInventoryCheck, demonstrates priorities, and uses join() to wait.
     */
    private static void runMultithreadingDemo(LibraryService service) {
        System.out.println("\n--- Multithreading Inventory Audit Demonstration (Module 15) ---");

        BackgroundInventoryCheck thread = new BackgroundInventoryCheck(service, new BackgroundInventoryCheck.InventoryListener() {
            @Override
            public void onStockAlert(String message, boolean isCritical) {
                System.out.println("  [THREAD ALERT] " + message);
            }

            @Override
            public void onCheckCompleted(int outOfStock, int lowStock, long elapsed) {
                System.out.println("  [THREAD AUDIT FINISHED] Out-of-Stock: " + outOfStock +
                                   ", Low-Stock: " + lowStock + " (Time: " + elapsed + " ms)");
            }
        }, 3000);

        System.out.println("Initial Thread State: " + thread.getState() + " (NEW)");
        thread.setPriority(Thread.NORM_PRIORITY + 1);
        System.out.println("Thread Priority set to: " + thread.getPriority());

        System.out.println("Invoking thread.start() to spawn concurrent worker...");
        thread.start();
        System.out.println("Thread State after start(): " + thread.getState());

        try {
            // Let the thread run for 1 second, then wait on it
            Thread.sleep(1200);
            System.out.println("Stopping thread safely via cooperative stopCheck()...");
            thread.stopCheck();

            // Module 15: join() call - main thread waits for inventory check thread to terminate
            System.out.println("Invoking thread.join() to synchronize with worker termination...");
            thread.join(3000);
            System.out.println("join() completed! Final Thread State: " + thread.getState() + " (TERMINATED)");
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        }
    }

    /**
     * Helper to sort books array by ID using simple bubble sort.
     */
    private static void sortBooksById(Book[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] != null && arr[j + 1] != null) {
                    if (arr[j].getBookId().compareToIgnoreCase(arr[j + 1].getBookId()) > 0) {
                        Book temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        LibraryService service = new LibraryService();
        runConsoleMenu(service);
    }
}
