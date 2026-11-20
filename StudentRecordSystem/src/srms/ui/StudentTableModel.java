package srms.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import srms.model.Student;

/**
 * TableModel that feeds a JTable from a List of Student objects.
 * Extending AbstractTableModel (inheritance) keeps the table logic separate
 * from the form logic; the cells are read-only, editing is done via the form.
 */
public class StudentTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNS = {
        "Roll No", "Name", "Course", "Academic Year", "Email", "Phone", "Gender"
    };

    private List<Student> students = new ArrayList<>();

    public void setStudents(List<Student> list) {
        this.students = new ArrayList<>(list);
        fireTableDataChanged();
    }

    public Student getStudentAt(int modelRow) {
        return students.get(modelRow);
    }

    @Override public int getRowCount()    { return students.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        Student s = students.get(row);
        switch (col) {
            case 0:  return s.getRollNo();
            case 1:  return s.getName();
            case 2:  return s.getCourse();
            case 3:  return s.getAcademicYear();
            case 4:  return s.getEmail();
            case 5:  return s.getPhone();
            case 6:  return s.getGender();
            default: return "";
        }
    }
}
