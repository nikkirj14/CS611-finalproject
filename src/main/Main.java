package main;
/* 
 * Main is the entry point of the application.
 */

import gui.FileHandler;
import gui.Portal;
import java.io.File;

import javax.swing.UIManager;

import core.CourseManager;

import java.awt.Font;

public class Main {
    public static void main(String[] args) {

        CourseManager courseManager = new CourseManager();
        if (args != null && args.length > 0) {
            String filePath = args[0];
            System.out.println("Attempting to load data from: " + filePath);
            if (!courseManager.populatePriorCourses(filePath)) {
                System.out.println("Failed to load data from: " + filePath);
                return;
            }
        }
        new Portal(courseManager).setVisible(true);
    }    
}