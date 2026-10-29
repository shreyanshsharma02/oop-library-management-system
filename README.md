# Manipal University Jaipur (MUJ)
## Department of Computer Science & Engineering
### CSC2104: Object Oriented Programming — Mini-Project Submission
### **Library Management System (LMS)**

---

## 1. Project Title & Overview
- **Project Title:** Desktop Library Management System
- **Course Code & Name:** CSC2104 — Object Oriented Programming
- **Target Platform:** macOS / Any platform with standard JDK 11+ (JDK 26 tested)
- **Primary Technologies:** Java SE, Java Swing (GUI), Standard Java I/O & Concurrency

---

## 2. Problem Statement
Traditional academic and departmental libraries often struggle with paper-based registers or fragmented spreadsheets for cataloging books, tracking student memberships, managing checkout timelines, and enforcing borrow quotas. 

This project implements a complete, self-contained desktop Library Management System built around object-oriented principles. The system manages books, differentiates between standard members and student members, automates loan periods (14-day rule), maintains real-time copy counters, provides background inventory auditing without freezing the user interface, and persists all state to local structured text files and raw byte-stream backups.

---

## 3. Objectives
1. **Core Domain Functionality:** Provide reliable book management, member registration, book issue, return transactions, and a live metrics dashboard.
2. **Strict Syllabus Adherence:** Seamlessly integrate and demonstrate all 16 modules from the MUJ CSC2104 OOP course handout.
3. **Robust Design:** Employ encapsulation, inheritance, runtime polymorphism, abstract classes, custom exceptions, interfaces, and multi-threading without relying on third-party frameworks, web tech, or SQL databases.
4. **Academic Explainability:** Ensure the codebase is clean, well-commented, and easily demonstrable during project vivas and lab examinations.

---

## 4. Scope and Limitations
### Scope:
- Desktop GUI built with Java Swing with an appealing dark theme (Slate `#0f172a`, Card `#1e293b`, Electric Sky `#38bdf8`, Emerald `#10b981`, Violet `#a855f7`, Amber `#f59e0b`, Coral `#f43f5e`) and standard layout managers (`BorderLayout`, `CardLayout`, `GridBagLayout`, `FlowLayout`, `GridLayout`).
- Terminal interactive mode for direct `Scanner` and `do-while` demonstration.
- In-memory data structures using standard single-dimensional and 2D arrays (`Book[]`, `Member[]`, `IssueTransaction[]`, `String[][]`).
- Local file persistence using Character Streams (`FileReader`, `FileWriter`) and Byte Streams (`FileInputStream`, `FileOutputStream`).
- Concurrency via `Thread` subclassing and `Runnable` task execution with monitors (`wait()` / `notifyAll()`).

### Limitations (By Project Specification):
- No external relational databases (MySQL, SQLite) or ORMs.
- No third-party UI libraries (Tailwind, JavaFX, Web frameworks).
- No network communication, cloud synchronization, or payment APIs.
- Scope strictly confined to topics covered within the CSC2104 syllabus.

---

## 5. Functional Modules
The application is structured into six functional modules:

1. **Book Management (`Book.java`, `LibraryService.java`):**
   - Add new books with ID, title, author, category, and total copies.
   - Validate copy counts (total $\ge 1$, available $\le$ total).
   - Search books by exact ID, title keyword, author, or category.
   - Edit book attributes while preserving active issue counts.
   - Delete books only when no active issue transactions exist.

2. **Member Management (`Person.java`, `Member.java`, `StudentMember.java`):**
   - Register Standard Members (quota: 2 books) and MUJ Student Members (quota: 4 books, registration number, department, semester).
   - Prevent duplicate member IDs.
   - Protect members from deletion while active book issues are pending.

3. **Book Issue Module (`IssueTransaction.java`, `LibraryService.java`):**
   - Select registered member and available book title.
   - Check member borrowing quotas.
   - Enforce 14-day loan period ($today + 14\text{ days}$).
   - Atomically decrement available copy count and increment issued count.
   - Throw custom `BookUnavailableException` when zero copies are available.

4. **Book Return Module:**
   - Select active transaction, restore available inventory, and record return date.
   - Prevent duplicate returns on already returned transactions.

5. **Live Dashboard & Reports:**
   - Real-time counters: Total Titles, Total Copies, Available Copies, Issued Copies, Registered Members.
   - Active issue transaction table.
   - Multidimensional 2D array report generation for inventory auditing.

6. **Local Data Persistence & Backup (`LibraryFileManager.java`):**
   - Auto-load on launch and auto-save on change (`data/books.txt`, `data/members.txt`, `data/transactions.txt`).
   - Byte-level binary backup (`data/books_backup.dat`) using `FileInputStream` and `FileOutputStream`.

---

## 6. Architecture & Class Relationships

```
+-----------------------------------------------------------------------------------+
|                                  <<interface>>                                    |
|                                    Searchable                                     |
|  + findBook(bookId: String): Book                                                 |
|  + searchBooks(keyword: String): Book[]                                           |
|  + findMember(memberId: String): Member                                           |
+-----------------------------------------+-----------------------------------------+
                                          ^
                                          | extends
+-----------------------------------------+-----------------------------------------+
|                                  <<interface>>                                    |
|                                LibraryOperations                                  |
|  + addBook(book: Book): void                                                      |
|  + deleteBook(bookId: String): boolean                                            |
|  + registerMember(member: Member): void                                           |
|  + issueBook(memberId: String, bookId: String): IssueTransaction                  |
|  + returnBook(txId: String): boolean                                              |
|  + getInventoryReportMatrix(): String[][]                                         |
+-----------------------------------------+-----------------------------------------+
                                          ^
                                          | implements
+-----------------------------------------+-----------------------------------------+
|                                 LibraryService                                    |
|  - books: Book[]                                                                  |
|  - members: Member[]                    "has-a" (Composition)                    |
|  - transactions: IssueTransaction[] --------------------+                         |
|  + recursiveBinarySearch(...): Book                     |                         |
|  + demonstratePassByValue(...): String                  |                         |
+---------------------------------------------------------+-------------------------+
                                                          |
                 +----------------------------------------+-------------------------+
                 |                                                                  |
                 v                                                                  v
+-----------------------------------+             +---------------------------------+
|               Book                |             |        IssueTransaction         |
|  - bookId: String                 |             |  - transactionId: final String  |
|  - totalCopies: int               |             |  - issueDate: String            |
|  - availableCopies: int           |             |  - dueDate: String              |
|  - issuedCopies: int              |             |  - returned: boolean            |
+-----------------------------------+             +---------------------------------+

                                 Inheritance Hierarchy
                                 =====================
                                 
                               +-----------------------+
                               |     <<abstract>>      |
                               |        Person         |
                               |  # id: String         |
                               |  # name: String       |
                               |  # contact: String    |
                               |  + getRoleDesc(): abs |
                               +-----------+-----------+
                                           ^
                                           | extends
                               +-----------+-----------+
                               |        Member         |
                               |  - membershipDate     |
                               |  - maxBooksAllowed    |
                               |  + getRoleDesc()      |
                               +-----------+-----------+
                                           ^
                                           | extends (Multilevel)
                               +-----------+-----------+
                               |     StudentMember     |
                               |  - studentRegNo       |
                               |  - department         |
                               |  - semester           |
                               |  + getRoleDesc()      |
                               +-----------------------+
```

---

## 7. Syllabus-to-Code Mapping Table (MUJ CSC2104)

| # | Topic from CSC2104 Handout | Java Concept Used | Class / Method Demonstrating It | How to Demonstrate in Viva |
|---|----------------------------|-------------------|---------------------------------|-----------------------------|
| **1** | **Introduction to OOP & Benefits** | Object grouping, encapsulation, separation of concerns | `Book.java`, `Member.java`, `LibraryService.java` | Explain how data & behavior are bundled into classes rather than scattered procedural functions. |
| **2** | **Data Types, Variables & Operators** | `int`, `boolean`, `String`, arithmetic (`+`, `-`), relational (`>`), logical (`&&`, `\|\|`), increment (`++`, `--`) | `Book.java` (`issueCopy()`, `returnCopy()`), `LibraryService.java` | Show copy increments, availability checks (`availableCopies > 0`), and logic in `issueCopy()`. |
| **3** | **Control Flow & Decision Making** | `if-else`, `switch-case`, `for`, `while`, `do-while` | `ConsoleDemo.java`, `LibraryFileManager.java`, `LibraryService.java` | Run `ConsoleDemo` to show `do-while` menu; inspect `switch-case` in menu/parser; show `while` loop reading files. |
| **4** | **Type Conversion & Wrapper Classes** | Explicit casting `(double)`, Implicit conversion, `Integer.parseInt()`, Autoboxing & Unboxing | `Book.java` (`getAvailabilityPercentage()`), `LibraryService.java` (`demonstrateAutoboxingAndUnboxing()`) | Click "Autoboxing & Casting" on Viva tab; inspect `Integer boxed = count; int unboxed = boxed;` |
| **5** | **Arrays** | 1D arrays (`Book[]`, `Member[]`), dynamic resizing, safe bounds, 2D array matrix (`String[][]`) | `LibraryService.java` (`ensureBookCapacity()`, `getInventoryReportMatrix()`) | Show `books` array declaration, automatic resizing loop, and `String[][]` feeding `JTable`. |
| **6** | **Classes, Objects & Constructors** | Default & parameterized constructors, `this` keyword, object reference assignment | `Person.java`, `Book.java`, `Member.java` | Show `this.title = title;`, default `new Book()`, and parameterized constructor invocation. |
| **7** | **Methods, Overloading & Recursion** | Method overloading (`searchBooks`), pass-by-value proof, recursive binary search | `LibraryService.java` (`searchBooks(...)`, `recursiveBinarySearch(...)`, `demonstratePassByValue(...)`) | Click "Recursive Search" and "Pass-by-Value" on Viva tab; show binary search dividing range recursively. |
| **8** | **Access Modifiers & `final`** | `private`, `protected`, `public`, package-private helper, `final` variable, `final` method | `Person.java` (`protected id`, `final getBasicInfo()`), `IssueTransaction.java` (`final transactionId`) | Show `protected` fields in `Person` inherited by `Member`; show `final` transaction ID prevents reassignment. |
| **9** | **Nested & Inner Classes** | Static nested class vs. Non-static inner class | `LibraryFileManager.FileOperationResult` (Static), `LibraryService.InventoryAuditor` (Inner) | Click "Static vs Inner" on Viva tab; explain that non-static inner class holds outer instance reference. |
| **10** | **Inheritance & `super`** | Multilevel inheritance (`Person` $\rightarrow$ `Member` $\rightarrow$ `StudentMember`), `super(...)` call | `StudentMember.java`, `Member.java`, `Person.java` | Show constructor chaining with `super(id, name, contact)` and student registration number extension. |
| **11** | **Method Overriding & Polymorphism** | `@Override`, dynamic method dispatch on subclass instances | `Person.java`, `Member.java`, `StudentMember.java` (`getRoleDescription()`) | Click "Polymorphic Dispatch" on Viva tab; show `StudentMember` method invoked through `Member` reference. |
| **12** | **Abstract Classes & Interfaces** | Abstract class (`Person`), interface (`Searchable`, `LibraryOperations`), Composition ("has-a") | `Person.java`, `Searchable.java`, `LibraryService.java` | Show abstract method in `Person`, interface implementation in `LibraryService`, and composition arrays. |
| **13** | **Console I/O & File Handling** | `Scanner`, `BufferedReader`, `FileReader`, `FileWriter`, `FileInputStream`, `FileOutputStream` | `ConsoleDemo.java`, `LibraryFileManager.java` | Run `./run.sh -c` to show `Scanner`; show `FileWriter` for txt saving and `FileInputStream`/`FileOutputStream` for binary backup. |
| **14** | **Exception Handling** | Custom checked exceptions (`BookUnavailableException`, `DuplicateBookException`), `try-catch-finally`, `throws` | `BookUnavailableException.java`, `DuplicateBookException.java`, `LibraryService.java` | Click "Custom Exceptions" on Viva tab; attempt to issue out-of-stock book or add duplicate ID to see dialog. |
| **15** | **Multithreading** | `extends Thread` (`BackgroundInventoryCheck`), `implements Runnable` (`BackgroundAlertTask`), `start()`, `join()`, priority, `synchronized`, `wait()`/`notify()` | `BackgroundInventoryCheck.java`, `BackgroundAlertTask.java` | Run "Multithreading Demo" or test priority/join on Reports tab; observe background scanning without freezing GUI. |
| **16** | **Garbage Collection & Memory** | Object reachability, dereferencing array slots (`null`), eligibility for GC | `LibraryService.java` (`deleteBook()`, `deleteMember()`) | Show `books[bookCount - 1] = null;` upon deletion, breaking active references so GC can collect memory. |

---

## 8. Detailed OOP Explanations for Viva Examination

### Q1: What is the difference between Procedural Programming and Object-Oriented Programming in this project?
- **Procedural Approach:** Functions operate on detached global structures or primitive arrays without encapsulation. Adding a field (e.g., student semester) requires modifying every function.
- **OOP Approach:** Data and operations are encapsulated inside cohesive domain classes (`Book`, `StudentMember`). `Book` guards its own copy count invariants (`issueCopy()`), while `LibraryService` coordinates business workflows without exposing internal state.

### Q2: How does Dynamic Method Dispatch work in your Member hierarchy?
- `Person` declares `public abstract String getRoleDescription()`.
- `Member` overrides it to return standard member details.
- `StudentMember` overrides it again to display university registration number and semester.
- In `LibraryService`:
  ```java
  Member m = members[i]; // Reference type is Member
  String desc = m.getRoleDescription(); // Executes StudentMember at runtime if object is StudentMember!
  ```
  The JVM resolves the method target at runtime based on the actual object type on the heap, demonstrating runtime polymorphism.

### Q3: What is the difference between a Static Nested Class and a Non-Static Inner Class?
- **Static Nested Class (`LibraryFileManager.FileOperationResult`):** Associated with the outer class but does *not* hold an implicit reference to an outer instance. It can be instantiated independently:
  ```java
  LibraryFileManager.FileOperationResult res = new LibraryFileManager.FileOperationResult(...);
  ```
- **Non-Static Inner Class (`LibraryService.InventoryAuditor`):** Bound to an active instance of `LibraryService`. It has implicit access to outer instance fields (`books`, `members`, `bookCount`). It is instantiated via the outer instance:
  ```java
  InventoryAuditor auditor = libraryService.new InventoryAuditor();
  ```

### Q4: How does Java handle argument passing? Prove it using your project.
- **Java is strictly pass-by-value.**
- For primitive types (e.g. `int`), a copy of the bit value is passed. Modifying the parameter inside `demonstratePassByValue(int val, Book b)` leaves the caller's variable untouched.
- For object reference variables, a copy of the reference pointer is passed. Mutating fields via the reference alters the object on the heap, but reassigning the reference parameter itself does not change the caller's pointer.

### Q5: What is the difference between `start()` and `run()` in Multithreading?
- Calling `start()` requests a new call stack from the operating system and switches the thread state from `NEW` to `RUNNABLE`, executing `run()` asynchronously.
- Calling `run()` directly merely executes the method synchronously on the caller's thread (e.g. Swing Event Dispatch Thread), causing the GUI to freeze.

### Q6: How is thread safety achieved in the library?
- Critical methods in `LibraryService` (`issueBook`, `returnBook`, `addBook`) are marked `synchronized`.
- When the background inventory check thread scans the books array, it acquires the intrinsic lock:
  ```java
  synchronized (libraryService) {
      Book[] all = libraryService.getAllBooks();
      ...
  }
  ```
  This prevents race conditions where an issue transaction decrements stock while the background auditor is reading it.

### Q7: How do `wait()` and `notifyAll()` work in `BackgroundAlertTask`?
- `BackgroundAlertTask` implements `Runnable` and maintains an internal alert queue.
- The consumer thread enters a `synchronized(alertLock)` block and calls `alertLock.wait()` while the queue is empty, releasing the CPU and lock.
- When `BackgroundInventoryCheck` detects low stock, it calls `postAlert()`, which pushes the alert and invokes `alertLock.notifyAll()`, waking up the consumer thread.

---

## 9. Compilation and Running Instructions (macOS)

### Prerequisites:
- macOS with JDK 11 or higher installed (Oracle JDK, OpenJDK, or Temurin).
- Check your JDK version:
  ```bash
  javac -version
  java -version
  ```

### Step 1: Open Terminal and Navigate to Project Directory
```bash
cd "/Users/shreyanshsharma/Projects/OOP Library management system"
```

### Step 2: Make the Build Script Executable
```bash
chmod +x run.sh
```

### Step 3: Run Modes

#### Mode A: Launch Desktop GUI Application (Default)
```bash
./run.sh
```
*(or via standard Java commands)*
```bash
javac -d bin src/*.java
java -cp bin Main
```

#### Mode B: Run Automated 16-Module Syllabus Test Suite
```bash
./run.sh --test
```
*(Validates every single module and outputs a 16/16 PASSED verification summary).*

#### Mode C: Launch Interactive Terminal Console Mode
```bash
./run.sh --console
```
*(Demonstrates `Scanner`, `do-while` menu, recursive binary search, and console diagnostics).*

---

## 10. Automated Test Results Summary
Running `./run.sh --test` executes automated assertions verifying each syllabus topic:

```
=====================================================================
  MANIPAL UNIVERSITY JAIPUR - CSC2104 OOP SYLLABUS VERIFICATION SUITE
=====================================================================
Module 1 (OOP Architecture & Encapsulation): PASSED
Module 2 (Data Types, Arithmetic & Relational Operators): PASSED
Module 3 (Control Flow: loops, if-else, switch-case): PASSED
Module 4 (Type Conversion, Wrapper Classes, Casting, Autoboxing): PASSED
Module 5 (1D & 2D Multidimensional Arrays & Bounds): PASSED
Module 6 (Classes, Constructors, 'this', Object References): PASSED
Module 7 (Methods, Overloading, Recursion & Pass-by-Value): PASSED
Module 8 (Access Modifiers, 'final' Modifiers & GC Reachability): PASSED
Module 9 (Static Nested Class & Non-static Inner Class): PASSED
Module 10 (Multilevel Inheritance, super, extends): PASSED
Module 11 (Method Overriding & Dynamic Method Dispatch): PASSED
Module 12 (Abstract Classes, Interfaces & Composition): PASSED
Module 13 (File I/O: Character & Byte Streams, Backup/Restore): PASSED
Module 14 (Exception Handling: Custom Checked Exceptions): PASSED
Module 15 (Multithreading: Thread, Runnable, join, priority, wait/notify): PASSED
Module 16 (Garbage Collection & Object Dereferencing): PASSED
---------------------------------------------------------------------
Modules Passed: 16 / 16
Test Suite Completed. Overall Result: ALL 16 MODULES PASSED (100%)
```

---

## 11. Conclusion
The MUJ CSC2104 Object Oriented Programming Library Management System fulfills all functional and academic requirements. Every syllabus module—from basic variable types and control structures to multilevel inheritance, abstract interfaces, custom exception hierarchies, character/byte I/O streams, and synchronized multithreading—is implemented with clarity, documented in source comments, and verified via an integrated test suite and graphical desktop interface.
