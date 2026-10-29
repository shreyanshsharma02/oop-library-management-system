/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 14: Custom Checked Exception for detecting and rejecting duplicate book IDs.
 */
public class DuplicateBookException extends Exception {

    public DuplicateBookException(String message) {
        super(message);
    }

    public DuplicateBookException(String message, Throwable cause) {
        super(message, cause);
    }
}
