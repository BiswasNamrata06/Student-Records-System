package srms.db;

import java.sql.SQLException;
import java.util.List;

import srms.exception.DuplicateRecordException;
import srms.model.SearchField;
import srms.model.Student;

/**
 * Abstraction over student storage. The GUI depends on this interface, not on
 * JDBC, so the storage (SQLite today, files or MySQL tomorrow) can be swapped
 * without touching the user interface (polymorphism / loose coupling).
 */
public interface StudentRepository {

    void add(Student student) throws SQLException, DuplicateRecordException;

    /** @return true if a record with that roll number existed and was updated. */
    boolean update(Student student) throws SQLException;

    /** @return true if a record with that roll number existed and was deleted. */
    boolean delete(String rollNo) throws SQLException;

    List<Student> findAll() throws SQLException;

    List<Student> search(SearchField field, String keyword) throws SQLException;

    boolean exists(String rollNo) throws SQLException;
}
