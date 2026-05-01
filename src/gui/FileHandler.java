// file helper for csv import flow

package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.Student;

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
            return null;
        }

        return students;
    }

}
