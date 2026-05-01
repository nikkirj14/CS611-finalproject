// swing portal window for basic actions

package gui;

import gui.FileHandler;
import javax.swing.*;

import core.Student;
import core.Assignment;
import core.Course;
import core.CourseManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

public class Portal extends JFrame implements ActionListener {
    private JButton importButton;
    private CourseManager courseManager;
    private JTextArea courseDisplay;

    public Portal(CourseManager c) {
        // create window
        super("Grading Portal");
        this.courseManager = c;
        setSize(12000, 8000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close app when you click X 
        setLayout(new BorderLayout());

        courseDisplay = new JTextArea();
        courseDisplay.setEditable(false);
        add(new JScrollPane(courseDisplay), BorderLayout.CENTER);

        // create button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton coursesButton = new JButton("Course ▾");
        coursesButton.addActionListener(this);

        importButton = new JButton("Import Grades");
        importButton.addActionListener(this);

        topPanel.add(coursesButton);
        topPanel.add(importButton);

        add(topPanel, BorderLayout.NORTH);

        // coursesButton.addActionListener(ev -> {showCourse(c);});
        // add(coursesButton);

        // // create button
        // importButton = new JButton("Import Grades");
        // importButton.addActionListener(this);
        // importButton.setBackground(Color.BLACK);
        // importButton.setHorizontalAlignment(SwingConstants.LEFT);
        // importButton.setVerticalAlignment(SwingConstants.TOP);
        // importButton.setBounds(100, 50, 150, 50);
        // importButton.setBorder(BorderFactory.createEtchedBorder());
        // setLayout(new FlowLayout(FlowLayout.LEFT));
        // add(importButton);

    }

    // handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("Import Grades")) {
            FileHandler f = new FileHandler();
            File file = f.importFile(this);
            List<Student> students = f.parseScores(file);
            System.out.println("Parsed " + students.size() + " students from the file.");
        }


        if (e.getActionCommand().equals("Course ▾")) {
            JPopupMenu popupMenu = new JPopupMenu();
            List<Course> courses = courseManager.getCourses();
            if (courses != null) {
                for (Course c : courses) {
                    JMenuItem item = new JMenuItem(c.getCourseId());

                    item.addActionListener(ev -> {
                        showCourseData(c); 
                    });

                    popupMenu.add(item);
                }
            } 
            popupMenu.show(this, 100, 50);
        }
        
    }

    private void showCourseData(Course course) {

        StringBuilder sb = new StringBuilder();

        sb.append("Course ID: ").append(course.getCourseId()).append("\n");
        sb.append("Course Name: ").append(course.getCourseName()).append("\n\n");

        sb.append("Assignments:\n");

        for (Assignment a : course.getAssignments()) {
            sb.append("- ")
            .append(a.getName())
            .append(" (weight: ")
            .append(a.getWeight())
            .append(", max: ")
            .append(a.getMaxPoints())
            .append(")\n");
        }

        courseDisplay.setText(sb.toString());
    }
}
