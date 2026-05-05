// interface for building courses

package core;

public interface Factory {
    Course createBlankCourse(String name, String id);

    Course createPortedCourse(String name, String id, Course sourceCourse);
}
