// swing portal window for basic actions
package gui;

import gui.FileHandler;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import core.Grader;
import core.Student;
import core.Assignment;
import core.Course;
import core.CourseManager;
import core.GradeRange;
import core.ScaleLetterRefresh;
import core.StudentImportCsv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class Portal extends JFrame implements ActionListener {
    private JButton addCourseButton;
    private JButton coursesButton;
    private JButton importButton;

    private CourseManager courseManager;
    private JPanel courseDisplay;

    private JPanel addCoursePanel;
    private JPanel centerPanel;

    private JTextField courseNameField;
    private JTextField courseIdField;
    private Course currentCourse;

    private Grader grader;
    private HashSet<Course> scaleLetterRefreshDone;

    public Portal(CourseManager c) {
        super("Grading Portal");
        this.courseManager = c;
        this.grader = new Grader();
        this.scaleLetterRefreshDone = new HashSet<Course>();
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        coursesButton = new JButton("Courses");
        coursesButton.setActionCommand("courses_menu");
        coursesButton.addActionListener(this);

        addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(this);

        topPanel.add(coursesButton);
        topPanel.add(addCourseButton);

        add(topPanel, BorderLayout.NORTH);

        // center panel
        centerPanel = new JPanel(new BorderLayout());
        courseDisplay = new JPanel();
        courseDisplay.setLayout(new BoxLayout(courseDisplay, BoxLayout.Y_AXIS));

        centerPanel.add(new JScrollPane(courseDisplay), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        importButton = new JButton("Import Grades");
        importButton.addActionListener(this);
        importButton.setPreferredSize(new Dimension(140, 25));
        importButton.setVisible(false);
        centerPanel.add(importButton);

        // add course panel (bottom)
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

    @Override
    public void actionPerformed(ActionEvent e) {

        // import grades into current course
        if (e.getActionCommand().equals("Import Grades")) {
            if (currentCourse == null) {
                JOptionPane.showMessageDialog(this, "Please open a course first.");
                return;
            }
            FileHandler f = new FileHandler();
            File file = f.importFile(this);
            if (file == null)
                return;

            runStudentCsvImport(currentCourse, file);
        }

        // show course dropdown
        if (e.getActionCommand().equals("courses_menu")) {
            JPopupMenu popupMenu = new JPopupMenu();
            List<Course> courses = courseManager.getCourses();
            if (courses != null) {
                for (Course c : courses) {
                    JMenuItem item = new JMenuItem(c.getCourseId());
                    item.addActionListener(ev -> showCourseView(c));
                    popupMenu.add(item);
                }
            }
            popupMenu.show(this, 100, 50);
        }

        // show add course panel
        if (e.getActionCommand().equals("Add Course")) {
            addCoursePanel.setVisible(true);
            revalidate();
            repaint();
        }
    }

    // refresh the course list on the home screen
    private void refreshCourseList() {
        centerPanel.removeAll();

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

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
            listPanel.add(row);
        }

        centerPanel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    // show course detail view with student grade table
    private void showCourseView(Course course) {
        this.currentCourse = course;
        ensureScaleLetterRefresh(course);
        courseDisplay.removeAll();
        courseDisplay.setLayout(new BorderLayout());

        // top bar
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(ev -> {
            courseDisplay.setLayout(new BoxLayout(courseDisplay, BoxLayout.Y_AXIS));
            refreshCourseList();
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(backBtn);
        topBar.add(new JLabel("  " + course.getCourseId() + " - " + course.getCourseName()));

        // edit course dropdown
        JButton editBtn = new JButton("Edit Course");
        editBtn.addActionListener(ev -> {
            JPopupMenu editMenu = new JPopupMenu();

            JMenuItem importGrades = new JMenuItem("Import Grades");
            JMenuItem addAssignment = new JMenuItem("Add Assignment");
            JMenuItem removeAssignment = new JMenuItem("Remove Assignment");
            JMenuItem adjustWeights = new JMenuItem("Adjust Weights");
            JMenuItem adjustBoundaries = new JMenuItem("Adjust Grade Boundaries");
            JMenuItem curveToTop = new JMenuItem("Curve Scale to Top Student");
            JMenuItem resetGradeScale = new JMenuItem("Reset Grade Scale to Default");
            JMenuItem markInactive = new JMenuItem("Set Student Activity");
            JMenuItem removeCourse = new JMenuItem("Remove Course");

            importGrades.addActionListener(e -> {
                FileHandler f = new FileHandler();
                File file = f.importFile(this);
                if (file == null)
                    return;
                runStudentCsvImport(course, file);
            });

            addAssignment.addActionListener(e -> addAssignment(course));
            removeAssignment.addActionListener(e -> removeAssignment(course));
            adjustWeights.addActionListener(e -> adjustWeights(course));
            adjustBoundaries.addActionListener(e -> adjustBoundaries(course));
            curveToTop.addActionListener(e -> {
                grader.applyTopScoreShift(course);
                showCourseView(course);
            });
            resetGradeScale.addActionListener(e -> {
                course.getGradeScale().resetToDefault();
                grader.assignLetterGradesForCourse(course);
                showCourseView(course);
            });
            markInactive.addActionListener(e -> editStudentActivity(course));
            removeCourse.addActionListener(e -> {
                courseManager.removeCourse(course.getCourseId());
                refreshCourseList();
            });

            editMenu.add(importGrades);
            editMenu.add(addAssignment);
            editMenu.add(removeAssignment);
            editMenu.add(adjustWeights);
            editMenu.add(adjustBoundaries);
            editMenu.add(curveToTop);
            editMenu.add(resetGradeScale);
            editMenu.add(markInactive);
            editMenu.add(removeCourse);

            editMenu.show(editBtn, 0, editBtn.getHeight());
        });
        topBar.add(editBtn);
        courseDisplay.add(topBar, BorderLayout.NORTH);

        // build table columns (status text + normalized weight percent per assignment)
        List<Assignment> assignments = course.getAssignments();
        String[] columns = new String[assignments.size() + 4];
        columns[0] = "Student";
        columns[1] = "Active Status";
        double weightSum = grader.getTotalWeight(course);
        for (int i = 0; i < assignments.size(); i++) {
            Assignment a = assignments.get(i);
            String name = a.getName();
            if (weightSum > 0 && a.getWeight() > 0) {
                double share = 100.0 * a.getWeight() / weightSum;
                columns[i + 2] = String.format("%s (%.1f%%)", name, share);
            } else {
                columns[i + 2] = name + " (—)";
            }
        }
        columns[columns.length - 2] = "Final %";
        columns[columns.length - 1] = "Grade";

        // all students in roster (inactive still shown)
        List<Student> students = course.getStudents();
        Object[][] data = new Object[students.size()][columns.length];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i][0] = s.getName();
            data[i][1] = s.isActive() ? "Active" : "Inactive";
            for (int j = 0; j < assignments.size(); j++) {
                data[i][j + 2] = s.getScore(assignments.get(j).getName());
            }
            data[i][columns.length - 2] = String.format("%.1f%%", s.getFinalPercent());
            data[i][columns.length - 1] = s.getLetterGrade();
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        courseDisplay.add(new JScrollPane(table), BorderLayout.CENTER);

        centerPanel.removeAll();
        centerPanel.add(courseDisplay, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    // add assignment dialog
    private void addAssignment(Course course) {
        JTextField nameField = new JTextField();
        JTextField weightField = new JTextField();
        JTextField maxPointsField = new JTextField();

        Object[] fields = {
                "Assignment Name:", nameField,
                "Weight (e.g. 0.20):", weightField,
                "Max Points:", maxPointsField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Assignment", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                double weight = Double.parseDouble(weightField.getText().trim());
                double maxPoints = Double.parseDouble(maxPointsField.getText().trim());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Assignment name cannot be empty.");
                    return;
                }

                Assignment a = new Assignment(name, weight, maxPoints, "");
                course.addAssignment(a);
                grader.calculateFinalPercentsForCourse(course);
                grader.assignLetterGradesForCourse(course);
                showCourseView(course);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Weight and Max Points must be numbers.");
            }
        }
    }

    // remove assignment dialog
    private void removeAssignment(Course course) {
        List<Assignment> assignments = course.getAssignments();
        if (assignments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assignments to remove.");
            return;
        }

        String[] names = assignments.stream().map(Assignment::getName).toArray(String[]::new);
        String selected = (String) JOptionPane.showInputDialog(this, "Select assignment to remove:",
                "Remove Assignment", JOptionPane.PLAIN_MESSAGE, null, names, names[0]);

        if (selected != null) {
            assignments.removeIf(a -> a.getName().equals(selected));
            grader.calculateFinalPercentsForCourse(course);
            grader.assignLetterGradesForCourse(course);
            showCourseView(course);
        }
    }

    // adjust weights dialog
    private void adjustWeights(Course course) {
        List<Assignment> assignments = course.getAssignments();
        if (assignments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assignments to adjust.");
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField[] fields = new JTextField[assignments.size()];

        for (int i = 0; i < assignments.size(); i++) {
            Assignment a = assignments.get(i);
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel(a.getName() + ":"));
            fields[i] = new JTextField(String.valueOf(a.getWeight()), 8);
            row.add(fields[i]);
            panel.add(row);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, "Adjust Weights", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                for (int i = 0; i < assignments.size(); i++) {
                    double weight = Double.parseDouble(fields[i].getText().trim());
                    assignments.get(i).setWeight(weight);
                }
                grader.calculateFinalPercentsForCourse(course);
                grader.assignLetterGradesForCourse(course);
                showCourseView(course);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "All weights must be numbers.");
            }
        }
    }

    // adjust grade boundaries dialog
    private void adjustBoundaries(Course course) {

        List<GradeRange> ranges = course.getGradeScale().getRanges();

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JTextField[] minFields = new JTextField[ranges.size()];
        JTextField[] maxFields = new JTextField[ranges.size()];

        for (int i = 0; i < ranges.size(); i++) {
            GradeRange r = ranges.get(i);
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.add(new JLabel(String.format("%-4s", r.getLetter())));
            row.add(new JLabel("min"));
            minFields[i] = new JTextField(String.valueOf(r.getMin()), 6);
            row.add(minFields[i]);
            row.add(new JLabel("max"));
            maxFields[i] = new JTextField(String.valueOf(r.getMax()), 6);
            row.add(maxFields[i]);
            panel.add(row);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, "Adjust Grade Boundaries", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                boolean ok = true;
                for (int i = 0; i < ranges.size(); i++) {
                    GradeRange r = ranges.get(i);
                    double newMin = Double.parseDouble(minFields[i].getText().trim());
                    double newMax = Double.parseDouble(maxFields[i].getText().trim());
                    if (!course.getGradeScale().updateRange(r.getLetter(), newMin, newMax)) {
                        JOptionPane.showMessageDialog(this,
                                "could not apply range for " + r.getLetter() + " (check ordering vs other letters)");
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    grader.assignLetterGradesForCourse(course);
                    showCourseView(course);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "All values must be numbers.");
            }
        }
    }

    // merge csv into course (creates assignment columns if missing) then grade
    private void runStudentCsvImport(Course course, File file) {
        StudentImportCsv importer = new StudentImportCsv();
        if (!importer.mergeFromFile(course, file.getAbsolutePath())) {
            JOptionPane.showMessageDialog(this, "could not read that file.");
            return;
        }
        grader.calculateFinalPercentsForCourse(course);
        grader.assignLetterGradesForCourse(course);
        showCourseView(course);
    }

    // checkboxes: checked = active, unchecked = inactive
    private void editStudentActivity(Course course) {
        List<Student> students = course.getStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students in this course.");
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JCheckBox[] boxes = new JCheckBox[students.size()];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            boxes[i] = new JCheckBox(s.getName() + "  (" + s.getStudentId() + ")", s.isActive());
            panel.add(boxes[i]);
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setPreferredSize(new Dimension(440, 300));

        int result = JOptionPane.showConfirmDialog(this, scroll, "Student activity (checked = active)",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            for (int i = 0; i < students.size(); i++) {
                students.get(i).setActive(boxes[i].isSelected());
            }
            grader.assignLetterGradesForCourse(course);
            showCourseView(course);
        }
    }

    // attach observer to refresh letter grades when scale changes
    private void ensureScaleLetterRefresh(Course course) {
        if (course == null)
            return;
        if (scaleLetterRefreshDone.contains(course))
            return;
        ScaleLetterRefresh.attach(course, grader);
        scaleLetterRefreshDone.add(course);
    }

    // handle new course submission
    private void handleInitCourse() {
        String courseName = courseNameField.getText().trim();
        String courseId = courseIdField.getText().trim();

        if (courseName.isEmpty() || courseId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Course Name and ID");
            return;
        }

        courseManager.createBlankCourse(courseName, courseId);

        courseNameField.setText("");
        courseIdField.setText("");
        addCoursePanel.setVisible(false);

        refreshCourseList();
        revalidate();
        repaint();
    }
}