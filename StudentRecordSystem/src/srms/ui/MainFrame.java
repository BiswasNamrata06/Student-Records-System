package srms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import srms.db.StudentDAO;
import srms.db.StudentRepository;
import srms.exception.DuplicateRecordException;
import srms.exception.ValidationException;
import srms.model.SearchField;
import srms.model.Student;
import srms.util.Validator;

/**
 * Main window: registration form (left), searchable JTable (centre).
 * Provides Add, View, Update, Delete and Search for student records.
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String[] COURSES = {
        "", "B.Tech CSE", "B.Tech IT", "B.Tech ECE", "B.Tech EE",
        "B.Tech ME", "B.Tech CE", "BCA", "MCA", "M.Tech"
    };
    private static final String[] YEARS = { "", "1st Year", "2nd Year", "3rd Year", "4th Year" };

    // Data access (depends on the interface, not the JDBC class)
    private final StudentRepository repo = new StudentDAO();
    private final StudentTableModel tableModel = new StudentTableModel();
    private final JTable table = new JTable(tableModel);

    // Form components
    private final JTextField txtRoll = new JTextField(18);
    private final JTextField txtName = new JTextField(18);
    private final JComboBox<String> cmbCourse = new JComboBox<>(COURSES);
    private final JComboBox<String> cmbYear = new JComboBox<>(YEARS);
    private final JTextField txtEmail = new JTextField(18);
    private final JTextField txtPhone = new JTextField(18);
    private final JRadioButton rbMale = new JRadioButton("Male");
    private final JRadioButton rbFemale = new JRadioButton("Female");
    private final JRadioButton rbOther = new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();

    private final JButton btnAdd = new JButton("Add");
    private final JButton btnUpdate = new JButton("Update");
    private final JButton btnDelete = new JButton("Delete");
    private final JButton btnClear = new JButton("Clear");

    // Search components
    private final JComboBox<SearchField> cmbSearchBy = new JComboBox<>(SearchField.values());
    private final JTextField txtSearch = new JTextField(18);
    private final JButton btnSearch = new JButton("Search");
    private final JButton btnShowAll = new JButton("Show All");

    private final JLabel lblStatus = new JLabel(" Ready");
    private final String adminName;

    public MainFrame(String adminName) {
        super("Student Record Management System");
        this.adminName = adminName;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 600);
        setMinimumSize(new Dimension(950, 520));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.WEST);
        add(buildTablePanel(), BorderLayout.CENTER);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(lblStatus, BorderLayout.SOUTH);

        registerListeners();
        setFormMode(false);
        refreshTable();
    }

    // ---------------------------------------------------------------- UI building

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 0, 12));

        JLabel title = new JLabel("Student Record Management System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        bar.add(title, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.add(new JLabel("Logged in as: " + adminName));
        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> logout());
        right.add(btnLogout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 0),
                BorderFactory.createTitledBorder("Student Details")));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderGroup.add(rbOther);
        JPanel genderPanel = new JPanel();
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        genderPanel.add(rbOther);

        addRow(form, gbc, 0, "Roll Number *", txtRoll);
        addRow(form, gbc, 1, "Name *", txtName);
        addRow(form, gbc, 2, "Course *", cmbCourse);
        addRow(form, gbc, 3, "Academic Year *", cmbYear);
        addRow(form, gbc, 4, "Email *", txtEmail);
        addRow(form, gbc, 5, "Phone *", txtPhone);
        addRow(form, gbc, 6, "Gender *", genderPanel);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 8, 8));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 8, 8);
        form.add(buttons, gbc);

        // push everything to the top
        gbc.gridy = 8; gbc.weighty = 1.0;
        form.add(new JPanel(), gbc);
        return form;
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, java.awt.Component c) {
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        p.add(c, gbc);
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel search = new JPanel();
        search.setBorder(BorderFactory.createTitledBorder("Search"));
        search.add(new JLabel("Search by:"));
        search.add(cmbSearchBy);
        search.add(txtSearch);
        search.add(btnSearch);
        search.add(btnShowAll);
        panel.add(search, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);          // click a header to sort
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(190);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(70);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);   // vertical + horizontal scrolling
        return panel;
    }

    // ---------------------------------------------------------------- event handling

    private void registerListeners() {
        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());
        btnSearch.addActionListener(e -> onSearch());
        txtSearch.addActionListener(e -> onSearch());          // Enter key in search box
        btnShowAll.addActionListener(e -> {
            txtSearch.setText("");
            refreshTable();
        });

        // Selecting a row loads it into the form for update / delete
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int viewRow = table.getSelectedRow();
            if (viewRow >= 0) {
                Student s = tableModel.getStudentAt(table.convertRowIndexToModel(viewRow));
                populateForm(s);
                setFormMode(true);
            }
        });
    }

    private void onAdd() {
        Student s = readForm();
        try {
            Validator.validate(s);
            repo.add(s);
            refreshTable();
            clearForm();
            setStatus("Student " + s.getRollNo() + " added successfully.");
            JOptionPane.showMessageDialog(this, "Student record added successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (ValidationException ex) {
            showValidationErrors(ex);
        } catch (DuplicateRecordException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Duplicate Roll Number", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void onUpdate() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Select a student from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student s = readForm();
        try {
            Validator.validate(s);
            int choice = JOptionPane.showConfirmDialog(this,
                    "Update the record of " + s.getRollNo() + "?",
                    "Confirm Update", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
            if (repo.update(s)) {
                refreshTable();
                clearForm();
                setStatus("Student " + s.getRollNo() + " updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Record not found. It may have been deleted.",
                        "Update Failed", JOptionPane.WARNING_MESSAGE);
                refreshTable();
            }
        } catch (ValidationException ex) {
            showValidationErrors(ex);
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a student from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student s = tableModel.getStudentAt(table.convertRowIndexToModel(viewRow));
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete " + s.getName() + " (" + s.getRollNo() + ")? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            repo.delete(s.getRollNo());
            refreshTable();
            clearForm();
            setStatus("Student " + s.getRollNo() + " deleted.");
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void onSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search keyword (or click Show All).",
                    "Empty Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        SearchField field = (SearchField) cmbSearchBy.getSelectedItem();
        try {
            List<Student> results = repo.search(field, keyword);
            tableModel.setStudents(results);
            clearFormFields();
            setFormMode(false);
            setStatus(results.size() + " record(s) found for " + field + " containing \"" + keyword + "\".");
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void logout() {
        dispose();
        new LoginFrame().setVisible(true);
    }

    // ---------------------------------------------------------------- helpers

    private void refreshTable() {
        try {
            List<Student> all = repo.findAll();
            tableModel.setStudents(all);
            setStatus("Showing all records: " + all.size() + " student(s).");
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    /** Builds a Student from the form (roll number is trimmed and upper-cased). */
    private Student readForm() {
        return new Student(
                txtRoll.getText().trim().toUpperCase(),
                txtName.getText().trim(),
                (String) cmbCourse.getSelectedItem(),
                (String) cmbYear.getSelectedItem(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim(),
                selectedGender());
    }

    private String selectedGender() {
        if (rbMale.isSelected())   return "Male";
        if (rbFemale.isSelected()) return "Female";
        if (rbOther.isSelected())  return "Other";
        return null;
    }

    private void populateForm(Student s) {
        txtRoll.setText(s.getRollNo());
        txtName.setText(s.getName());
        cmbCourse.setSelectedItem(s.getCourse());
        cmbYear.setSelectedItem(s.getAcademicYear());
        txtEmail.setText(s.getEmail());
        txtPhone.setText(s.getPhone());
        if ("Male".equals(s.getGender()))        rbMale.setSelected(true);
        else if ("Female".equals(s.getGender())) rbFemale.setSelected(true);
        else                                     rbOther.setSelected(true);
    }

    private void clearFormFields() {
        txtRoll.setText("");
        txtName.setText("");
        cmbCourse.setSelectedIndex(0);
        cmbYear.setSelectedIndex(0);
        txtEmail.setText("");
        txtPhone.setText("");
        genderGroup.clearSelection();
    }

    private void clearForm() {
        table.clearSelection();
        clearFormFields();
        setFormMode(false);
        txtRoll.requestFocusInWindow();
    }

    /**
     * editing = true  -> a table row is selected: Update/Delete enabled, Roll No locked
     * editing = false -> new-record mode: only Add enabled
     */
    private void setFormMode(boolean editing) {
        txtRoll.setEditable(!editing);
        btnAdd.setEnabled(!editing);
        btnUpdate.setEnabled(editing);
        btnDelete.setEnabled(editing);
    }

    private void setStatus(String msg) {
        lblStatus.setText(" " + msg);
    }

    private void showValidationErrors(ValidationException ex) {
        StringBuilder sb = new StringBuilder("Please correct the following:\n");
        for (String err : ex.getErrors()) {
            sb.append("\u2022 ").append(err).append('\n');
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    private void showDbError(SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
