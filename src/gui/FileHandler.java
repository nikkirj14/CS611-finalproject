// file helper for csv import flow

package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.Student;
import core.Assignment;
import core.Course;


public class FileHandler {

    // import a csv file
    public File importFile(Portal frame) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.rescanCurrentDirectory();
        int result = fileChooser.showOpenDialog(frame);// opens dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            if (!file.getName().toLowerCase().endsWith(".csv")) {
                JOptionPane.showMessageDialog(frame, "Please select a CSV file.");
                return null;
            }

            System.out.println("Valid CSV file selected: " + file.getAbsolutePath());
            return file;

        }

        return null;
    }

    // parse students and scores from csv
    public List<Student> parseScores(File file) {
        List<Student> students = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            String headerLine = reader.readLine();
            if (headerLine == null) {
                return students;
            }

            String[] headers = headerLine.split(",");

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 2)
                    continue; // basic safety check

                String id = parts[0].trim();
                String name = parts[1].trim();
                String email = parts[2].trim();

                HashMap<String, Double> scores = new HashMap<>();

                for (int i = 3; i < parts.length; i++) {
                    String columnName = headers[i].trim();

                    try {
                        double value = Double.parseDouble(parts[i].trim());
                        scores.put(columnName, value);
                    } catch (NumberFormatException e) {
                        scores.put(columnName, 0.0); // to handle missing/invalid values
                    }
                }

                Student student = new Student(id, name, email, scores);
                students.add(student);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading file: " + file.getAbsolutePath());
            return null;
        }

        return students;
    }

    public Map<String, Course> parseWeights(File file) {
        Map<String, Course> courses = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            String headerLine = reader.readLine();
            if (headerLine == null) {
                return courses;
            }

            // courseId, courseName, assignmentName, weight, maxPoints, note
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                if (parts.length < 5) {
                    System.out.println("Skipping invalid row: " + line);
                    continue;
                }

                String courseId = parts[0].trim();
                String courseName = parts[1].trim();
                String assignmentName = parts[2].trim();
                double weight = Double.parseDouble(parts[3].trim().replace("\r", ""));
                double maxPoints = Double.parseDouble(parts[4].trim().replace("\r", ""));
                
                String note = (parts.length >= 6) ? parts[5].trim() : "";

                System.out.println("Parsed line - Course ID: " + courseId + ", Course Name: " + courseName + ", Assignment Name: " + assignmentName + ", Weight: " + weight + ", Max Points: " + maxPoints + ", Note: " + note);
                Course course = courses.get(courseId);
                if (course == null) {
                    System.out.println("Creating new course: " + courseId + " - " + courseName);
                    course = new Course(courseName, courseId);
                    courses.put(courseId, course);
                } 
                System.out.println("Adding assignment to course: " + courseId);
                Assignment assignment = new Assignment(assignmentName, weight, maxPoints, note);
                System.out.println("Created assignment: " + assignmentName + " with weight " + weight + " and max points " + maxPoints);
                course.addAssignment(assignment);
                System.out.println("Added assignment '" + assignmentName + "' to course '" + courseId + "' with weight " + weight + " and max points " + maxPoints);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error reading file: " + file.getAbsolutePath());
            return null;
        }

        return courses;
    }

    public boolean validateFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        return file.getName().toLowerCase().endsWith(".csv");
    }

    public Map<String, Course> loadData(String filePath) {
        System.out.println("Loading data from: " + filePath);
        File file = new File(filePath);
        Map<String, Course> courses = new HashMap<>();

        courses = parseWeights(file);
        if (courses != null) {
            System.out.println("Loaded " + courses.size() + " courses from " + filePath);
        } else {
            System.out.println("Failed to load data from " + filePath);
        }
        return courses;
    }




    
}
