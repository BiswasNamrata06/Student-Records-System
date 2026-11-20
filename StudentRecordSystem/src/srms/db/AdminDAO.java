package srms.db;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles administrator accounts. Passwords are never stored in plain text;
 * only their SHA-256 hash is saved in the database.
 */
public class AdminDAO {

    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_PASSWORD = "admin123";

    /** Inserts the default admin account if the admin table is empty. */
    public void seedDefaultAdmin() throws SQLException {
        try (Connection con = DatabaseManager.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM admin")) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO admin (username, password_hash) VALUES (?, ?)")) {
                    ps.setString(1, DEFAULT_USERNAME);
                    ps.setString(2, hash(DEFAULT_PASSWORD));
                    ps.executeUpdate();
                }
            }
        }
    }

    /** Returns true when the username exists and the password matches. */
    public boolean authenticate(String username, char[] password) throws SQLException {
        String sql = "SELECT password_hash FROM admin WHERE username = ?";
        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getString(1).equals(hash(new String(password)));
            }
        }
    }

    static String hash(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
