/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS INTERFACE:
 * - Module 12: Interfaces - Defines a contract of search operations without implementation.
 *              All methods declared in an interface are implicitly public and abstract.
 */
public interface Searchable {

    /**
     * Finds a book by its exact ID.
     * @param bookId The unique identifier of the book.
     * @return Book object if found; null otherwise (Module 7: returning objects or null).
     */
    Book findBook(String bookId);

    /**
     * Overloaded / specialized search returning an array of books matching a search keyword.
     * @param keyword Part of book title or author.
     * @return Array of matching Book objects.
     */
    Book[] searchBooks(String keyword);

    /**
     * Finds a registered member by member ID.
     * @param memberId The unique identifier of the member.
     * @return Member object if found; null otherwise.
     */
    Member findMember(String memberId);
}
