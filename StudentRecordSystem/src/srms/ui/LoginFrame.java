package srms.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import srms.db.AdminDAO;

/** Administrator login window. Three failed attempts close the application. */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int MAX_ATTEMPTS = 3;

    private final AdminDAO adminDAO = new AdminDAO();
    private final JTextField txtUser = new JTextField(16);
    private final JPasswordField txtPass = new JPasswordField(16);
    private final JCheckBox chkShow = new JCheckBox("Show password");
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnExit = new JButton("Exit");
    private int attempts = 0;

    public LoginFrame() {
        super("Student Record Management System - Admin Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Administrator Login", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0; panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; panel.add(txtUser, gbc);

        gbc.gridy = 2; gbc.gridx = 0; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; panel.add(txtPass, gbc);

        gbc.gridy = 3; gbc.gridx = 1; panel.add(chkShow, gbc);

        JPanel buttons = new JPanel();
        buttons.add(btnLogin);
        buttons.add(btnExit);
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(buttons, gbc);

        add(panel);
        getRootPane().setDefaultButton(btnLogin);   // Enter key = Login

        // Event handling
        btnLogin.addActionListener(e -> doLogin());
        btnExit.addActionListener(e -> System.exit(0));
        char defaultEcho = txtPass.getEchoChar();
        chkShow.addActionListener(e ->
                txtPass.setEchoChar(chkShow.isSelected() ? (char) 0 : defaultEcho));

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void doLogin() {
        String user = txtUser.getText().trim();
        char[] pass = txtPass.getPassword();

        if (user.isEmpty() || pass.length == 0) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (adminDAO.authenticate(user, pass)) {
                MainFrame main = new MainFrame(user);
                main.setVisible(true);
                dispose();
            } else {
                attempts++;
                txtPass.setText("");
                if (attempts >= MAX_ATTEMPTS) {
                    JOptionPane.showMessageDialog(this,
                            "Too many failed attempts. The application will close.",
                            "Access Denied", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password. Attempts left: " + (MAX_ATTEMPTS - attempts),
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            java.util.Arrays.fill(pass, '\0');   // wipe password from memory
        }
    }
}
