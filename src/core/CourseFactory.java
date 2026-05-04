// factory for creating courses

package core;

import java.util.ArrayList;

public class CourseFactory implements Factory {

    // create a blank course
    public Course createBlankCourse(String name, String id, String term) {
        return new Course(name, id, term);
    }

    // create a new course by copying setup from another
    public Course createPortedCourse(String name, String id, String term, Course sourceCourse) {
        Course newCourse = new Course(name, id, term);
        if (sourceCourse == null) {
            return newCourse;
        }

        // copy assignment setup only, not students
        ArrayList<Assignment> copiedAssignments = copyAssignments(sourceCourse.assignments);
        for (Assignment assignment : copiedAssignments) {
            newCourse.addAssignment(assignment);
        }

        return newCourse;
    }

    // copy assignment list into new assignment objects
    private ArrayList<Assignment> copyAssignments(ArrayList<Assignment> sourceAssignments) {
        ArrayList<Assignment> copied = new ArrayList<Assignment>();
        if (sourceAssignments == null) {
            return copied;
        }

        for (Assignment original : sourceAssignments) {
            Assignment clone = new Assignment(original.name, (int) original.maxPoints);
            clone.setWeight(original.weight);
            clone.note = original.note;
            copied.add(clone);
        }
        return copied;
    }
}
