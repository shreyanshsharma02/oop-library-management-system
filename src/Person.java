/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 6: Class fundamentals, instance fields, default and parameterized constructors, 'this' keyword.
 * - Module 8: Access modifiers ('protected', 'private', 'public'), 'final' methods.
 * - Module 10: Inheritance - Base class in the hierarchy (Person -> Member -> StudentMember).
 * - Module 12: Abstract classes and abstract methods.
 */
public abstract class Person {

    // Module 8 & 10: Protected fields allow direct access in derived classes (Member, StudentMember)
    // while keeping data encapsulated from unrelated classes outside the hierarchy.
    protected String id;
    protected String name;
    protected String contact;

    /**
     * Module 6: Default constructor (no-argument constructor).
     * Initializes fields to default values.
     */
    public Person() {
        this.id = "";
        this.name = "";
        this.contact = "";
    }

    /**
     * Module 6: Parameterized constructor.
     * Uses 'this' keyword to resolve variable shadowing between parameters and instance fields.
     */
    public Person(String id, String name, String contact) {
        this.id = (id != null) ? id.trim() : "";
        this.name = (name != null) ? name.trim() : "";
        this.contact = (contact != null) ? contact.trim() : "";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = (id != null) ? id.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = (name != null) ? name.trim() : "";
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = (contact != null) ? contact.trim() : "";
    }

    /**
     * Module 12: Abstract Method.
     * Any concrete subclass must provide its own implementation of this method.
     * This enforces polymorphism across all Person subclasses.
     */
    public abstract String getRoleDescription();

    /**
     * Module 8: 'final' Method demonstration.
     * Subclasses cannot override this method, ensuring a uniform basic representation.
     */
    public final String getBasicInfo() {
        return "ID: " + id + " | Name: " + name + " | Contact: " + contact;
    }

    @Override
    public String toString() {
        return getBasicInfo() + " (" + getRoleDescription() + ")";
    }
}
