package srms.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Thrown when one or more fields of a student record are invalid. */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super("Validation failed with " + errors.size() + " error(s)");
        this.errors = new ArrayList<>(errors);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
