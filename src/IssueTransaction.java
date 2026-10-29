/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 2: Data types (String, boolean, int), relational and logical operators.
 * - Module 6: Classes, objects, parameterized constructor.
 * - Module 8: 'final' variable (transactionId cannot be reassigned once created),
 *              public static final constant for library loan period.
 */
public final class IssueTransaction { // Module 8: 'final' class cannot be subclassed

    // Module 8: 'final' constant representing the fixed 14-day borrowing duration
    public static final int STANDARD_LOAN_DAYS = 14;

    // Module 8: 'final' instance variable: unique identifier locked at creation
    private final String transactionId;

    private String bookId;
    private String memberId;
    private String issueDate; // Formatted YYYY-MM-DD
    private String dueDate;   // Formatted YYYY-MM-DD
    private String returnDate;// Formatted YYYY-MM-DD or "-" if not returned
    private boolean returned; // Module 2: boolean data type

    /**
     * Module 6: Parameterized constructor.
     */
    public IssueTransaction(String transactionId, String bookId, String memberId,
                            String issueDate, String dueDate) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = "-";
        this.returned = false;
    }

    /**
     * Parameterized constructor for reloading existing transactions from file.
     */
    public IssueTransaction(String transactionId, String bookId, String memberId,
                            String issueDate, String dueDate, String returnDate, boolean returned) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.returned = returned;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    /**
     * Marks the transaction as returned with the given return date.
     */
    public void markReturned(String returnDate) {
        this.returned = true;
        this.returnDate = (returnDate != null && !returnDate.isEmpty()) ? returnDate : "Returned";
    }

    /**
     * Formats this transaction to a delimited text line.
     * Format: txId|bookId|memberId|issueDate|dueDate|returnDate|returned
     */
    public String toFileString() {
        return transactionId + "|" + bookId + "|" + memberId + "|" + issueDate + "|" + dueDate + "|" + returnDate + "|" + returned;
    }

    @Override
    public String toString() {
        return "Tx[" + transactionId + "] Book: " + bookId + " -> Member: " + memberId +
               " | Issued: " + issueDate + " | Due: " + dueDate + " | Returned: " + (returned ? returnDate : "ACTIVE");
    }
}
