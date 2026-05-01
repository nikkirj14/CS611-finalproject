package core;

import java.util.ArrayList;

public class CourseFactory implements Factory {

    public Course createBlankCourse(String name, String id) {
        return new Course(name, id);
    }

    public Course createPortedCourse(String name, String id, Course sourceCourse) {
        Course newCourse = new Course(name, id);
        if (sourceCourse == null) {
            return newCourse;
        }

        // copy assignment setup only, not students
        ArrayList<Assignment> copiedAssignments = copyAssignments(sourceCourse.assignments);
        for (Assignment assignment : copiedAssignments) {
            newCourse.addAssignment(assignment);
        }

        // copy grade scale ranges
        newCourse.setGradeScale(copyGradeScale(sourceCourse.getGradeScale()));

        return newCourse;
    }

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

    private GradeScale copyGradeScale(GradeScale sourceScale) {
        GradeScale copiedScale = new GradeScale();
        copiedScale.ranges = new ArrayList<GradeRange>();

        if (sourceScale == null || sourceScale.getRanges() == null) {
            return copiedScale;
        }

        for (GradeRange range : sourceScale.getRanges()) {
            copiedScale.ranges.add(new GradeRange(range.getLetter(), range.getMin(), range.getMax()));
        }
        return copiedScale;
    }
}
