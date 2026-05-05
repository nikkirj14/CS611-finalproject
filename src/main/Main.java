package main;
/* 
 * Main is the entry point of the application and initializes the course manager and portal GUI. It also handles loading prior courses from a file if a file path is provided as a command line argument. The main method sets the look and feel of the GUI, creates a CourseManager instance, populates it with prior courses if a file path is given, and then launches the Portal GUI with the course manager.
 */

import gui.FileHandler;
import gui.Portal;

import javax.swing.UIManager;

import core.CourseManager;
public class Main {
    
    public static void main(String[] args) {
    try {
        
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

    } catch (Exception e) {
        e.printStackTrace();
    }

    CourseManager courseManager = new CourseManager();
    if (args != null && args.length > 0) {
        String filePath = args[0];
        if (!courseManager.populatePriorCourses(filePath)) {
            System.out.println("Failed to load data from: " + filePath);
            return;
        }
    }
    new Portal(courseManager).setVisible(true);
}
    
}