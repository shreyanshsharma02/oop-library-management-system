/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 6: Classes, objects, constructors, encapsulation with private fields and public accessors.
 * - Module 10: Inheritance - 'extends Person', 'super(...)' constructor call, inheriting parent members.
 * - Module 11: Method overriding ('@Override') and Runtime Polymorphism.
 */
public class Member extends Person {

    private String membershipDate;
    private int maxBooksAllowed;

    /**
     * Module 6 & 10: Default constructor.
     * Calls super() implicitly and initializes member-specific defaults.
     */
    public Member() {
        super();
        this.membershipDate = "2024-01-01";
        this.maxBooksAllowed = 2; // Standard member limit
    }

    /**
     * Module 6 & 10: Parameterized constructor.
     * Uses 'super(id, name, contact)' to initialize base class (Person) private/protected state.
     */
    public Member(String id, String name, String contact, String membershipDate, int maxBooksAllowed) {
        super(id, name, contact); // Module 10: invoking superclass constructor
        this.membershipDate = (membershipDate != null) ? membershipDate.trim() : "2024-01-01";
        this.maxBooksAllowed = (maxBooksAllowed > 0) ? maxBooksAllowed : 2;
    }

    public String getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(String membershipDate) {
        this.membershipDate = membershipDate;
    }

    public int getMaxBooksAllowed() {
        return maxBooksAllowed;
    }

    public void setMaxBooksAllowed(int maxBooksAllowed) {
        if (maxBooksAllowed > 0) {
            this.maxBooksAllowed = maxBooksAllowed;
        }
    }

    /**
     * Module 11: Method Overriding and Dynamic Method Dispatch.
     * Overrides abstract method getRoleDescription() from Person.
     * When called through a Person or Member reference, this version executes at runtime.
     */
    @Override
    public String getRoleDescription() {
        return "Standard Member (Borrow Limit: " + maxBooksAllowed + " books)";
    }

    /**
     * Serializes this member object to a delimited text line for file storage.
     * Format: STANDARD|id|name|contact|membershipDate|maxBooksAllowed
     */
    public String toFileString() {
        return "STANDARD|" + id + "|" + name + "|" + contact + "|" + membershipDate + "|" + maxBooksAllowed;
    }
}
