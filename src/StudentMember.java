/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 10: Multilevel Inheritance: Person -> Member -> StudentMember.
 *              Uses 'super(...)' to chain constructors across levels of inheritance.
 * - Module 11: Method Overriding and Dynamic Method Dispatch:
 *              Overrides getRoleDescription() again, providing specialized behavior for students.
 */
public class StudentMember extends Member {

    private String studentRegNo;
    private String department;
    private int semester;

    /**
     * Module 6 & 10: Default constructor.
     */
    public StudentMember() {
        super();
        this.studentRegNo = "N/A";
        this.department = "CSE";
        this.semester = 3;
        this.setMaxBooksAllowed(4); // Students get a higher quota
    }

    /**
     * Module 6 & 10: Parameterized constructor.
     * Passes shared member attributes up the hierarchy via super(...).
     */
    public StudentMember(String id, String name, String contact, String membershipDate,
                         int maxBooksAllowed, String studentRegNo, String department, int semester) {
        super(id, name, contact, membershipDate, (maxBooksAllowed > 0 ? maxBooksAllowed : 4));
        this.studentRegNo = (studentRegNo != null) ? studentRegNo.trim() : "N/A";
        this.department = (department != null) ? department.trim() : "CSE";
        this.semester = (semester >= 1 && semester <= 8) ? semester : 3;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        if (semester >= 1 && semester <= 8) {
            this.semester = semester;
        }
    }

    /**
     * Module 11: Method Overriding (Runtime Polymorphism).
     * Demonstrates dynamic method dispatch. When stored in a Member or Person reference array,
     * this specific implementation will execute for StudentMember instances.
     */
    @Override
    public String getRoleDescription() {
        return "MUJ Student Member [RegNo: " + studentRegNo + ", Dept: " + department +
               ", Sem: " + semester + "] (Borrow Limit: " + getMaxBooksAllowed() + " books)";
    }

    /**
     * Serializes this student member object to a delimited text line.
     * Format: STUDENT|id|name|contact|membershipDate|maxBooksAllowed|studentRegNo|department|semester
     */
    @Override
    public String toFileString() {
        return "STUDENT|" + id + "|" + name + "|" + contact + "|" + getMembershipDate() +
               "|" + getMaxBooksAllowed() + "|" + studentRegNo + "|" + department + "|" + semester;
    }
}
