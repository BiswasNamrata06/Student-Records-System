package srms.model;

/**
 * Fields a student can be searched by. Each constant carries the matching
 * database column, so user input never decides which column is queried
 * (this prevents SQL injection through the column name).
 */
public enum SearchField {
    ROLL_NO("Roll Number", "roll_no"),
    NAME("Student Name", "name"),
    COURSE("Course", "course");

    private final String label;
    private final String column;

    SearchField(String label, String column) {
        this.label = label;
        this.column = column;
    }

    public String getColumn() { return column; }

    /** Used by JComboBox to display the item. */
    @Override
    public String toString() { return label; }
}
