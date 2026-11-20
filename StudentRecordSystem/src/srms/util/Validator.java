package srms.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import srms.exception.ValidationException;
import srms.model.Student;

/**
 * Centralised input validation. All problems are collected into a list
 * so the user sees every mistake at once instead of one at a time.
 */
public final class Validator {

    private static final Pattern ROLL  = Pattern.compile("^[A-Za-z0-9/_-]{3,20}$");
    private static final Pattern NAME  = Pattern.compile("^[A-Za-z][A-Za-z .'-]{1,49}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[6-9][0-9]{9}$");

    private Validator() { }

    public static void validate(Student s) throws ValidationException {
        List<String> errors = new ArrayList<>();

        if (isBlank(s.getRollNo())) {
            errors.add("Roll Number is required.");
        } else if (!ROLL.matcher(s.getRollNo()).matches()) {
            errors.add("Roll Number must be 3-20 characters (letters, digits, / _ - only).");
        }

        if (isBlank(s.getName())) {
            errors.add("Name is required.");
        } else if (!NAME.matcher(s.getName()).matches()) {
            errors.add("Name must be 2-50 characters and contain only letters, spaces, . ' -");
        }

        if (isBlank(s.getCourse())) {
            errors.add("Course must be selected.");
        }

        if (isBlank(s.getAcademicYear())) {
            errors.add("Academic Year must be selected.");
        }

        if (isBlank(s.getEmail())) {
            errors.add("Email Address is required.");
        } else if (!EMAIL.matcher(s.getEmail()).matches()) {
            errors.add("Email Address is not valid (example: name@example.com).");
        }

        if (isBlank(s.getPhone())) {
            errors.add("Phone Number is required.");
        } else if (!PHONE.matcher(s.getPhone()).matches()) {
            errors.add("Phone Number must be 10 digits and start with 6, 7, 8 or 9.");
        }

        if (isBlank(s.getGender())) {
            errors.add("Gender must be selected.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
