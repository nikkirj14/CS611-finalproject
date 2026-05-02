// reads a student grade csv and merges it into one course

package core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StudentImportCsv {

    // studentId, name, email, then assignment columns
    public boolean mergeFromFile(Course course, String filePath) {
        if (course == null || filePath == null || filePath.length() == 0) {
            return false;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            try {
                String headerLine = reader.readLine();
                if (headerLine == null) {
                    return false;
                }

                String[] headers = headerLine.split(",");
                if (headers.length < 3) {
                    return false;
                }

                for (int h = 3; h < headers.length; h++) {
                    ensureAssignment(course, headers[h].trim());
                }

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 3) {
                        continue;
                    }

                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String email = parts[2].trim();

                    Student student = findStudentById(course, id);
                    if (student == null) {
                        student = new Student(id, name, email);
                        course.addStudent(student);
                    } else {
                        student.setName(name);
                        student.setEmail(email);
                    }

                    for (int i = 3; i < headers.length && i < parts.length; i++) {
                        String col = headers[i].trim();
                        try {
                            double val = Double.parseDouble(parts[i].trim());
                            student.setScore(col, val);
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                }
            } finally {
                reader.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // find student by id or return null
    private Student findStudentById(Course course, String id) {
        if (id == null) {
            return null;
        }
        ArrayList<Student> list = course.getStudents();
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            Student s = list.get(i);
            if (id.equals(s.getStudentId())) {
                return s;
            }
        }
        return null;
    }

    // add a placeholder assignment if this column name is new
    private void ensureAssignment(Course course, String assignmentName) {
        if (assignmentName == null || assignmentName.length() == 0) {
            return;
        }

        ArrayList<Assignment> list = course.getAssignments();
        if (list == null) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            if (assignmentName.equals(list.get(i).getName())) {
                return;
            }
        }

        Assignment a = new Assignment(assignmentName, 1.0, 100.0, "");
        course.addAssignment(a);
    }
}
