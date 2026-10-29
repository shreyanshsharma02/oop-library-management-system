import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 1: Benefits of OOP - Modularity, data encapsulation, clean service architecture.
 * - Module 2: Variables, primitive data types, arithmetic (+, -, *), relational (>, <, ==),
 *              logical (&&, ||, !), assignment (=, +=) and increment/decrement (++, --).
 * - Module 3: Control flow - if, if-else, switch-case, for loop, while loop.
 * - Module 4: Type conversion - Autoboxing, unboxing, explicit and implicit casting.
 * - Module 5: Single-dimensional arrays (Book[], Member[], IssueTransaction[]), safe bound
 *              checking, array resizing, and Multidimensional 2D array (String[][]) generation.
 * - Module 6: Objects, constructor calls, 'this' keyword, object reference assignment.
 * - Module 7: Methods, method overloading, passing objects as parameters, returning objects,
 *              pass-by-value demonstration, and a recursive binary search algorithm.
 * - Module 8: Access modifiers, package-private helper, GC reachability (nulling array slots).
 * - Module 9: Non-static Inner Class (InventoryAuditor) with direct access to outer instance fields.
 * - Module 10 & 11: Inheritance and Runtime Polymorphism (dynamic dispatch on Member subclasses).
 * - Module 12: Implements Searchable and LibraryOperations interfaces; Composition ("has-a").
 * - Module 14: Custom exception throwing and catching (BookUnavailableException, DuplicateBookException).
 * - Module 15: Thread synchronization ('synchronized' methods protecting shared state).
 */
public class LibraryService implements LibraryOperations, Searchable {

    // Initial capacity for dynamic array management
    private static final int INITIAL_CAPACITY = 20;

    // Module 5: Single-dimensional arrays storing in-memory records
    private Book[] books;
    private int bookCount;

    private Member[] members;
    private int memberCount;

    private IssueTransaction[] transactions;
    private int transactionCount;

    // Auto-increment transaction counter
    private int nextTxSequence = 1001;

    // Date formatter for consistent date handling
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // File manager dependency
    private final LibraryFileManager fileManager;
    private final String dataDir = "data/";

    /**
     * Module 6: Constructor initializing arrays and loading persistent state.
     */
    public LibraryService() {
        // Module 5: Array declaration, creation, and initialization
        this.books = new Book[INITIAL_CAPACITY];
        this.bookCount = 0;

        this.members = new Member[INITIAL_CAPACITY];
        this.memberCount = 0;

        this.transactions = new IssueTransaction[INITIAL_CAPACITY];
        this.transactionCount = 0;

        this.fileManager = new LibraryFileManager();

        // Load existing data from files on startup
        loadAllData();
    }

    // =========================================================================
    // Module 5: ARRAY DYNAMIC EXPANSION & SAFE BOUNDS
    // =========================================================================

    /**
     * Module 5: Demonstrates manual array resizing when capacity is reached.
     */
    private void ensureBookCapacity() {
        if (bookCount >= books.length) {
            Book[] largerArray = new Book[books.length * 2];
            for (int i = 0; i < bookCount; i++) {
                largerArray[i] = books[i];
            }
            this.books = largerArray;
            System.out.println("[LibraryService] Book array resized to capacity: " + books.length);
        }
    }

    private void ensureMemberCapacity() {
        if (memberCount >= members.length) {
            Member[] largerArray = new Member[members.length * 2];
            for (int i = 0; i < memberCount; i++) {
                largerArray[i] = members[i];
            }
            this.members = largerArray;
            System.out.println("[LibraryService] Member array resized to capacity: " + members.length);
        }
    }

    private void ensureTxCapacity() {
        if (transactionCount >= transactions.length) {
            IssueTransaction[] largerArray = new IssueTransaction[transactions.length * 2];
            for (int i = 0; i < transactionCount; i++) {
                largerArray[i] = transactions[i];
            }
            this.transactions = largerArray;
            System.out.println("[LibraryService] Transaction array resized to capacity: " + transactions.length);
        }
    }

    // =========================================================================
    // Module 7: METHOD OVERLOADING DEMONSTRATION
    // =========================================================================

    /**
     * Module 7: Overloaded method 1 - Search book by exact ID.
     * Demonstrates linear traversal using a 'for' loop.
     * @return Book object or null if not found.
     */
    @Override
    public Book findBook(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            return null;
        }
        String searchId = bookId.trim();
        // Module 3 & 5: 'for' loop traversing 1D array safely
        for (int i = 0; i < bookCount; i++) {
            // Module 2: Relational and string equality
            if (books[i] != null && books[i].getBookId().equalsIgnoreCase(searchId)) {
                return books[i]; // Return book reference
            }
        }
        return null;
    }

    /**
     * Module 7: Overloaded method 2 - Search books by keyword matching Title or Author.
     * Demonstrates method overloading (same method name, different signature/parameter).
     * @return Array of matching books.
     */
    @Override
    public Book[] searchBooks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Return copy of all current books
            Book[] all = new Book[bookCount];
            System.arraycopy(books, 0, all, 0, bookCount);
            return all;
        }

        String query = keyword.trim().toLowerCase();
        // First count matches to allocate exact sized array
        int matchCount = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                // Module 2: Logical || operator
                if (books[i].getTitle().toLowerCase().contains(query) ||
                    books[i].getAuthor().toLowerCase().contains(query) ||
                    books[i].getBookId().toLowerCase().contains(query)) {
                    matchCount++;
                }
            }
        }

        Book[] matches = new Book[matchCount];
        int index = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                if (books[i].getTitle().toLowerCase().contains(query) ||
                    books[i].getAuthor().toLowerCase().contains(query) ||
                    books[i].getBookId().toLowerCase().contains(query)) {
                    matches[index++] = books[i];
                }
            }
        }
        return matches;
    }

    /**
     * Module 7: Overloaded method 3 - Search books filtering by category.
     */
    public Book[] searchBooks(String keyword, String category) {
        Book[] initialMatches = searchBooks(keyword);
        if (category == null || category.equalsIgnoreCase("All") || category.trim().isEmpty()) {
            return initialMatches;
        }
        int catMatchCount = 0;
        for (int i = 0; i < initialMatches.length; i++) {
            if (initialMatches[i].getCategory().equalsIgnoreCase(category.trim())) {
                catMatchCount++;
            }
        }
        Book[] filtered = new Book[catMatchCount];
        int idx = 0;
        for (int i = 0; i < initialMatches.length; i++) {
            if (initialMatches[i].getCategory().equalsIgnoreCase(category.trim())) {
                filtered[idx++] = initialMatches[i];
            }
        }
        return filtered;
    }

    // =========================================================================
    // Module 7: RECURSIVE ALGORITHM DEMONSTRATION
    // =========================================================================

    /**
     * Module 7: Recursive Binary Search demonstration on a sorted array of books by ID.
     * Demonstrates recursion: base cases (not found, found) and recursive steps.
     *
     * @param arr Array of books sorted by ID.
     * @param low Lower index boundary.
     * @param high Upper index boundary.
     * @param targetId Book ID being searched.
     * @return Matching Book object or null if not found.
     */
    public Book recursiveBinarySearch(Book[] arr, int low, int high, String targetId) {
        // Base case 1: search space exhausted
        if (low > high || arr == null) {
            return null;
        }

        // Calculate midpoint
        int mid = low + (high - low) / 2;

        if (arr[mid] == null) {
            return null;
        }

        int comparison = arr[mid].getBookId().compareToIgnoreCase(targetId);

        // Base case 2: element found at mid
        if (comparison == 0) {
            return arr[mid];
        }

        // Recursive case 1: target is smaller, search left half
        if (comparison > 0) {
            return recursiveBinarySearch(arr, low, mid - 1, targetId);
        }

        // Recursive case 2: target is larger, search right half
        return recursiveBinarySearch(arr, mid + 1, high, targetId);
    }

    // =========================================================================
    // Module 7 & 8: PASS-BY-VALUE & GARBAGE COLLECTION DEMONSTRATIONS
    // =========================================================================

    /**
     * Module 7: Argument passing demonstration (Pass-by-Value).
     * In Java:
     * 1. Primitive arguments (e.g. int) pass a copy of the primitive value.
     *    Modifying 'val' inside does NOT affect the caller's variable.
     * 2. Object references pass a COPY of the reference pointer.
     *    Modifying the object's fields via the reference DOES mutate the object,
     *    but reassigning the reference variable itself (e.g. b = new Book()) does NOT change the caller's reference.
     */
    public String demonstratePassByValue(int primitiveVal, Book bookRef) {
        int originalVal = primitiveVal;
        primitiveVal += 50; // Only alters local copy

        String bookTitleBefore = (bookRef != null) ? bookRef.getTitle() : "null";
        if (bookRef != null) {
            bookRef.setTitle(bookTitleBefore + " [Modified via Reference]");
        }

        return "Primitive: passed=" + originalVal + ", localCopyChangedTo=" + primitiveVal +
               " (Caller's variable untouched) | Object Reference: mutated field=" +
               ((bookRef != null) ? bookRef.getTitle() : "null");
    }

    /**
     * Module 4: Autoboxing and Unboxing demonstration.
     * Converts between primitive 'int' and wrapper class 'Integer'.
     */
    public String demonstrateAutoboxingAndUnboxing() {
        // Module 4: Autoboxing: primitive int is automatically wrapped into an Integer object
        int primitiveCount = this.bookCount;
        Integer boxedCount = primitiveCount; // Autoboxing: int -> Integer

        // Module 4: Unboxing: Integer object is automatically converted back to primitive int
        int unboxedCount = boxedCount; // Unboxing: Integer -> int

        // Explicit casting demonstration:
        double rawAverage = (double) unboxedCount / 2.0; // Implicit conversion & explicit cast
        int truncatedAverage = (int) rawAverage;          // Explicit narrowing cast

        return "Autoboxed Integer: " + boxedCount + ", Unboxed int: " + unboxedCount +
               ", Explicit cast to int: " + truncatedAverage;
    }

    // =========================================================================
    // BOOK MANAGEMENT OPERATIONS
    // =========================================================================

    /**
     * Module 14: Throws custom checked exception DuplicateBookException.
     * Module 15: 'synchronized' method ensures thread safety during inventory addition.
     */
    @Override
    public synchronized void addBook(Book book) throws DuplicateBookException {
        if (book == null || book.getBookId().isEmpty()) {
            throw new IllegalArgumentException("Book details cannot be empty.");
        }

        // Check for duplicate ID
        if (findBook(book.getBookId()) != null) {
            // Module 14: 'throw' keyword throwing custom checked exception
            throw new DuplicateBookException("A book with ID '" + book.getBookId() + "' already exists!");
        }

        ensureBookCapacity();
        books[bookCount++] = book; // Module 2: post-increment operator
        saveAllData();
        System.out.println("[LibraryService] Added book: " + book.getTitle() + " (Total registered: " + bookCount + ")");
    }

    @Override
    public synchronized boolean updateBook(Book updatedBook) {
        if (updatedBook == null) {
            return false;
        }
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getBookId().equalsIgnoreCase(updatedBook.getBookId())) {
                books[i].setTitle(updatedBook.getTitle());
                books[i].setAuthor(updatedBook.getAuthor());
                books[i].setCategory(updatedBook.getCategory());
                books[i].setTotalCopies(updatedBook.getTotalCopies());
                saveAllData();
                return true;
            }
        }
        return false;
    }

    /**
     * Module 8 & 16: Garbage Collection Demonstration.
     * Deletes a book if it has no active issue transactions.
     * Shifts elements left and sets the last array slot to 'null'.
     * Setting books[bookCount - 1] = null removes the reference, making the unreferenced
     * Book object eligible for JVM Garbage Collection.
     */
    @Override
    public synchronized boolean deleteBook(String bookId) throws Exception {
        if (bookId == null || bookId.trim().isEmpty()) {
            return false;
        }

        // Rule: cannot delete book if it is currently issued in an active transaction
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null &&
                transactions[i].getBookId().equalsIgnoreCase(bookId) &&
                !transactions[i].isReturned()) {
                throw new Exception("Cannot delete book '" + bookId + "': it currently has active issue transactions!");
            }
        }

        int targetIndex = -1;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null && books[i].getBookId().equalsIgnoreCase(bookId)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex != -1) {
            // Shift elements left to maintain contiguous array
            for (int i = targetIndex; i < bookCount - 1; i++) {
                books[i] = books[i + 1];
            }
            // Module 8 & 16: Clear the reference at the end to make it reachable by GC
            books[bookCount - 1] = null;
            bookCount--; // Module 2: Decrement operator
            saveAllData();
            System.out.println("[LibraryService] Deleted book ID: " + bookId + ". Active book count: " + bookCount);
            return true;
        }
        return false;
    }

    // =========================================================================
    // MEMBER MANAGEMENT & POLYMORPHISM
    // =========================================================================

    @Override
    public Member findMember(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return null;
        }
        String id = memberId.trim();
        for (int i = 0; i < memberCount; i++) {
            if (members[i] != null && members[i].getId().equalsIgnoreCase(id)) {
                return members[i];
            }
        }
        return null;
    }

    public Member[] searchMembers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            Member[] all = new Member[memberCount];
            System.arraycopy(members, 0, all, 0, memberCount);
            return all;
        }
        String query = keyword.trim().toLowerCase();
        int count = 0;
        for (int i = 0; i < memberCount; i++) {
            if (members[i] != null) {
                if (members[i].getId().toLowerCase().contains(query) ||
                    members[i].getName().toLowerCase().contains(query) ||
                    members[i].getContact().toLowerCase().contains(query)) {
                    count++;
                }
            }
        }
        Member[] matches = new Member[count];
        int idx = 0;
        for (int i = 0; i < memberCount; i++) {
            if (members[i] != null) {
                if (members[i].getId().toLowerCase().contains(query) ||
                    members[i].getName().toLowerCase().contains(query) ||
                    members[i].getContact().toLowerCase().contains(query)) {
                    matches[idx++] = members[i];
                }
            }
        }
        return matches;
    }

    @Override
    public synchronized void registerMember(Member member) throws Exception {
        if (member == null || member.getId().isEmpty()) {
            throw new IllegalArgumentException("Member ID cannot be empty.");
        }
        if (findMember(member.getId()) != null) {
            throw new Exception("Member with ID '" + member.getId() + "' already exists!");
        }
        ensureMemberCapacity();
        members[memberCount++] = member;
        saveAllData();
        System.out.println("[LibraryService] Registered member: " + member.getName() + " (" + member.getRoleDescription() + ")");
    }

    public synchronized boolean updateMember(Member updated) {
        if (updated == null) {
            return false;
        }
        for (int i = 0; i < memberCount; i++) {
            if (members[i] != null && members[i].getId().equalsIgnoreCase(updated.getId())) {
                members[i].setName(updated.getName());
                members[i].setContact(updated.getContact());
                members[i].setMembershipDate(updated.getMembershipDate());
                members[i].setMaxBooksAllowed(updated.getMaxBooksAllowed());

                // If updating a student member
                if (updated instanceof StudentMember && members[i] instanceof StudentMember) {
                    StudentMember sNew = (StudentMember) updated;
                    StudentMember sOld = (StudentMember) members[i];
                    sOld.setStudentRegNo(sNew.getStudentRegNo());
                    sOld.setDepartment(sNew.getDepartment());
                    sOld.setSemester(sNew.getSemester());
                }
                saveAllData();
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean deleteMember(String memberId) throws Exception {
        if (memberId == null || memberId.trim().isEmpty()) {
            return false;
        }
        // Cannot delete member if they have active issued books
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null &&
                transactions[i].getMemberId().equalsIgnoreCase(memberId) &&
                !transactions[i].isReturned()) {
                throw new Exception("Cannot delete member '" + memberId + "': active book issues are pending return!");
            }
        }

        int targetIndex = -1;
        for (int i = 0; i < memberCount; i++) {
            if (members[i] != null && members[i].getId().equalsIgnoreCase(memberId)) {
                targetIndex = i;
                break;
            }
        }

        if (targetIndex != -1) {
            for (int i = targetIndex; i < memberCount - 1; i++) {
                members[i] = members[i + 1];
            }
            // Module 8 & 16: Clear reference for garbage collection
            members[memberCount - 1] = null;
            memberCount--;
            saveAllData();
            return true;
        }
        return false;
    }

    /**
     * Module 11: Runtime Polymorphism & Dynamic Method Dispatch Demonstration.
     * Iterates through the Member[] array containing both Member and StudentMember instances.
     * Invoking getRoleDescription() on each element executes the corresponding subclass implementation
     * at runtime through dynamic method dispatch.
     */
    public String[] demonstratePolymorphism() {
        String[] descriptions = new String[memberCount];
        for (int i = 0; i < memberCount; i++) {
            if (members[i] != null) {
                // Dynamic method dispatch executes Member or StudentMember method dynamically
                descriptions[i] = members[i].getName() + " -> " + members[i].getRoleDescription();
            }
        }
        return descriptions;
    }

    // =========================================================================
    // BOOK ISSUE AND RETURN (CORE BUSINESS LOGIC & EXCEPTIONS)
    // =========================================================================

    /**
     * Module 8: Package-private helper method to generate unique transaction IDs.
     */
    String generateTransactionId() {
        return "TX-" + (nextTxSequence++);
    }

    /**
     * Counts how many books a member currently has checked out.
     */
    public int getActiveIssueCountForMember(String memberId) {
        int count = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null &&
                transactions[i].getMemberId().equalsIgnoreCase(memberId) &&
                !transactions[i].isReturned()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Issues a book to a member.
     * Module 14: Throws BookUnavailableException (custom checked exception) if copy count <= 0.
     * Module 15: Synchronized method prevents race condition when two threads try to issue the last copy.
     */
    @Override
    public synchronized IssueTransaction issueBook(String memberId, String bookId)
            throws BookUnavailableException, Exception {

        Member member = findMember(memberId);
        if (member == null) {
            throw new Exception("Member with ID '" + memberId + "' is not registered.");
        }

        Book book = findBook(bookId);
        if (book == null) {
            throw new Exception("Book with ID '" + bookId + "' does not exist.");
        }

        // Check borrowing limit
        int activeIssues = getActiveIssueCountForMember(memberId);
        if (activeIssues >= member.getMaxBooksAllowed()) {
            throw new Exception("Member " + member.getName() + " has reached the maximum borrowing limit of " +
                                member.getMaxBooksAllowed() + " books.");
        }

        // Module 14: Throw custom checked exception if book has 0 available copies
        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("Cannot issue book '" + book.getTitle() + "' (ID: " +
                                              bookId + "): No copies are currently available in the library!");
        }

        // Decrement available copies, increment issued copies
        boolean success = book.issueCopy();
        if (!success) {
            throw new BookUnavailableException("Failed to reserve book copy for '" + book.getTitle() + "'.");
        }

        // Calculate issue date and due date (fixed 14-day loan period)
        LocalDate today = LocalDate.now();
        LocalDate due = today.plusDays(IssueTransaction.STANDARD_LOAN_DAYS);

        String issueDateStr = today.format(dateFormatter);
        String dueDateStr = due.format(dateFormatter);

        String txId = generateTransactionId();
        IssueTransaction tx = new IssueTransaction(txId, book.getBookId(), member.getId(), issueDateStr, dueDateStr);

        ensureTxCapacity();
        transactions[transactionCount++] = tx;

        saveAllData();
        System.out.println("[LibraryService] Book issued successfully: " + tx);
        return tx;
    }

    /**
     * Returns an issued book, restoring its copy count.
     */
    @Override
    public synchronized boolean returnBook(String transactionId) throws Exception {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty.");
        }

        IssueTransaction targetTx = null;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && transactions[i].getTransactionId().equalsIgnoreCase(transactionId.trim())) {
                targetTx = transactions[i];
                break;
            }
        }

        if (targetTx == null) {
            throw new Exception("Transaction ID '" + transactionId + "' was not found.");
        }

        if (targetTx.isReturned()) {
            throw new Exception("This book has already been returned on " + targetTx.getReturnDate() + "!");
        }

        Book book = findBook(targetTx.getBookId());
        if (book != null) {
            book.returnCopy();
        }

        String returnDateStr = LocalDate.now().format(dateFormatter);
        targetTx.markReturned(returnDateStr);

        saveAllData();
        System.out.println("[LibraryService] Book returned successfully for transaction: " + transactionId);
        return true;
    }

    // =========================================================================
    // Module 5: 2D MULTIDIMENSIONAL ARRAY GENERATION (Inventory Report Matrix)
    // =========================================================================

    /**
     * Module 5: Multidimensional 2D Array Demonstration.
     * Generates a 2D String array (matrix) representing book inventory records.
     * Directly consumed by Swing JTable and inventory reports.
     */
    @Override
    public String[][] getInventoryReportMatrix() {
        String[][] matrix = new String[bookCount][7];
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                matrix[i][0] = books[i].getBookId();
                matrix[i][1] = books[i].getTitle();
                matrix[i][2] = books[i].getAuthor();
                matrix[i][3] = books[i].getCategory();
                matrix[i][4] = String.valueOf(books[i].getTotalCopies());
                matrix[i][5] = String.valueOf(books[i].getAvailableCopies());
                matrix[i][6] = String.valueOf(books[i].getIssuedCopies());
            }
        }
        return matrix;
    }

    public String[][] getMemberTableMatrix() {
        String[][] matrix = new String[memberCount][6];
        for (int i = 0; i < memberCount; i++) {
            if (members[i] != null) {
                matrix[i][0] = members[i].getId();
                matrix[i][1] = members[i].getName();
                matrix[i][2] = members[i].getContact();
                matrix[i][3] = (members[i] instanceof StudentMember) ? "Student" : "Standard";
                matrix[i][4] = String.valueOf(members[i].getMaxBooksAllowed());
                // Module 11: Polymorphic role description dynamically formatted
                matrix[i][5] = members[i].getRoleDescription();
            }
        }
        return matrix;
    }

    public String[][] getTransactionTableMatrix(boolean activeOnly) {
        int count = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null) {
                if (!activeOnly || !transactions[i].isReturned()) {
                    count++;
                }
            }
        }

        String[][] matrix = new String[count][7];
        int idx = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null) {
                if (!activeOnly || !transactions[i].isReturned()) {
                    matrix[idx][0] = transactions[i].getTransactionId();
                    matrix[idx][1] = transactions[i].getBookId();
                    matrix[idx][2] = transactions[i].getMemberId();
                    matrix[idx][3] = transactions[i].getIssueDate();
                    matrix[idx][4] = transactions[i].getDueDate();
                    matrix[idx][5] = transactions[i].getReturnDate();
                    matrix[idx][6] = transactions[i].isReturned() ? "RETURNED" : "ACTIVE";
                    idx++;
                }
            }
        }
        return matrix;
    }

    // =========================================================================
    // Module 9: NON-STATIC INNER CLASS DEMONSTRATION
    // =========================================================================

    /**
     * Module 9: Non-static Inner Class demonstration.
     * Unlike a static nested class, a non-static inner class is bound to an instance
     * of the enclosing LibraryService class. It has direct, implicit access to the
     * enclosing instance's private fields (books, members, transactions, bookCount).
     */
    public class InventoryAuditor {

        /**
         * Performs an audit scanning for low-stock and out-of-stock titles.
         */
        public String generateAuditSummary() {
            int outOfStock = 0;
            int lowStock = 0;
            int healthyStock = 0;

            // Direct access to outer class private fields
            for (int i = 0; i < bookCount; i++) {
                if (books[i] != null) {
                    int avail = books[i].getAvailableCopies();
                    if (avail == 0) {
                        outOfStock++;
                    } else if (avail < 2) {
                        lowStock++;
                    } else {
                        healthyStock++;
                    }
                }
            }

            return "Audit Summary: Total Titles=" + bookCount +
                   " | Healthy=" + healthyStock +
                   " | Low Stock (<2)=" + lowStock +
                   " | Out of Stock=" + outOfStock;
        }

        /**
         * Identifies books that require restocking.
         */
        public Book[] findBooksNeedingRestock() {
            int count = 0;
            for (int i = 0; i < bookCount; i++) {
                if (books[i] != null && books[i].getAvailableCopies() <= 1) {
                    count++;
                }
            }
            Book[] restockList = new Book[count];
            int idx = 0;
            for (int i = 0; i < bookCount; i++) {
                if (books[i] != null && books[i].getAvailableCopies() <= 1) {
                    restockList[idx++] = books[i];
                }
            }
            return restockList;
        }
    }

    /**
     * Factory method creating an instance of the non-static inner class.
     */
    public InventoryAuditor createAuditor() {
        return new InventoryAuditor();
    }

    // =========================================================================
    // DATA PERSISTENCE & BACKUP (MODULE 13)
    // =========================================================================

    public synchronized void saveAllData() {
        try {
            fileManager.saveBooks(books, bookCount, dataDir + "books.txt");
            fileManager.saveMembers(members, memberCount, dataDir + "members.txt");
            fileManager.saveTransactions(transactions, transactionCount, dataDir + "transactions.txt");
        } catch (IOException e) {
            System.err.println("[LibraryService] Error saving data: " + e.getMessage());
        }
    }

    public synchronized void loadAllData() {
        try {
            this.bookCount = fileManager.loadBooks(dataDir + "books.txt", books, books.length);
            this.memberCount = fileManager.loadMembers(dataDir + "members.txt", members, members.length);
            this.transactionCount = fileManager.loadTransactions(dataDir + "transactions.txt", transactions, transactions.length);

            // Reconstruct next transaction ID sequence
            for (int i = 0; i < transactionCount; i++) {
                if (transactions[i] != null) {
                    String id = transactions[i].getTransactionId();
                    if (id.startsWith("TX-")) {
                        try {
                            int seq = Integer.parseInt(id.substring(3));
                            if (seq >= nextTxSequence) {
                                nextTxSequence = seq + 1;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            // If empty, initialize default academic sample data for convenience
            if (bookCount == 0 && memberCount == 0) {
                initializeDefaultData();
            }
        } catch (IOException e) {
            System.err.println("[LibraryService] Error loading data: " + e.getMessage());
        }
    }

    /**
     * Seeds initial academic data if the system is started fresh.
     */
    private void initializeDefaultData() {
        System.out.println("[LibraryService] Initializing default sample data for MUJ CSC2104...");
        try {
            addBook(new Book("B101", "Core Java: Fundamentals", "Cay S. Horstmann", "Computer Science", 5));
            addBook(new Book("B102", "Java: The Complete Reference", "Herbert Schildt", "Computer Science", 4));
            addBook(new Book("B103", "Data Structures & Algorithms in Java", "Robert Lafore", "Computer Science", 3));
            addBook(new Book("B104", "Effective Java", "Joshua Bloch", "Software Engineering", 2));
            addBook(new Book("B105", "Operating System Concepts", "Silberschatz", "Computer Science", 1));

            registerMember(new Member("M201", "Dr. Rajesh Kumar", "rajesh.kumar@jaipur.manipal.edu", "2024-01-15", 3));
            registerMember(new StudentMember("S301", "Aarav Sharma", "aarav.23@muj.edu", "2024-08-01", 4, "239301042", "CSE", 3));
            registerMember(new StudentMember("S302", "Ananya Verma", "ananya.23@muj.edu", "2024-08-05", 4, "239301115", "CSE", 3));
            registerMember(new Member("M202", "Prof. Sunita Rao", "sunita.rao@jaipur.manipal.edu", "2024-02-10", 3));

            issueBook("S301", "B101");
            issueBook("S302", "B104");
        } catch (Exception e) {
            System.err.println("[LibraryService] Error initializing defaults: " + e.getMessage());
        }
    }

    /**
     * Module 13: Binary Backup & Restore operations
     */
    public LibraryFileManager.FileOperationResult createBinaryBackup() throws IOException {
        saveAllData();
        return fileManager.backupLibraryData(dataDir + "books.txt", dataDir + "books_backup.dat");
    }

    public LibraryFileManager.FileOperationResult restoreBinaryBackup() throws IOException {
        LibraryFileManager.FileOperationResult res = fileManager.restoreLibraryData(dataDir + "books_backup.dat", dataDir + "books.txt");
        if (res.isSuccess()) {
            this.bookCount = fileManager.loadBooks(dataDir + "books.txt", books, books.length);
        }
        return res;
    }

    // =========================================================================
    // METRICS AND GETTERS
    // =========================================================================

    public synchronized int getTotalBookTitles() {
        return bookCount;
    }

    public synchronized int getTotalBookCopies() {
        int sum = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                sum += books[i].getTotalCopies();
            }
        }
        return sum;
    }

    public synchronized int getTotalAvailableCopies() {
        int sum = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                sum += books[i].getAvailableCopies();
            }
        }
        return sum;
    }

    public synchronized int getTotalIssuedCopies() {
        int sum = 0;
        for (int i = 0; i < bookCount; i++) {
            if (books[i] != null) {
                sum += books[i].getIssuedCopies();
            }
        }
        return sum;
    }

    public synchronized int getRegisteredMemberCount() {
        return memberCount;
    }

    public synchronized int getActiveTransactionCount() {
        int count = 0;
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i] != null && !transactions[i].isReturned()) {
                count++;
            }
        }
        return count;
    }

    public synchronized Book[] getAllBooks() {
        Book[] copy = new Book[bookCount];
        System.arraycopy(books, 0, copy, 0, bookCount);
        return copy;
    }

    public synchronized Member[] getAllMembers() {
        Member[] copy = new Member[memberCount];
        System.arraycopy(members, 0, copy, 0, memberCount);
        return copy;
    }

    public synchronized IssueTransaction[] getAllTransactions() {
        IssueTransaction[] copy = new IssueTransaction[transactionCount];
        System.arraycopy(transactions, 0, copy, 0, transactionCount);
        return copy;
    }
}
