// swing portal window for basic actions

package gui;

import gui.FileHandler;
import javax.swing.*;
import core.Grader;
import core.Student;
import core.Assignment;
import core.Course;
import core.CourseManager;
import core.ScaleLetterRefresh;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

public class Portal extends JFrame implements ActionListener {
    private JButton addCourseButton;
    private JButton coursesButton;
    private JButton importButton;

    private CourseManager courseManager;
    private JTextArea courseDisplay;

    private JPanel addCoursePanel;
    private JPanel centerPanel;

    private JTextField courseNameField;
    private JTextField courseIdField;
    private Course currentCourse;

    private Grader grader;
    private HashSet<Course> scaleLetterRefreshDone;

    public Portal(CourseManager c) {
        // create window
        super("Grading Portal");
        this.courseManager = c;
        this.grader = new Grader();
        this.scaleLetterRefreshDone = new HashSet<Course>();
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close app when you click X 
        setLayout(new BorderLayout());

        // top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        coursesButton = new JButton("Courses ▾");
        coursesButton.addActionListener(this);

        addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(this);

        topPanel.add(coursesButton);
        topPanel.add(addCourseButton);

        add(topPanel, BorderLayout.NORTH);


        // center
        centerPanel = new JPanel(new BorderLayout());
        courseDisplay = new JTextArea();
        courseDisplay.setEditable(false);

        centerPanel.add(new JScrollPane(courseDisplay), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        importButton = new JButton("Import Grades");
        importButton.addActionListener(this);
        importButton.setPreferredSize(new Dimension(140, 25));
        importButton.setVisible(false); // hidden initially

        centerPanel.add(importButton);

        add(centerPanel, BorderLayout.CENTER);

        // course name input fields
        addCoursePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        courseNameField = new JTextField(15);
        courseIdField = new JTextField(15);

        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(e -> handleInitCourse());

        addCoursePanel.add(new JLabel("Course Name:"));
        addCoursePanel.add(courseNameField);

        addCoursePanel.add(new JLabel("Course ID:"));
        addCoursePanel.add(courseIdField);

        addCoursePanel.add(submitButton);

        addCoursePanel.setVisible(false);

        add(addCoursePanel, BorderLayout.SOUTH);

        setVisible(true);

        refreshCourseList();

    }

    // handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("Import Grades")) {
            // if (currentCourse == null) {
            //     JOptionPane.showMessageDialog(this, "Please open a course first.");
            //     return;
            // }
            FileHandler f = new FileHandler();
            File file = f.importFile(this);
            if (file == null) return;
            List<Student> students = f.parseScores(file);
                
            System.out.println("Parsed " + students.size() + " students from the file.");
            displayCourseStats(students);
            
            // for (Student s : students) {
            //     currentCourse.addStudent(s);
            // }
            
            // Grader grader = new Grader();
            // grader.calculateFinalPercentsForCourse(currentCourse);
            // grader.assignLetterGradesForCourse(currentCourse);
            
            // showCourseView(currentCourse);
        }

        if (e.getActionCommand().equals("Courses ▾")) {
            JPopupMenu popupMenu = new JPopupMenu();
            List<Course> courses = courseManager.getCourses();
            if (courses != null) {
                for (Course c : courses) {
                    JMenuItem item = new JMenuItem(c.getCourseId());

                    item.addActionListener(ev -> {
                        showCourseView(c); 
                    });

                    popupMenu.add(item);
                }
            } 
            popupMenu.show(this, 100, 50);
        }

        if (e.getActionCommand().equals("Add Course")) {
            addCoursePanel.setVisible(true);
            revalidate();
            repaint();
        }
        
    }



    // method to refresh and update the course list display in home view
    private void refreshCourseList() {
        courseDisplay.removeAll();
        courseDisplay.setLayout(new BoxLayout(courseDisplay, BoxLayout.Y_AXIS));

        List<Course> courses = courseManager.getCourses();
        for (Course c : courses) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JLabel label = new JLabel(c.getCourseId() + " - " + c.getCourseName());
            JButton openBtn = new JButton("Open");
            openBtn.addActionListener(ev -> showCourseView(c));

            row.add(label, BorderLayout.WEST);
            row.add(openBtn, BorderLayout.EAST);
            courseDisplay.add(row);
        }

        courseDisplay.revalidate();
        courseDisplay.repaint();
    }

    // method to show course details and student scores in a table
    private void showCourseView(Course course) {
    this.currentCourse = course;
    ensureScaleLetterRefresh(course);
    courseDisplay.removeAll();
    courseDisplay.setLayout(new BorderLayout());

    // back button at top
    JButton backBtn = new JButton("Back");
    backBtn.addActionListener(ev -> {
        courseDisplay.setLayout(new BoxLayout(courseDisplay, BoxLayout.Y_AXIS));
        refreshCourseList();
    });
    JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
    topBar.add(backBtn);
    topBar.add(new JLabel("  " + course.getCourseId() + " - " + course.getCourseName()));
    courseDisplay.add(topBar, BorderLayout.NORTH);

    // build column headers
    List<Assignment> assignments = course.getAssignments();
    String[] columns = new String[assignments.size() + 3];
    columns[0] = "Student";
    for (int i = 0; i < assignments.size(); i++) {
        columns[i + 1] = assignments.get(i).getName();
    }
    columns[columns.length - 2] = "Final %";
    columns[columns.length - 1] = "Grade";

    // build rows from active students
    List<Student> students = course.getActiveStudents();
    Object[][] data = new Object[students.size()][columns.length];
    for (int i = 0; i < students.size(); i++) {
        Student s = students.get(i);
        data[i][0] = s.getName();
        for (int j = 0; j < assignments.size(); j++) {
            data[i][j + 1] = s.getScore(assignments.get(j).getName());
        }
        data[i][columns.length - 2] = String.format("%.1f%%", s.getFinalPercent());
        data[i][columns.length - 1] = s.getLetterGrade();
    }

    JTable table = new JTable(data, columns);
    table.setFillsViewportHeight(true);
    courseDisplay.add(new JScrollPane(table), BorderLayout.CENTER);

    courseDisplay.revalidate();
    courseDisplay.repaint();
    }

    // connect scale changes to letter grades once per course instance
    private void ensureScaleLetterRefresh(Course course) {
        if (course == null) {
            return;
        }
        if (scaleLetterRefreshDone.contains(course)) {
            return;
        }
        ScaleLetterRefresh.attach(course, grader);
        scaleLetterRefreshDone.add(course);
    }

    private void handleInitCourse() {
        String courseName = courseNameField.getText().trim();
        String courseId = courseIdField.getText().trim();

        if (courseName.isEmpty() || courseId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Course Name and ID");
            return;
        }

        Course newCourse = new Course(courseName, courseId);
        courseManager.addCourse(newCourse);

        System.out.println("Added course: " + courseName + " (" + courseId + ")");

        // reset UI
        courseNameField.setText("");
        courseIdField.setText("");

        addCoursePanel.setVisible(false);
        importButton.setVisible(true);

        revalidate();
        repaint();
    }

    private void displayCourseStats(List<Student> students) {
        
        String[] columnNames = {"Name", "Grade"};
        Object[][] data = new Object[students.size()][2];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i][0] = s.getName();
            data[i][1] = s.getFinalPercent(); //calculateFinalPercentForStudent(s);
        }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);

            
            centerPanel.removeAll(); 

            centerPanel.add(scrollPane, BorderLayout.CENTER);

            centerPanel.revalidate();
            centerPanel.repaint();
    }

}
