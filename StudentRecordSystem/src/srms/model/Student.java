package srms.model;

/**
 * Model (entity) class representing one student record.
 * Demonstrates encapsulation: private fields with public getters/setters.
 */
public class Student {
    private String rollNo;
    private String name;
    private String course;
    private String academicYear;
    private String email;
    private String phone;
    private String gender;

    public Student() { }

    public Student(String rollNo, String name, String course, String academicYear,
                   String email, String phone, String gender) {
        this.rollNo = rollNo;
        this.name = name;
        this.course = course;
        this.academicYear = academicYear;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    @Override
    public String toString() {
        return rollNo + " | " + name + " | " + course + " | " + academicYear
                + " | " + email + " | " + phone + " | " + gender;
    }
}
