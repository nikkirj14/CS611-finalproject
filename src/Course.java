

import java.util.ArrayList;

public class Course {
    protected String courseName;
    protected String courseId;
    protected ArrayList<Assignment> assignments;
    protected ArrayList<Student> students;
    protected GradeScale gradeScale;

    public Course(String name, String id) {
        this.courseName = name;
        this.courseId = id;
        this.gradeScale = new GradeScale();
    }

    public void addAssignment(Assignment a) {
        assignments.add(a);
    }
    
    public void addStudent(Student s) {
        students.add(s);
    }

    public void copyAssignmentSetupFrom(Course other) {
        this.assignments = other.assignments;
    }

    public ArrayList<Student> getActiveStudents() {
        ArrayList<Student> active = new ArrayList<>();
        for (Student s : students) {
            if (s.active) {
                active.add(s);
            }
        }
        return active;
    }

}
