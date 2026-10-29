/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 14: Custom Checked Exception.
 *              Extends java.lang.Exception directly.
 *              Checked exceptions must be explicitly declared using 'throws'
 *              or handled within a 'try-catch' block.
 */
public class BookUnavailableException extends Exception {

    public BookUnavailableException(String message) {
        super(message);
    }

    public BookUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
