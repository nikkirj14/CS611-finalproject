// swing portal window for basic actions

package gui;

import gui.FileHandler;
import javax.swing.*;
import core.Grader;
import core.Student;
import core.Assignment;
import core.Course;
import core.CourseManager;
import core.Grader;

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
    private JPanel courseDisplay;
    private Course currentCourse;

    public Portal(CourseManager c) {
        // create window
        super("Grading Portal");
        this.courseManager = c;
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close app when you click X 
        setLayout(new BorderLayout());

        courseDisplay = new JPanel();
        courseDisplay.setLayout(new BoxLayout(courseDisplay, BoxLayout.Y_AXIS) );
        add(new JScrollPane(courseDisplay), BorderLayout.CENTER);

        // create button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton coursesButton = new JButton("Course v");
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

        refreshCourseList();

    }

    // handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {

    if (e.getActionCommand().equals("Import Grades")) {
        if (currentCourse == null) {
            JOptionPane.showMessageDialog(this, "Please open a course first.");
            return;
        }
        FileHandler f = new FileHandler();
        File file = f.importFile(this);
        if (file == null) return;
        
        List<Student> students = f.parseScores(file);
        for (Student s : students) {
            currentCourse.addStudent(s);
        }
        
        Grader grader = new Grader();
        grader.calculateFinalPercentsForCourse(currentCourse);
        grader.assignLetterGradesForCourse(currentCourse);
        
        showCourseView(currentCourse);
    }


        if (e.getActionCommand().equals("Course v")) {
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
        
    }

    // private void showCourseData(Course course) {

    //     StringBuilder sb = new StringBuilder();

    //     sb.append("Course ID: ").append(course.getCourseId()).append("\n");
    //     sb.append("Course Name: ").append(course.getCourseName()).append("\n\n");

    //     sb.append("Assignments:\n");

    //     for (Assignment a : course.getAssignments()) {
    //         sb.append("- ")
    //         .append(a.getName())
    //         .append(" (weight: ")
    //         .append(a.getWeight())
    //         .append(", max: ")
    //         .append(a.getMaxPoints())
    //         .append(")\n");
    //     }

    //     courseDisplay.add(new JLabel(sb.toString()));
    //     courseDisplay.revalidate();
    //     courseDisplay.repaint();
    // }



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
}
