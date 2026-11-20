# Student Record Management System

A desktop-based **Student Record Management System** developed using **Java Swing** and **SQLite**. The application provides an administrator login and a graphical interface for managing student records, searching the student list, and storing data locally in an SQLite database.

## Overview

The system is designed to make common student-record tasks easier through a single desktop interface. An administrator can add, view, update, delete, and search student information. Student data is stored in a local SQLite database, which is initialized automatically when the application starts for the first time.

## Features

- **Administrator login** with a default account created on first database initialization.
- **Add student records** with roll number, name, course, academic year, email, phone number, and gender.
- **Update and delete** existing student records.
- **View all records** in a tabular interface.
- **Search** by roll number, student name, or course.
- **Sort table rows** by clicking a table column heading.
- **Input validation** for required fields, roll-number format, email format, and phone number.
- **Duplicate roll-number prevention** because each roll number uniquely identifies a student.
- **SQLite persistence** so records remain available between application runs.
- **Logout** to return to the administrator login screen.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java (JDK 11 or newer) | Application logic and object-oriented design |
| Java Swing | Desktop graphical user interface |
| SQLite | Local relational database |
| JDBC | Connecting Java code to SQLite |
| SQLite JDBC (`sqlite-jdbc-3.36.0.3.jar`) | SQLite database driver |

## Project Structure

```text
StudentRecordSystem/
├── .vscode/                 # VS Code project settings (if included)
├── lib/
│   └── sqlite-jdbc-3.36.0.3.jar
├── src/
│   └── srms/
│       ├── Main.java        # Application entry point
│       ├── db/              # Database connection and data access classes
│       │   ├── AdminDAO.java
│       │   ├── DatabaseManager.java
│       │   ├── StudentDAO.java
│       │   └── StudentRepository.java
│       ├── exception/       # Custom validation and duplicate-record exceptions
│       ├── model/           # Student model and search-field definitions
│       ├── ui/              # Swing login, main window, and table model
│       └── util/            # Input validation utilities
├── .gitignore
└── README.md
```

Generated files such as `out/` and `students.db` may appear after building or running the application; they are not required source files.

## Requirements

Before running the project, make sure you have:

1. **JDK 11 or newer** installed.
2. **Visual Studio Code** (optional) with the **Extension Pack for Java** installed, or another Java IDE.
3. The SQLite JDBC driver file `sqlite-jdbc-3.36.0.3.jar` placed inside the project's `lib/` directory.

Check that Java is installed by running:

```powershell
java -version
javac -version
```

## Setup and Run

### Option 1: Run from the VS Code terminal on Windows

1. Open the project folder that contains the `src` and `lib` directories in VS Code.
2. Confirm that `lib/sqlite-jdbc-3.36.0.3.jar` exists.
3. Open **Terminal → New Terminal**.
4. Run the following commands from the project root:

```powershell
$files = Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName }
New-Item -ItemType Directory -Force -Path out | Out-Null
javac -d out -cp "lib\sqlite-jdbc-3.36.0.3.jar" $files
java -cp "out;lib\sqlite-jdbc-3.36.0.3.jar" srms.Main
```

The administrator login window should open.

### Option 2: Run from Windows Command Prompt

Run these commands from the project root:

```bat
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out -cp "lib\sqlite-jdbc-3.36.0.3.jar" @sources.txt
java -cp "out;lib\sqlite-jdbc-3.36.0.3.jar" srms.Main
```

### Linux or macOS

Run from the project root (using the same JDK and JDBC driver):

```bash
mkdir -p out
javac -d out -cp "lib/sqlite-jdbc-3.36.0.3.jar" $(find src -name "*.java")
java -cp "out:lib/sqlite-jdbc-3.36.0.3.jar" srms.Main
```

> **Note:** The classpath separator is a semicolon (`;`) on Windows and a colon (`:`) on Linux/macOS.

## Default Administrator Login

On a fresh database, the application creates this default administrator account:

| Field | Default value |
|---|---|
| Username | `admin` |
| Password | `admin123` |

The password is stored in the database as a SHA-256 hash rather than plain text. The login screen allows up to **three failed attempts** before the application closes.

**Important:** These are default demonstration credentials. Change or replace them before using the application with real or sensitive data. Do not publish a populated database or real student information in a public repository.

## Database

- The database file is named **`students.db`**.
- It is created automatically in the application's **current working directory** when the program starts.
- The application creates the `students` and `admin` tables if they do not already exist.
- Student roll numbers are unique and used as the primary key.
- Keep a backup of `students.db` if you need to preserve locally entered records.

## Student Data Validation

| Field | Validation rule |
|---|---|
| Roll Number | Required; 3–20 characters; letters, digits, `/`, `_`, and `-` are allowed; saved in uppercase; must be unique |
| Name | Required; 2–50 characters; letters, spaces, periods, apostrophes, and hyphens are allowed |
| Course | Required selection |
| Academic Year | Required selection |
| Email | Required and must match a valid email format |
| Phone | Required; exactly 10 digits and must start with 6, 7, 8, or 9 |
| Gender | Required selection |

The application reports validation problems to the user before saving a record.

## Main Application Workflow

1. Start the application using one of the commands above.
2. Sign in using the default administrator credentials (on a fresh database).
3. Enter the student's details in the form.
4. Select **Add** to save a new record.
5. Select a row in the table to load its details into the form; use **Update** to save changes or **Delete** to remove the selected record.
6. Use the search controls to search by roll number, student name, or course. Select **Show All** to display the complete student list again.
7. Select **Clear** to reset the form or **Logout** to return to the login screen.

## Troubleshooting

| Problem | What to check |
|---|---|
| `javac` is not recognized | Install a JDK and configure `JAVA_HOME`/`PATH`, then reopen the terminal. |
| `SQLite JDBC driver not found` | Check that the JAR is in `lib/` and that the classpath includes it. |
| `Could not find or load main class srms.Main` | Run the command from the project root and confirm compilation completed into `out/`. |
| `package org.sqlite does not exist` or driver-related errors | Confirm the correct SQLite JDBC JAR is present and included in the compile/run classpath. |
| Login does not accept the default credentials | The default account is seeded only when the `admin` table is empty. Check whether an existing `students.db` contains a previously created admin account. |

## Author / Academic Use

This project was developed as an application-based academic project for demonstrating object-oriented programming, Java Swing GUI development, JDBC connectivity, database operations, and input validation.

---

*To run the application, follow the Setup and Run instructions above.*
