# Student Record Management System (Java Swing + SQLite)

OOP (PCC-CS503) application-based assessment.

## Project structure
```
src/srms/
  Main.java                      entry point
  model/      Student, SearchField
  exception/  ValidationException, DuplicateRecordException
  util/       Validator          (input validation)
  db/         DatabaseManager, StudentRepository (interface), StudentDAO, AdminDAO   (JDBC)
  ui/         LoginFrame, MainFrame, StudentTableModel                                (Swing)
```

## Setup
1. Install JDK 11 or newer (`java -version` and `javac -version` should both work).
2. Download **sqlite-jdbc-3.36.0.3.jar** (Maven Central: org.xerial:sqlite-jdbc) and put it in the `lib/` folder.
   This version needs no extra jars. If you use a newer version, also add `slf4j-api` to `lib/`.

## Compile and run

Windows (Command Prompt, inside this folder):
```
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
java -cp "out;lib\sqlite-jdbc-3.36.0.3.jar" srms.Main
```

Linux / macOS:
```
javac -d out $(find src -name "*.java")
java -cp "out:lib/sqlite-jdbc-3.36.0.3.jar" srms.Main
```

`students.db` is created automatically in the folder you run the command from.

## Default admin login
Username: `admin`  Password: `admin123` (stored as a SHA-256 hash). Three wrong attempts close the app.

## Validation rules
| Field | Rule |
|---|---|
| Roll Number | required, 3-20 chars (letters, digits, / _ -), unique, saved in upper case |
| Name | required, 2-50 chars, letters, spaces and . ' - only |
| Course, Academic Year, Gender | must be selected |
| Email | required, valid format |
| Phone | required, 10 digits, starts with 6-9 |
