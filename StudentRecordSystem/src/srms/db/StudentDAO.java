package srms.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import srms.exception.DuplicateRecordException;
import srms.model.SearchField;
import srms.model.Student;

/** JDBC implementation of {@link StudentRepository} using SQLite. */
public class StudentDAO implements StudentRepository {

    private static final String COLUMNS =
            "roll_no, name, course, academic_year, email, phone, gender";

    @Override
    public void add(Student s) throws SQLException, DuplicateRecordException {
        if (exists(s.getRollNo())) {
            throw new DuplicateRecordException(s.getRollNo());
        }
        String sql = "INSERT INTO students (" + COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getCourse());
            ps.setString(4, s.getAcademicYear());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getGender());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean update(Student s) throws SQLException {
        String sql = "UPDATE students SET name = ?, course = ?, academic_year = ?, "
                + "email = ?, phone = ?, gender = ? WHERE roll_no = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getCourse());
            ps.setString(3, s.getAcademicYear());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getGender());
            ps.setString(7, s.getRollNo());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String rollNo) throws SQLException {
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE roll_no = ?")) {
            ps.setString(1, rollNo);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM students ORDER BY roll_no";
        List<Student> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public List<Student> search(SearchField field, String keyword) throws SQLException {
        // The column name comes from the enum (never from user text); the keyword is bound as a parameter.
        String sql = "SELECT " + COLUMNS + " FROM students WHERE " + field.getColumn()
                + " LIKE ? ORDER BY roll_no";
        List<Student> list = new ArrayList<>();
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean exists(String rollNo) throws SQLException {
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM students WHERE roll_no = ?")) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("roll_no"),
                rs.getString("name"),
                rs.getString("course"),
                rs.getString("academic_year"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("gender"));
    }
}
