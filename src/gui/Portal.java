// swing portal window for basic actions

package gui;

import gui.FileHandler;
import javax.swing.*;

import core.Student;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class Portal extends JFrame implements ActionListener {
    private JButton importButton;

    // constructor
    public Portal() {
        // create window
        super("Grading Portal");
        setSize(12000, 8000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close app when you click X 

        // create button
        importButton = new JButton("Import Grades");
        importButton.addActionListener(this);
        importButton.setBackground(Color.BLACK);
        importButton.setHorizontalAlignment(SwingConstants.LEFT);
        importButton.setVerticalAlignment(SwingConstants.TOP);
        importButton.setBounds(100, 50, 150, 50);
        importButton.setBorder(BorderFactory.createEtchedBorder());
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(importButton);
    }

    // handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("Import Grades")) {
            FileHandler f = new FileHandler();
            File file = f.importFile(this);
            List<Student> students = f.parseScores(file);
            System.out.println("Parsed " + students.size() + " students from the file.");
        }
        
    }
}
