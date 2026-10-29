/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS INTERFACE:
 * - Module 12: Interfaces - Core library service contract.
 * - Module 14: Declaring checked exceptions in interface method signatures ('throws').
 */
public interface LibraryOperations extends Searchable {

    void addBook(Book book) throws DuplicateBookException;

    boolean updateBook(Book book);

    boolean deleteBook(String bookId) throws Exception;

    void registerMember(Member member) throws Exception;

    boolean deleteMember(String memberId) throws Exception;

    IssueTransaction issueBook(String memberId, String bookId) throws BookUnavailableException, Exception;

    boolean returnBook(String transactionId) throws Exception;

    String[][] getInventoryReportMatrix();
}
