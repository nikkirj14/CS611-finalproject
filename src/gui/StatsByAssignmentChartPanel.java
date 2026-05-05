package gui;


import core.GradeRange;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.FontMetrics;


public class StatsByAssignmentChartPanel extends JPanel{
    private LetterGradeBarChartPanel chartPanel;
    private  List<Double> scores;
    private List<GradeRange> ranges;

    public StatsByAssignmentChartPanel( List<Double> scores, List<GradeRange> ranges) {

        this.scores = scores;
        this.ranges = ranges;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        HashMap<String, Integer> counts = buildDistribution(scores, ranges);

        Color barColor = new Color(84,146,125);

        LetterGradeBarChartPanel chartPanel =    
            new LetterGradeBarChartPanel(counts, new ArrayList<>(ranges), barColor);


        add(chartPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(520, 180));
    }

    private HashMap<String, Integer> buildDistribution(List<Double> scores, List<GradeRange> ranges) {
        HashMap<String, Integer> counts = new HashMap<>();

        // initialize all letters to 0
        for (GradeRange r : ranges) {
            counts.put(r.getLetter(), 0);
        }

        for (double score : scores) {
            String letter = toLetter(score, ranges);
            counts.put(letter, counts.get(letter) + 1);
        }

        return counts;
    }

    private String toLetter(double score, List<GradeRange> ranges) {
        for (GradeRange r : ranges) {
            if ( score >= r.getMin() && score < r.getMax()) { 
                return r.getLetter();
            }
        }
        return "F"; 
    }
}