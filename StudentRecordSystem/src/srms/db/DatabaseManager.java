package srms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates JDBC connections to the SQLite database file (students.db, created
 * in the folder the program is run from) and sets up the tables on first run.
 */
public final class DatabaseManager {

    private static final String URL = "jdbc:sqlite:students.db";

    private DatabaseManager() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Loads the driver, creates the tables if missing and seeds the default admin. */
    public static void initialize() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found. "
                    + "Add the sqlite-jdbc jar to the classpath (see README).", e);
        }

        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS students ("
                    + "roll_no TEXT PRIMARY KEY, "
                    + "name TEXT NOT NULL, "
                    + "course TEXT NOT NULL, "
                    + "academic_year TEXT NOT NULL, "
                    + "email TEXT NOT NULL, "
                    + "phone TEXT NOT NULL, "
                    + "gender TEXT NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS admin ("
                    + "username TEXT PRIMARY KEY, "
                    + "password_hash TEXT NOT NULL)");
        }
        new AdminDAO().seedDefaultAdmin();
    }
}
