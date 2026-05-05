/*
 * Factory interface defines methods for creating courses, allowing for different implementations of course creation logic.
 * This abstraction enables flexibility in how courses are instantiated, such as creating blank courses or porting
 */
package core;

public interface Factory {
    Course createBlankCourse(String name, String id);

    Course createPortedCourse(String name, String id, Course sourceCourse);
}
