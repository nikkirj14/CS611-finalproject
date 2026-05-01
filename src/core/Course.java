// course class for assignments, students, and grade scale

package core;

import java.util.ArrayList;

public class Course {
    protected String courseName;
    protected String courseId;
    protected ArrayList<Assignment> assignments;
    protected ArrayList<Student> students;
    protected GradeScale gradeScale;

    // constructor
    public Course(String name, String id) {
        this.courseName = name;
        this.courseId = id;
        this.assignments = new ArrayList<Assignment>();
        this.students = new ArrayList<Student>();
        this.gradeScale = new GradeScale();
    }

    // add one assignment
    public void addAssignment(Assignment a) {
        if (a == null) {
            return;
        }
        assignments.add(a);
    }

    // add one student
    public void addStudent(Student s) {
        if (s == null) {
            return;
        }
        students.add(s);
    }

    // copy assignment setup from another course
    public void copyAssignmentSetupFrom(Course other) {
        if (other == null) {
            return;
        }
        this.assignments = other.assignments;
    }

    // get only active students
    public ArrayList<Student> getActiveStudents() {
        ArrayList<Student> active = new ArrayList<Student>();
        if (students == null) {
            return active;
        }
        for (Student s : students) {
            if (s.isActive()) {
                active.add(s);
            }
        }
        return active;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public ArrayList<Assignment> getAssignments() {
        return assignments;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public GradeScale getGradeScale() {
        return gradeScale;
    }

    // set a grade scale
    public void setGradeScale(GradeScale gradeScale) {
        if (gradeScale != null) {
            this.gradeScale = gradeScale;
        }
    }
}
