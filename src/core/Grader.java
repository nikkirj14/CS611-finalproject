// grader class for final grade math

package core;

public class Grader {

    // calculate one student's final percent
    public double calculateFinalPercent(Student student, Course course) {
        if (student == null || course == null || course.assignments == null || course.assignments.isEmpty()) {
            return -1.0;
        }

        double totalWeight = getTotalWeight(course);
        if (totalWeight <= 0) {
            return -1.0;
        }

        double weightedPercent = 0.0;
        for (Assignment assignment : course.assignments) {
            if (assignment == null || assignment.maxPoints <= 0 || assignment.weight <= 0) {
                continue;
            }

            double assignmentPercent = (student.getScore(assignment.name) / assignment.maxPoints) * 100.0;
            double normalizedWeight = assignment.weight / totalWeight;
            weightedPercent += assignmentPercent * normalizedWeight;
        }

        student.setFinalPercent(weightedPercent);
        return weightedPercent;
    }

    // calculate final percents for all students in a course
    public int calculateFinalPercentsForCourse(Course course) {
        if (course == null || course.students == null || course.students.isEmpty()) {
            return 0;
        }

        int updatedCount = 0;
        for (Student student : course.students) {
            double result = calculateFinalPercent(student, course);
            if (result >= 0) {
                updatedCount++;
            }
        }
        return updatedCount;
    }

    // add up assignment weights for a course
    public double getTotalWeight(Course course) {
        if (course == null || course.assignments == null || course.assignments.isEmpty()) {
            return 0.0;
        }

        double totalWeight = 0.0;
        for (Assignment assignment : course.assignments) {
            if (assignment == null || assignment.weight <= 0) {
                continue;
            }
            totalWeight += assignment.weight;
        }
        return totalWeight;
    }
}
