/*
 * Gradebook class to manage multiple courses and provide methods for course creation and retrieval.
 * It serves as the main entry point for managing the courses in the system.
 */
package core;

import java.util.ArrayList;

public class Gradebook {
    protected ArrayList<Course> courses;
    protected Factory courseFactory;

    // constructor
    public Gradebook() {
        this.courses = new ArrayList<Course>();
        this.courseFactory = new CourseFactory();
    }

    // get all courses
    public ArrayList<Course> getAllCourses() {
        return courses;
    }

    // create a blank course
    public Course createBlankCourse(String name, String id) {
        Course course = courseFactory.createBlankCourse(name, id);
        courses.add(course);
        return course;
    }

    // create a course by porting from another course
    public Course createPortedCourse(String name, String id, Course sourceCourse) {
        Course course = courseFactory.createPortedCourse(name, id, sourceCourse);
        courses.add(course);
        return course;
    }

    // add a course to the gradebook
    public void addCourse(Course course) {
        if (course == null) {
            return;
        }
        courses.add(course);
    }

    // remove a course from the gradebook
    public boolean removeCourse(String id) {
        if (id == null) {
            return false;
        }

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            if (id.equals(course.getCourseId())) {
                courses.remove(i);
                return true;
            }
        }
        return false;
    }

    // get a course by id
    public Course getCourseById(String id) {
        if (id == null) {
            return null;
        }

        for (Course course : courses) {
            if (id.equals(course.getCourseId())) {
                return course;
            }
        }
        return null;
    }
}
