package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import gui.FileHandler;

public class CourseManager {
    
    HashMap<String, Course> courses;
    
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
    
}
