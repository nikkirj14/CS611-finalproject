/*
 * Factory interface defines methods for creating courses, allowing for different implementations of course creation logic.
 * This abstraction enables flexibility in how courses are instantiated, such as creating blank courses or porting
 */
package core;

public interface Factory {
    // creates a blank course
    Course createBlankCourse(String name, String id);

    // creates a new course by copying setup from another course
    Course createPortedCourse(String name, String id, Course sourceCourse);
}
