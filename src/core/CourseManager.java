package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import gui.FileHandler;

public class CourseManager {
    
    HashMap<String, Course> courses;

    public CourseManager() {
        this.courses = new HashMap<>();
    }
    
    public boolean populatePriorCourses(String filePath) {
        FileHandler f = new FileHandler();
        if (!f.validateFile(new java.io.File(filePath))) {
            System.out.println("Invalid file: " + filePath);
            return false;
        }
        System.out.println("Valid file: " + filePath);
        
        HashMap<String, Course> priorCourses = new HashMap<>(f.loadData(filePath));
        if (priorCourses == null || priorCourses.isEmpty()) {
            System.out.println("No courses loaded from: " + filePath);
        } else {
            System.out.println("Loaded " + priorCourses.size() + " courses from: " + filePath);
        }
        this.courses = priorCourses;
        System.out.println("CourseManager initialized with " + courses.size() + " courses.");
        return true;
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses.values());
    }

    public void addCourse(Course course) {
        if (course == null || course.getCourseId() == null) {
            System.out.println("Invalid course or course ID.");
            return;
        }
        courses.put(course.getCourseId(), course);
    }

    public void removeCourse(String courseId) {
        if (courseId == null) {
            System.out.println("Course ID cannot be null.");
            return;
        }
        courses.remove(courseId);
    }

    public Course getCourseById(String courseId) {
        if (courseId == null) {
            System.out.println("Course ID cannot be null.");
            return null;
        }
        return courses.get(courseId);
    }

    public void createBlankCourse(String name, String id) {
        if (name == null || id == null) {
            System.out.println("Course name and ID cannot be null.");
            return;
        }
        Course course = new Course(name, id);
        addCourse(course);
    }


    
}
