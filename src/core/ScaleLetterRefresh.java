/*
 * ScaleLetterRefresh class for refreshing letter grades when the grade scale changes.
 */

package core;

public class ScaleLetterRefresh implements GradeScaleObserver {

    protected Course course;
    protected Grader grader;

    public ScaleLetterRefresh(Course course, Grader grader) {
        this.course = course;
        this.grader = grader;
    }

    // called whenever ranges shift or reset
    public void gradeScaleChanged() {
        if (course == null || grader == null) {
            return;
        }
        grader.assignLetterGradesForCourse(course);
    }

    // attach one course to one grader
    // call once per course so listeners dont stack
    public static void attach(Course course, Grader grader) {
        if (course == null || grader == null) {
            return;
        }
        if (course.getGradeScale() == null) {
            return;
        }

        ScaleLetterRefresh listener = new ScaleLetterRefresh(course, grader);
        course.getGradeScale().addObserver(listener);
    }
}
