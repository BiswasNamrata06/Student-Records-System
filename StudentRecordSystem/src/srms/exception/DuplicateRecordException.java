package srms.exception;

/** Thrown when a student with the same Roll Number already exists. */
public class DuplicateRecordException extends Exception {
    private static final long serialVersionUID = 1L;

    public DuplicateRecordException(String rollNo) {
        super("A student with Roll Number '" + rollNo + "' already exists.");
    }
}
