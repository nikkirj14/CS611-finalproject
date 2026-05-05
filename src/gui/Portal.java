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
import core.Stats;
import core.StudentImportCsv;
import core.CourseResultsCsv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    private JLabel headerCourseTitle;
    private JButton headerAssignmentStatsBtn;
    private JButton headerDisplayStatsBtn;
    private JButton headerEditBtn;
    private JButton headerBackBtn;
    private FullWidthCenterHeaderBar topHeaderBar;

    public Portal(CourseManager c) {
        super("Grading Portal");
        this.courseManager = c;
        this.grader = new Grader();
        this.scaleLetterRefreshDone = new HashSet<Course>();
        setSize(1200, 800);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // if (isDirty) {
                    int result = JOptionPane.showConfirmDialog(
                        Portal.this,
                        "Save changes before exiting?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_CANCEL_OPTION
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        FileHandler f = new FileHandler();
                        f.saveData("", courseManager.getCourseMap());
                        Portal.this.dispose();
                    } else {
                        Portal.this.dispose();
                    }
            }
        });

        setLayout(new BorderLayout());

        // top panel
        FullWidthCenterHeaderBar headerBar = new FullWidthCenterHeaderBar();
        headerBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(170, 170, 170)),
                BorderFactory.createEmptyBorder(6, 10, 8, 10)));

        coursesButton = new JButton("Courses");
        coursesButton.setActionCommand("courses_menu");
        coursesButton.addActionListener(this);

        addCourseButton = new JButton("Add Course");
        addCourseButton.addActionListener(this);

        JButton saveGradesButton = new JButton("Save Grades");
        saveGradesButton.addActionListener(e -> {
            if (currentCourse == null) {
                JOptionPane.showMessageDialog(this, "Please open a course first.");
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File(currentCourse.getCourseId() + "_grades.csv"));
            int res = chooser.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                CourseResultsCsv exporter = new CourseResultsCsv();
                boolean ok = exporter.exportToFile(currentCourse, file.getAbsolutePath());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Grades saved to " + file.getName());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to save grades.");
                }
            }
        });

        headerCourseTitle = new JLabel();
        headerCourseTitle.setVisible(false);

        // headerAssignmentStatsBtn = new JButton("View Assignment Stats");
        // headerAssignmentStatsBtn.setVisible(false);
        // headerAssignmentStatsBtn.addActionListener(e -> {
        //     if (currentCourse != null) {
        //         showAssignmentStatsDialog(currentCourse);
        //     }
        // });

        headerEditBtn = new JButton("Edit Course");
        headerEditBtn.setVisible(false);
        headerEditBtn.addActionListener(e -> {
            if (currentCourse != null) {
                showEditCourseMenu(headerEditBtn, currentCourse);
            }
        });

        headerDisplayStatsBtn = new JButton("View Stats");
        headerDisplayStatsBtn.setVisible(false);
        headerDisplayStatsBtn.addActionListener(e -> {
            if (currentCourse != null) {
                showCourseStatsMenu(headerDisplayStatsBtn, currentCourse);
            }
        });

        JPanel westHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        westHeader.setOpaque(false);
        // westHeader.add(headerAssignmentStatsBtn);
        westHeader.add(headerEditBtn);
        westHeader.add(headerDisplayStatsBtn);

        headerBackBtn = new JButton("Home");
        headerBackBtn.setVisible(false);
        headerBackBtn.addActionListener(e -> {
            courseDisplay.setLayout(new BoxLayout(courseDisplay, BoxLayout.Y_AXIS));
            refreshCourseList();
        });

        JPanel eastHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        eastHeader.setOpaque(false);
        eastHeader.add(coursesButton);
        eastHeader.add(addCourseButton);
        eastHeader.add(saveGradesButton);
        eastHeader.add(headerBackBtn);

        headerBar.wirePanels(westHeader, headerCourseTitle, eastHeader);
        this.topHeaderBar = headerBar;

        add(headerBar, BorderLayout.NORTH);

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
        JButton cancelAddCourseButton = new JButton("Cancel");
        cancelAddCourseButton.addActionListener(e -> {
            courseNameField.setText("");
            courseIdField.setText("");
            addCoursePanel.setVisible(false);
            revalidate();
            repaint();
        });

        addCoursePanel.add(new JLabel("Course Name:"));
        addCoursePanel.add(courseNameField);
        addCoursePanel.add(new JLabel("Course ID:"));
        addCoursePanel.add(courseIdField);
        addCoursePanel.add(submitButton);
        addCoursePanel.add(cancelAddCourseButton);
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
            popupMenu.show(coursesButton, 0, coursesButton.getHeight());
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
        setCourseHeaderVisible(false);
        centerPanel.removeAll();

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        List<Course> courses = courseManager.getCourses();
        for (Course c : courses) {
            JPanel row = new JPanel(new GridBagLayout());
            row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

            JLabel label = new JLabel(courseHomeRowHtml(c));
            JButton openBtn = new JButton("Open");
            openBtn.addActionListener(ev -> showCourseView(c));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.gridx = 0;
            row.add(Box.createHorizontalGlue(), gbc);
            gbc.gridx = 1;
            gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.CENTER;
            row.add(label, gbc);
            gbc.gridx = 2;
            gbc.weightx = 1.0;
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            right.setOpaque(false);
            right.add(openBtn);
            row.add(right, gbc);

            listPanel.add(row);
        }

        centerPanel.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void setCourseHeaderVisible(boolean visible) {
        headerCourseTitle.setVisible(visible);
        // headerAssignmentStatsBtn.setVisible(visible);
        headerDisplayStatsBtn.setVisible(visible);
        headerEditBtn.setVisible(visible);
        headerBackBtn.setVisible(visible);
        if (topHeaderBar != null) {
            topHeaderBar.revalidate();
            topHeaderBar.repaint();
        }
    }

    private static String htmlEscape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void showAssignmentStatsDialog(Course course) {
        Stats st = new Stats();
        Map<String, Map<String, Double>> byAssign = st.statsByAssignment(course);
        if (byAssign.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No raw scores entered for active students, or there are no assignments.");
            return;
        }

        ArrayList<String> names = new ArrayList<String>(byAssign.keySet());
        Collections.sort(names);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-size:11px'>");
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            Map<String, Double> m = byAssign.get(name);
            sb.append("<b>").append(htmlEscape(name)).append("</b><br/>");
            sb.append(String.format(
                    "&nbsp;&nbsp;Min: %.2f &nbsp; Max: %.2f &nbsp; Mean: %.2f &nbsp; Median: %.2f<br/><br/>",
                    m.get("min"), m.get("max"), m.get("average"), m.get("median")));
        }
        sb.append("<div style='color:gray;font-size:10px'>Based on active students with a score for each assignment.</div>");
        sb.append("</body></html>");

        JLabel label = new JLabel(sb.toString());
        JScrollPane scroll = new JScrollPane(label);
        scroll.setPreferredSize(new Dimension(520, 340));
        JOptionPane.showMessageDialog(this, scroll, "Assignment stats", JOptionPane.PLAIN_MESSAGE);
    }

    private static String courseTitleHtml(Course course) {
        return "<html><div style='text-align:center'><b>Course:</b> "
                + htmlEscape(course.getCourseName()) + " &nbsp;&nbsp; <b>ID:</b> "
                + htmlEscape(course.getCourseId()) + " </div></html>";
    }

    private static String courseHomeRowHtml(Course course) {
        int enrolled = course.getStudents() != null ? course.getStudents().size() : 0;
        int active = course.getActiveStudents().size();
        return "<html><div style='text-align:center'><b>Course:</b> "
                + htmlEscape(course.getCourseName()) + " &nbsp;&nbsp; <b>ID:</b> "
                + htmlEscape(course.getCourseId()) + " &nbsp;&nbsp; <br/>"
                + "Enrolled: " + enrolled + " &nbsp;&middot;&nbsp; Active: " + active
                + "</div></html>";
    }

    private void showEditCourseMenu(JButton anchor, Course course) {
        JPopupMenu editMenu = new JPopupMenu();

            JMenuItem importGrades = new JMenuItem("Import Grades");
            JMenuItem addAssignment = new JMenuItem("Add Assignment");
            JMenuItem removeAssignment = new JMenuItem("Remove Assignment");
            JMenuItem adjustWeights = new JMenuItem("Adjust Assignment Weights");
            JMenuItem adjustBoundaries = new JMenuItem("Adjust Grade Boundaries");
            JMenuItem curveToTop = new JMenuItem("Curve Scale to Top Student");
            JMenuItem resetGradeScale = new JMenuItem("Reset Grade Scale to Default");
            JMenuItem markInactive = new JMenuItem("Set Student Activity");
            JMenuItem editStudentNote = new JMenuItem("Add or Edit Student Note");
            JMenuItem removeCourse = new JMenuItem("Remove Course");

            importGrades.addActionListener(e -> {
                FileHandler f = new FileHandler();
                File file = f.importFile(this);
                if (file == null)
                    return;
                runStudentCsvImport(course, file);
            });

            JMenuItem importWeights = new JMenuItem("Import Weights from Course");
            importWeights.addActionListener(e -> importWeightsFromCourse(course));
            editMenu.add(importWeights);
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
            editStudentNote.addActionListener(e -> editStudentNoteDialog(course));
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
            editMenu.add(editStudentNote);
            editMenu.add(removeCourse);

        editMenu.show(anchor, 0, anchor.getHeight());
    }

    private void showCourseStatsMenu(JButton anchor, Course course) {
        JPopupMenu displayMenu = new JPopupMenu();
        JMenuItem byAssignment = new JMenuItem("By Assignment");
        JMenuItem byStudent = new JMenuItem("By Student");

        byAssignment.addActionListener(e -> {
                displayAssignmentSelectionDialog(course);
            });

        byStudent.addActionListener(e -> {
                displayStudentSelectionDialog(course);
             });
        
        displayMenu.add(byAssignment);
        displayMenu.add(byStudent);

        displayMenu.show(anchor, 0, anchor.getHeight());
    }

    private void displayAssignmentSelectionDialog(Course course) {
        List<Student> students = course.getActiveStudents();
        List<Assignment> assignments = course.getAssignments();
        if (assignments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assignments with scores to display.");
            return; 
        }

        String[] names = new String[assignments.size()];
        for (int i = 0; i < assignments.size(); i++) {
            names[i] = assignments.get(i).getName();
        }

        String selected = (String) JOptionPane.showInputDialog(this, "Select an assignment:",
                "Assignment Selection", JOptionPane.PLAIN_MESSAGE, null, names, names[0]);

        if (selected != null) {
            Assignment chosen = null;
            for (Assignment a : assignments) {
                if (a.getName().equals(selected)) {
                    chosen = a;
                    break;
                }
            }
            if (chosen != null) {
                showAssignmentGraph(students, chosen, course.getGradeScale().getRanges());
            }
        }
    }

    private void displayStudentSelectionDialog(Course course) {
        DefaultListModel<String> model = new DefaultListModel<>();
        List<Student> students = course.getActiveStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active students with scores to display.");
            return; 
        }
        List<Assignment> assignments = course.getAssignments();
        if (assignments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No assignments with scores to display.");
            return; 
        }

        for (Student s : students) {
            model.addElement(s.getName());
        }

        JList<String> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JDialog dialog = new JDialog(this, "Select Student", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(list), BorderLayout.CENTER);

        list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.getSelectedIndex();
                    if (index >= 0) {
                        Student selected = students.get(index);
                        dialog.dispose();
                        
                        showStudentGraph(selected, assignments);
                    }
                }
            }
        });

        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showStudentGraph(Student student, List<Assignment> assignments) {
        StatsByStudentChartPanel panel = new StatsByStudentChartPanel(student, assignments);

        JDialog dialog = new JDialog(this, student.getName() + " - Assignment Scores", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);

        dialog.setSize(600, 300); // or use pack() if preferred
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        System.out.println("Displaying graph for student: " + student.getName());
    }

    private void showAssignmentGraph(List<Student> students, Assignment assignment, List<GradeRange> gradeRanges) {
        List<Double> scores = new ArrayList<>();
        for (Student s : students) {
            Double score = s.getScore(assignment.getName());
            scores.add(score);
        }
        StatsByAssignmentChartPanel panel = new StatsByAssignmentChartPanel(scores, gradeRanges);

        JDialog dialog = new JDialog(this, assignment.getName() + " - Score Distribution", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);

        dialog.setSize(600, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // show course detail view with student grade table
 private void showCourseView(Course course) {
    this.currentCourse = course;
    ensureScaleLetterRefresh(course);
    courseDisplay.removeAll();
    courseDisplay.setLayout(new BorderLayout());

    headerCourseTitle.setText(courseTitleHtml(course));
    setCourseHeaderVisible(true);

    Stats rosterStats = new Stats();
    rosterStats.computeCourseOverview(course);

    JLabel statsLabel = new JLabel(buildCourseStatsHtml(rosterStats, course));
    statsLabel.setHorizontalAlignment(JLabel.CENTER);

    JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    statsRow.setAlignmentX(Component.CENTER_ALIGNMENT);
    statsRow.add(statsLabel);

    LetterGradeBarChartPanel letterChart = new LetterGradeBarChartPanel(rosterStats.letterCounts,
            course.getGradeScale().getRanges(), new Color(	54, 117, 136));
    JPanel chartWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    chartWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
    chartWrap.setBorder(BorderFactory.createTitledBorder("Letter Counts"));
    chartWrap.add(letterChart);

    JPanel underChart = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    JLabel gradeDistLabel = new JLabel(buildGradeDistLabel(course));
    underChart.add(gradeDistLabel);

    JPanel topStack = new JPanel();
    topStack.setLayout(new BoxLayout(topStack, BoxLayout.Y_AXIS));
    topStack.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
    topStack.add(statsRow);
    topStack.add(chartWrap);
    topStack.add(underChart);

    courseDisplay.add(topStack, BorderLayout.NORTH);

    // build table columns
    final List<Assignment> assignments = course.getAssignments();
    final List<Student> students = course.getStudents();

    String[] columns = new String[assignments.size() + 5];
    columns[0] = "Student";
    columns[1] = "Active Status";
    columns[2] = "Notes";
    double weightSum = grader.getTotalWeight(course);
    for (int i = 0; i < assignments.size(); i++) {
        Assignment a = assignments.get(i);
        String name = a.getName();
        if (weightSum > 0 && a.getWeight() > 0) {
            double share = 100.0 * a.getWeight() / weightSum;
            columns[i + 3] = String.format("%s (%.1f%%)", name, share);
        } else {
            columns[i + 3] = name + " (-)";
        }
    }
    columns[columns.length - 2] = "Final %";
    columns[columns.length - 1] = "Grade";

    Object[][] data = new Object[students.size()][columns.length];
    for (int i = 0; i < students.size(); i++) {
        Student s = students.get(i);
        data[i][0] = s.getName();
        data[i][1] = s.isActive() ? "Active" : "Inactive";
        data[i][2] = s.getNote() != null ? s.getNote() : "";
        for (int j = 0; j < assignments.size(); j++) {
            data[i][j + 3] = s.getScore(assignments.get(j).getName());
        }
        data[i][columns.length - 2] = String.format("%.1f%%", s.getFinalPercent());
        data[i][columns.length - 1] = s.getLetterGrade();
    }

    DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
        public boolean isCellEditable(int row, int column) {
            if (column == 2) return true;
            return column >= 3 && column < assignments.size() + 3;
        }
    };

    JTable table = new JTable(tableModel);
    table.setFillsViewportHeight(true);

    tableModel.addTableModelListener(e -> {
        int row = e.getFirstRow();
        int col = e.getColumn();
        Student s = students.get(row);
        Object val = tableModel.getValueAt(row, col);

        if (col == 2) {
            s.setNote(val == null ? "" : val.toString());
            return;
        }

        if (col < 3 || col >= assignments.size() + 3) return;

        Assignment a = assignments.get(col - 3);
        try {
            double score = Double.parseDouble(val.toString().trim());
            s.setScore(a.getName(), score);
            grader.calculateFinalPercentsForCourse(course);
            grader.assignLetterGradesForCourse(course);
            tableModel.setValueAt(String.format("%.1f%%", s.getFinalPercent()), row, columns.length - 2);
            tableModel.setValueAt(s.getLetterGrade(), row, columns.length - 1);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(Portal.this, "Score must be a number.");
            tableModel.setValueAt(s.getScore(a.getName()), row, col);
        }
    });

    // sort button above table, right aligned
    JButton sortBtn = new JButton("Sort");
    sortBtn.addActionListener(ev -> {
        String[] options = {"By Name", "By Final Grade"};
        String choice = (String) JOptionPane.showInputDialog(this,
                "Sort students by:", "Sort", JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);
        if (choice == null) return;
        if (choice.equals("By Name")) {
            students.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        } else {
            students.sort((a, b) -> Double.compare(b.getFinalPercent(), a.getFinalPercent()));
        }
        showCourseView(course);
    });

    JPanel tableTopBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    tableTopBar.add(sortBtn);

    JPanel tableWrapper = new JPanel(new BorderLayout());
    tableWrapper.add(tableTopBar, BorderLayout.NORTH);
    tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER);

    courseDisplay.add(tableWrapper, BorderLayout.CENTER);

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

        int result = JOptionPane.showConfirmDialog(this, panel, "Adjust Assignment Weights", JOptionPane.OK_CANCEL_OPTION);

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
                double[] newMins = new double[ranges.size()];
                double[] newMaxs = new double[ranges.size()];
                for (int i = 0; i < ranges.size(); i++) {
                    newMins[i] = Double.parseDouble(minFields[i].getText().trim());
                    newMaxs[i] = Double.parseDouble(maxFields[i].getText().trim());
                }
                if (!course.getGradeScale().applyAllRangeUpdates(newMins, newMaxs)) {
                    JOptionPane.showMessageDialog(this,
                            "Could not apply boundaries (ranges must stay ordered without overlap).");
                } else {
                    grader.assignLetterGradesForCourse(course);
                    showCourseView(course);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "All values must be numbers.");
            }
        }
    }

    // import weights from another course or csv file, then recalculate grades
    private void importWeightsFromCourse(Course targetCourse) {
    JRadioButton fromCourse = new JRadioButton("Existing Course");
    JRadioButton fromCsv = new JRadioButton("CSV File");
    fromCourse.setSelected(true);

    ButtonGroup group = new ButtonGroup();
    group.add(fromCourse);
    group.add(fromCsv);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(new JLabel("Import weights from:"));
    panel.add(fromCourse);
    panel.add(fromCsv);

    int res = JOptionPane.showConfirmDialog(this, panel, "Import Weights", JOptionPane.OK_CANCEL_OPTION);
    if (res != JOptionPane.OK_OPTION) return;

    if (fromCourse.isSelected()) {
        // import from existing loaded course
        List<Course> allCourses = courseManager.getCourses();
        List<Course> others = new ArrayList<>();
        for (Course c : allCourses) {
            if (!c.getCourseId().equals(targetCourse.getCourseId())) {
                others.add(c);
            }
        }

        if (others.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No other courses available.");
            return;
        }

        String[] choices = others.stream()
                .map(c -> c.getCourseId() + " - " + c.getCourseName())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this,
                "Select course to import weights from:",
                "Import Weights", JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

        if (selected == null) return;

        Course source = others.get(java.util.Arrays.asList(choices).indexOf(selected));
        targetCourse.copyAssignmentSetupFrom(source);

    } 
    else {
        FileHandler f = new FileHandler();
    File file = f.importFile(this);
    if (file == null) return;

    // load courses from file without adding them to courseManager
    Map<String, Course> loaded = f.loadData(file.getAbsolutePath());
    if (loaded == null || loaded.isEmpty()) {
        JOptionPane.showMessageDialog(this, "No courses found in file.");
        return;
    }

    String[] choices = loaded.values().stream()
            .map(c -> c.getCourseId() + " - " + c.getCourseName())
            .toArray(String[]::new);

    String selected = (String) JOptionPane.showInputDialog(this,
            "Select course to import weights from:",
            "Import Weights", JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

    if (selected == null) return;

    Course source = loaded.values().stream()
            .filter(c -> (c.getCourseId() + " - " + c.getCourseName()).equals(selected))
            .findFirst().orElse(null);

    if (source == null) return;
    targetCourse.copyAssignmentSetupFrom(source);
    }

    grader.calculateFinalPercentsForCourse(targetCourse);
    grader.assignLetterGradesForCourse(targetCourse);
    showCourseView(targetCourse);
    JOptionPane.showMessageDialog(this, "Weights imported successfully.");
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

    // html summary for the top stats strip (letter counts shown in bar chart)
    private String buildCourseStatsHtml(Stats st, Course course) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-size:11px;text-align:center'>");

        int totalEnrolled = course.getStudents() != null ? course.getStudents().size() : 0;
        int inactive = totalEnrolled - st.activeCount;
        sb.append("<b>Course Stats (Active Only)</b><br/><br/>");
        sb.append("Enrolled: ").append(totalEnrolled).append(" · Active: ").append(st.activeCount)
                .append(" · Inactive: ").append(inactive).append("<br/><br/>");

        if (st.activeCount == 0) {
            sb.append("No active students.</body></html>");
            return sb.toString();
        }

        sb.append(String.format(
                "Final %% Statistics: Mean %.2f · Median %.2f · St. Dev. %.2f · Min %.2f · Max %.2f<br/><br/>",
                st.avgFinalPercent, st.medianFinalPercent, st.stdFinalPercent, st.minFinalPercent,
                st.maxFinalPercent));

        int passing = 0;
        for (Student s : course.getActiveStudents()) {
            String g = s.getLetterGrade();
            if (g != null && !"F".equals(g) && !"D".equals(g)) {
                passing++;
            }
        }
        sb.append(String.format("Passing: %d / %d (%.1f%%)",
                passing, st.activeCount, 100.0 * passing / st.activeCount));

        sb.append("</body></html>");
        return sb.toString();
    }

    private String buildGradeDistLabel(Course course) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        List<GradeRange> ranges = course.getGradeScale().getRanges();
        for (GradeRange r : ranges) {
            sb.append(String.format("%s \u2265 %.0f , ", r.getLetter(), r.getMin()));
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 3); 
        }
        sb.append("\n");
        return sb.toString();
    }

    // pick a student and edit their note text
    private void editStudentNoteDialog(Course course) {
        List<Student> students = course.getStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No students in this course.");
            return;
        }

        String[] choices = new String[students.size()];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            choices[i] = s.getName() + " (" + s.getStudentId() + ")";
        }

        String picked = (String) JOptionPane.showInputDialog(this, "Choose student:", "Add or Edit Student Note",
                JOptionPane.PLAIN_MESSAGE, null, choices, choices[0]);

        if (picked == null) {
            return;
        }

        Student chosen = null;
        for (int i = 0; i < students.size(); i++) {
            if (choices[i].equals(picked)) {
                chosen = students.get(i);
                break;
            }
        }
        if (chosen == null) {
            return;
        }

        JTextArea area = new JTextArea(chosen.getNote() != null ? chosen.getNote() : "", 6, 36);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(area);
        int result = JOptionPane.showConfirmDialog(this, scroll,
                "Note for " + chosen.getName(), JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            chosen.setNote(area.getText().trim());
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
            JOptionPane.showMessageDialog(this, "Please enter Course Name and ID.");
            return;
        }

        courseManager.createBlankCourse(courseName, courseId);

        Course newCourse = courseManager.getCourseById(courseId);
        if (newCourse != null) {
            int res = JOptionPane.showConfirmDialog(this,
                    "Would you like to import weights from an existing course?",
                    "Import Weights", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                importWeightsFromCourse(newCourse);
            }
        }

        courseNameField.setText("");
        courseIdField.setText("");
        addCoursePanel.setVisible(false);

        refreshCourseList();
        revalidate();
        repaint();
    }

    private static class FullWidthCenterHeaderBar extends JPanel {
        private JPanel westCluster;
        private JComponent centerTitle;
        private JPanel eastCluster;

        FullWidthCenterHeaderBar() {
            setLayout(null);
        }

        void wirePanels(JPanel west, JComponent center, JPanel east) {
            this.westCluster = west;
            this.centerTitle = center;
            this.eastCluster = east;
            removeAll();
            if (center != null) {
                add(center);
            }
            if (west != null) {
                add(west);
            }
            if (east != null) {
                add(east);
            }
        }

        public void doLayout() {
            synchronized (getTreeLock()) {
                Insets in = getInsets();
                int W = getWidth() - in.left - in.right;
                int H = getHeight() - in.top - in.bottom;
                int top = in.top;
                int left = in.left;
                if (westCluster != null) {
                    Dimension pw = westCluster.getPreferredSize();
                    westCluster.setBounds(left, top + (H - pw.height) / 2, pw.width, pw.height);
                }
                if (eastCluster != null) {
                    Dimension pe = eastCluster.getPreferredSize();
                    eastCluster.setBounds(left + W - pe.width, top + (H - pe.height) / 2, pe.width, pe.height);
                }
                if (centerTitle != null) {
                    if (centerTitle.isVisible()) {
                        Dimension pt = centerTitle.getPreferredSize();
                        int tx = left + (W - pt.width) / 2;
                        int ty = top + (H - pt.height) / 2;
                        centerTitle.setBounds(tx, ty, pt.width, pt.height);
                    } else {
                        centerTitle.setBounds(0, 0, 0, 0);
                    }
                }
            }
        }

        public Dimension getPreferredSize() {
            Insets in = getInsets();
            int mh = 28;
            if (westCluster != null) {
                mh = Math.max(mh, westCluster.getPreferredSize().height);
            }
            if (eastCluster != null) {
                mh = Math.max(mh, eastCluster.getPreferredSize().height);
            }
            if (centerTitle != null && centerTitle.isVisible()) {
                mh = Math.max(mh, centerTitle.getPreferredSize().height);
            }
            return new Dimension(600, mh + in.top + in.bottom);
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }
    }
}
