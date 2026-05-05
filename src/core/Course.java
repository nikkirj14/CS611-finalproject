// course class for assignments, students, and grade scale

package core;

import java.util.ArrayList;

public class Course {
    protected String courseId;
    protected String courseName;
    protected ArrayList<Assignment> assignments;
    protected ArrayList<Student> students;
    protected GradeScale gradeScale;

    // constructor
    public Course(String name, String id) {
        this.courseName = name;
        this.courseId = id;
        this.gradeScale = new GradeScale();
        this.assignments = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public Course(String name, String id, ArrayList<Assignment> assignments) {
        this.courseName = name;
        this.courseId = id;
        this.assignments = assignments;
        this.gradeScale = new GradeScale();
        this.students = new ArrayList<>();
    }
    
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
        
        for (Assignment a : other.assignments) {
            Assignment copy = new Assignment(a.name, a.weight, a.maxPoints, a.note);
            addAssignment(copy);
        }
        
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
