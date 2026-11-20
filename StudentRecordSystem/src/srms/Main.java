package srms;

import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import srms.db.DatabaseManager;
import srms.ui.LoginFrame;

/** Entry point: prepares the database and opens the login window. */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to the default look and feel
        }

        try {
            DatabaseManager.initialize();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Could not initialise the database:\n" + ex.getMessage(),
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
