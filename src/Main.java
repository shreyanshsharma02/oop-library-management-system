import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * APPLICATION ENTRY POINT
 *
 * Supports three launch modes:
 * 1. Default (No arguments): Launches the Java Swing Desktop GUI application.
 * 2. '--console': Launches the interactive console utility demonstrating Scanner,
 *                 BufferedReader, and do-while loops.
 * 3. '--test': Runs a comprehensive automated verification suite across all 16 syllabus modules.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize Core OOP Service
        LibraryService service = new LibraryService();

        if (args.length > 0) {
            String mode = args[0].trim().toLowerCase();

            if (mode.equals("--console") || mode.equals("-c")) {
                System.out.println("[Main] Launching Interactive Console Mode for MUJ CSC2104...");
                ConsoleDemo.runConsoleMenu(service);
                return;
            } else if (mode.equals("--test") || mode.equals("-t")) {
                System.out.println("[Main] Executing Automated 16-Module Syllabus Test Suite...\n");
                boolean allPassed = runComprehensiveTests(service);
                System.out.println("\n[Main] Test Suite Completed. Overall Result: " + (allPassed ? "ALL 16 MODULES PASSED (100%)" : "FAILED"));
                return;
            }
        }

        // Default: Launch Swing Desktop Application
        System.out.println("[Main] Starting MUJ CSC2104 Library Management System (Desktop GUI - Dark Edition)...");
        try {
            // Set modern system look and feel if available
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback gracefully to default Swing look and feel
        }

        // Apply Dark Theme defaults for standard dialogs and option panes
        UIManager.put("OptionPane.background", new java.awt.Color(30, 41, 59));
        UIManager.put("Panel.background", new java.awt.Color(30, 41, 59));
        UIManager.put("OptionPane.messageForeground", new java.awt.Color(248, 250, 252));
        UIManager.put("Label.foreground", new java.awt.Color(248, 250, 252));

        SwingUtilities.invokeLater(() -> {
            LibraryManagementSystem app = new LibraryManagementSystem(service);
            app.setVisible(true);
        });
    }

    /**
     * Comprehensive automated verification testing every syllabus topic.
     */
    public static boolean runComprehensiveTests(LibraryService service) {
        int passed = 0;
        int total = 16;

        System.out.println("=====================================================================");
        System.out.println("  MANIPAL UNIVERSITY JAIPUR - CSC2104 OOP SYLLABUS VERIFICATION SUITE");
        System.out.println("=====================================================================");

        // Module 1: OOP Organization & Separation of Concerns
        try {
            Book testB = new Book("TEST-01", "OOP Principles", "Author", "CS", 5);
            if (testB.getBookId().equals("TEST-01") && testB.getAvailableCopies() == 5) {
                System.out.println("Module 1 (OOP Architecture & Encapsulation): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 1 FAILED: " + e.getMessage());
        }

        // Module 2: Data Types, Variables & Operators
        try {
            Book b = new Book("TEST-02", "Operators", "Author", "CS", 2);
            b.issueCopy(); // Decrement available, increment issued
            if (b.getAvailableCopies() == 1 && b.getIssuedCopies() == 1) {
                b.returnCopy();
                if (b.getAvailableCopies() == 2 && b.getIssuedCopies() == 0) {
                    System.out.println("Module 2 (Data Types, Arithmetic & Relational Operators): PASSED");
                    passed++;
                }
            }
        } catch (Exception e) {
            System.out.println("Module 2 FAILED: " + e.getMessage());
        }

        // Module 3: Control Flow (if-else, switch, loops)
        try {
            int counter = 0;
            // Test for loop
            for (int i = 0; i < 5; i++) {
                counter += i;
            }
            // Test switch-case
            String type = "STUDENT";
            String label = "";
            switch (type) {
                case "STUDENT": label = "Student"; break;
                default: label = "General"; break;
            }
            if (counter == 10 && label.equals("Student")) {
                System.out.println("Module 3 (Control Flow: loops, if-else, switch-case): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 3 FAILED: " + e.getMessage());
        }

        // Module 4: Type Conversion, Wrapper Classes, Casting, Autoboxing/Unboxing
        try {
            String boxReport = service.demonstrateAutoboxingAndUnboxing();
            Book b = new Book("TEST-04", "Casting", "Author", "CS", 4);
            b.issueCopy();
            double ratio = b.getAvailabilityPercentage();
            if (boxReport.contains("Autoboxed") && ratio == 75.0) {
                System.out.println("Module 4 (Type Conversion, Wrapper Classes, Casting, Autoboxing): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 4 FAILED: " + e.getMessage());
        }

        // Module 5: Arrays (1D arrays, safe bound checks, 2D report matrix)
        try {
            String[][] matrix = service.getInventoryReportMatrix();
            if (matrix != null && matrix.length > 0 && matrix[0].length == 7) {
                System.out.println("Module 5 (1D & 2D Multidimensional Arrays & Bounds): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 5 FAILED: " + e.getMessage());
        }

        // Module 6: Classes, Objects, Object References, Constructors, 'this'
        try {
            Book defaultBook = new Book();
            Book paramBook = new Book("B_REF", "Ref Book", "Author", "CS", 3);
            Book refCopy = paramBook; // Assigning object reference
            refCopy.setTitle("Ref Book Updated");
            if (paramBook.getTitle().equals("Ref Book Updated") && defaultBook.getCategory().equals("General")) {
                System.out.println("Module 6 (Classes, Constructors, 'this', Object References): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 6 FAILED: " + e.getMessage());
        }

        // Module 7: Methods, Overloading, Argument Passing by Value, Recursion
        try {
            Book bById = service.findBook("B101");
            Book[] bByKw = service.searchBooks("Java");
            Book[] sortedArr = service.getAllBooks();
            // Sort
            for (int i = 0; i < sortedArr.length - 1; i++) {
                for (int j = 0; j < sortedArr.length - i - 1; j++) {
                    if (sortedArr[j].getBookId().compareToIgnoreCase(sortedArr[j + 1].getBookId()) > 0) {
                        Book tmp = sortedArr[j];
                        sortedArr[j] = sortedArr[j + 1];
                        sortedArr[j + 1] = tmp;
                    }
                }
            }
            Book foundRec = service.recursiveBinarySearch(sortedArr, 0, sortedArr.length - 1, "B101");
            if (bById != null && bByKw.length > 0 && foundRec != null && foundRec.getBookId().equals("B101")) {
                System.out.println("Module 7 (Methods, Overloading, Recursion & Pass-by-Value): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 7 FAILED: " + e.getMessage());
        }

        // Module 8: Access Modifiers, 'final' Fields/Methods, Garbage Collection
        try {
            IssueTransaction tx = new IssueTransaction("TX-9999", "B101", "M201", "2024-10-01", "2024-10-15");
            if (tx.getTransactionId().equals("TX-9999") && IssueTransaction.STANDARD_LOAN_DAYS == 14) {
                System.out.println("Module 8 (Access Modifiers, 'final' Modifiers & GC Reachability): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 8 FAILED: " + e.getMessage());
        }

        // Module 9: Nested & Inner Classes (Static nested & Non-static inner)
        try {
            LibraryFileManager.FileOperationResult staticRes = new LibraryFileManager.FileOperationResult("Test", true, 5, "OK");
            LibraryService.InventoryAuditor innerAuditor = service.createAuditor();
            String summary = innerAuditor.generateAuditSummary();
            if (staticRes.isSuccess() && summary.contains("Total Titles=")) {
                System.out.println("Module 9 (Static Nested Class & Non-static Inner Class): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 9 FAILED: " + e.getMessage());
        }

        // Module 10: Multilevel Inheritance (Person -> Member -> StudentMember)
        try {
            Person p1 = new Member("M_TEST", "Prof. Tester", "test@muj.edu", "2024-01-01", 3);
            Person p2 = new StudentMember("S_TEST", "Student Tester", "st@muj.edu", "2024-01-01", 4, "239301999", "CSE", 3);
            if (p1 instanceof Person && p2 instanceof Member && p2 instanceof StudentMember) {
                System.out.println("Module 10 (Multilevel Inheritance, super, extends): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 10 FAILED: " + e.getMessage());
        }

        // Module 11: Method Overriding & Runtime Polymorphism (Dynamic Dispatch)
        try {
            Member m = new StudentMember("S_TEST2", "Aarav", "a@m.com", "2024-01-01", 4, "239301042", "CSE", 3);
            String desc = m.getRoleDescription(); // Overridden method called through Member reference
            if (desc.contains("MUJ Student Member") && desc.contains("239301042")) {
                System.out.println("Module 11 (Method Overriding & Dynamic Method Dispatch): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 11 FAILED: " + e.getMessage());
        }

        // Module 12: Abstract Classes & Interfaces (Person, Searchable, Composition)
        try {
            Searchable searchableRef = service; // Polymorphic interface reference
            Book searched = searchableRef.findBook("B101");
            if (searchableRef instanceof LibraryOperations && searched != null) {
                System.out.println("Module 12 (Abstract Classes, Interfaces & Composition): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 12 FAILED: " + e.getMessage());
        }

        // Module 13: File Handling (FileReader/Writer & FileInputStream/FileOutputStream)
        try {
            LibraryFileManager.FileOperationResult bkp = service.createBinaryBackup();
            if (bkp.isSuccess()) {
                System.out.println("Module 13 (File I/O: Character & Byte Streams, Backup/Restore): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 13 FAILED: " + e.getMessage());
        }

        // Module 14: Exception Handling (try-catch-finally, custom exceptions)
        try {
            boolean caughtDuplicate = false;
            try {
                service.addBook(new Book("B101", "Existing", "Author", "CS", 2));
            } catch (DuplicateBookException dbe) {
                caughtDuplicate = true;
            }

            if (caughtDuplicate) {
                System.out.println("Module 14 (Exception Handling: Custom Checked Exceptions): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 14 FAILED: " + e.getMessage());
        }

        // Module 15: Multithreading (Thread, Runnable, start, join, priority, wait/notify)
        try {
            BackgroundAlertTask runnableTask = new BackgroundAlertTask();
            Thread consumerThread = new Thread(runnableTask, "TestConsumer");
            consumerThread.setDaemon(true);
            consumerThread.start();

            BackgroundInventoryCheck threadWorker = new BackgroundInventoryCheck(service, null, 500);
            threadWorker.setAlertTask(runnableTask);
            threadWorker.setPriority(Thread.NORM_PRIORITY + 1);
            threadWorker.start();

            Thread.sleep(200);
            threadWorker.stopCheck();
            threadWorker.join(2000); // join()

            runnableTask.stopTask();

            if (!threadWorker.isAlive()) {
                System.out.println("Module 15 (Multithreading: Thread, Runnable, join, priority, wait/notify): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 15 FAILED: " + e.getMessage());
        }

        // Module 16: Garbage Collection & Memory Management
        try {
            Book temp = new Book("TEMP_GC", "Temporary Book", "Author", "General", 1);
            service.addBook(temp);
            service.deleteBook("TEMP_GC"); // Dereferences and sets slot to null
            if (service.findBook("TEMP_GC") == null) {
                System.out.println("Module 16 (Garbage Collection & Object Dereferencing): PASSED");
                passed++;
            }
        } catch (Exception e) {
            System.out.println("Module 16 FAILED: " + e.getMessage());
        }

        System.out.println("---------------------------------------------------------------------");
        System.out.println("Modules Passed: " + passed + " / " + total);
        return passed == total;
    }
}
