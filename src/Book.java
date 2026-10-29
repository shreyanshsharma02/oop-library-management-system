/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 2: Primitive data types (int, double, boolean), reference types (String).
 *              Arithmetic operators (+, -, /), relational operators (>, <=, ==),
 *              logical operators (&&, ||), increment/decrement (++ , --).
 * - Module 4: Implicit and explicit type casting.
 * - Module 6: Classes, private fields, default and parameterized constructors, 'this' keyword.
 * - Module 8: Encapsulation with private state and public accessors, final method.
 */
public class Book {

    // Module 2: Primitive and Reference Data Types
    private String bookId;
    private String title;
    private String author;
    private String category;
    private int totalCopies;
    private int availableCopies;
    private int issuedCopies;

    /**
     * Module 6: Default constructor (no-arg).
     */
    public Book() {
        this.bookId = "";
        this.title = "";
        this.author = "";
        this.category = "General";
        this.totalCopies = 1;
        this.availableCopies = 1;
        this.issuedCopies = 0;
    }

    /**
     * Module 6: Parameterized constructor.
     * Initializes all book fields and validates copy counts.
     */
    public Book(String bookId, String title, String author, String category, int totalCopies) {
        this.bookId = (bookId != null) ? bookId.trim() : "";
        this.title = (title != null) ? title.trim() : "";
        this.author = (author != null) ? author.trim() : "";
        this.category = (category != null) ? category.trim() : "General";

        // Module 2 & 3: Relational and if-else logic
        if (totalCopies <= 0) {
            this.totalCopies = 1;
        } else {
            this.totalCopies = totalCopies;
        }

        this.availableCopies = this.totalCopies;
        this.issuedCopies = 0;
    }

    /**
     * Parameterized constructor specifying existing copy distribution (for file loading).
     */
    public Book(String bookId, String title, String author, String category, int totalCopies, int availableCopies, int issuedCopies) {
        this.bookId = (bookId != null) ? bookId.trim() : "";
        this.title = (title != null) ? title.trim() : "";
        this.author = (author != null) ? author.trim() : "";
        this.category = (category != null) ? category.trim() : "General";
        this.totalCopies = Math.max(1, totalCopies);
        this.availableCopies = Math.max(0, availableCopies);
        this.issuedCopies = Math.max(0, issuedCopies);
    }

    // Getters and Setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = (bookId != null) ? bookId.trim() : "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = (title != null) ? title.trim() : "";
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = (author != null) ? author.trim() : "";
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = (category != null) ? category.trim() : "General";
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getIssuedCopies() {
        return issuedCopies;
    }

    /**
     * Module 2: Arithmetic operators, relational operators, increment/decrement.
     * Decrements availableCopies and increments issuedCopies safely.
     * @return true if copy was successfully issued; false if no copies were available.
     */
    public boolean issueCopy() {
        // Module 2: Relational check
        if (this.availableCopies > 0) {
            this.availableCopies--; // Module 2: Decrement operator
            this.issuedCopies++;    // Module 2: Increment operator
            return true;
        }
        return false;
    }

    /**
     * Module 2: Arithmetic operators, increment/decrement.
     * Restores an available copy when a book is returned.
     */
    public boolean returnCopy() {
        // Module 2: Logical && operator
        if (this.issuedCopies > 0 && (this.availableCopies < this.totalCopies)) {
            this.availableCopies++; // Module 2: Increment operator
            this.issuedCopies--;    // Module 2: Decrement operator
            return true;
        }
        return false;
    }

    /**
     * Updates total copies count and adjusts available copies accordingly.
     */
    public void setTotalCopies(int newTotal) {
        if (newTotal >= this.issuedCopies) {
            this.totalCopies = newTotal;
            // Module 2: Arithmetic operator '-'
            this.availableCopies = this.totalCopies - this.issuedCopies;
        }
    }

    /**
     * Module 4: Type Conversion - Implicit and Explicit Casting.
     * availableCopies and totalCopies are integers.
     * Dividing integers would result in integer truncation (e.g., 3/4 = 0).
     * Explicitly cast 'availableCopies' to double so the division is performed in floating-point,
     * demonstrating explicit casting.
     * The result is returned as double, demonstrating implicit conversion.
     */
    public double getAvailabilityPercentage() {
        if (this.totalCopies == 0) {
            return 0.0;
        }
        // Explicit casting of int to double, followed by arithmetic multiplication
        double ratio = ((double) this.availableCopies / (double) this.totalCopies) * 100.0;
        return ratio;
    }

    /**
     * Module 8: 'final' method preventing modification of critical file serialization format.
     */
    public final String toFileString() {
        return bookId + "|" + title + "|" + author + "|" + category + "|" + totalCopies + "|" + availableCopies + "|" + issuedCopies;
    }

    @Override
    public String toString() {
        return "[" + bookId + "] " + title + " by " + author + " (" + category + ") - Avail: " + availableCopies + "/" + totalCopies;
    }
}
