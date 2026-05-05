/*
 * CourseResultsCsv handles exporting a course's results to a csv file, with one line per student and one column per assignment.
 */


package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CourseResultsCsv {

    // export the course's students and their scores to a csv file
    public boolean exportToFile(Course course, String filePath) {
        if (course == null || filePath == null || filePath.length() == 0) {
            return false;
        }

        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(filePath));
            try {
                ArrayList<Assignment> assigns = course.getAssignments();
                ArrayList<Student> students = course.getStudents();
                if (assigns == null) {
                    assigns = new ArrayList<Assignment>();
                }
                if (students == null) {
                    students = new ArrayList<Student>();
                }

                writeHeader(w, assigns);
                for (int i = 0; i < students.size(); i++) {
                    writeStudentRow(w, students.get(i), assigns);
                }
            } finally {
                w.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // first line is fixed columns plus one column per assignment
    private void writeHeader(BufferedWriter w, ArrayList<Assignment> assigns) throws IOException {
        w.write("studentId,name,email,active");
        for (int i = 0; i < assigns.size(); i++) {
            w.write(",");
            w.write(escapeCell(assigns.get(i).getName()));
        }
        w.write(",finalPercent,letterGrade");
        w.newLine();
    }

    // one line per student with blank cell if a score was never entered
    private void writeStudentRow(BufferedWriter w, Student s, ArrayList<Assignment> assigns)
            throws IOException {
        w.write(escapeCell(s.getStudentId()));
        w.write(",");
        w.write(escapeCell(s.getName()));
        w.write(",");
        w.write(escapeCell(s.getEmail()));
        w.write(",");
        w.write(s.isActive() ? "true" : "false");

        for (int i = 0; i < assigns.size(); i++) {
            w.write(",");
            String colName = assigns.get(i).getName();
            if (s.hasScore(colName)) {
                w.write(scoreText(s.getScore(colName)));
            }
        }

        w.write(",");
        w.write(scoreText(s.getFinalPercent()));
        w.write(",");
        if (s.getLetterGrade() != null) {
            w.write(escapeCell(s.getLetterGrade()));
        }
        w.newLine();
    }

    private String scoreText(double value) {
        return Double.toString(value);
    }

    // wrap fields that could break csv commas
    private String escapeCell(String value) {
        if (value == null) {
            return "";
        }
        if (value.indexOf(',') >= 0 || value.indexOf('"') >= 0 || value.indexOf('\n') >= 0) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
