/* 
 * StatsByStudentChartPanel bar chart for displaying a student's scores on all assignments in a course
*/
package gui;

import core.Assignment;
import core.GradeRange;
import core.Student;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

public class StatsByStudentChartPanel extends JPanel {

    private Student student;
    private List<Assignment> assignments;
    private Color[] palette = new Color[] {
    new Color(102, 122, 138), 
    new Color(149, 125, 173),
    new Color(120, 160, 140),
    new Color(140, 150, 160), 
    new Color(170, 140, 160) 
};

    public StatsByStudentChartPanel(Student student, List<Assignment> assignments)  {
        this.student = student;
        this.assignments = assignments;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(520, 180));
    }

    protected void paintComponent(Graphics g) {
                super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int padL = 20;
        int padR = 20;
        int padTop = 30;
        int padBottom = 70;

        int chartW = w - padL - padR;
        int chartH = h - padTop - padBottom;

        int n = assignments.size();
        if (n == 0) return;

        // find max score dynamically
        double maxScore = 0;
        for (Assignment a : assignments) {
            double s = student.getScore(a.getName());
            if (s > maxScore) maxScore = s;
        }
        if (maxScore < 1) maxScore = 100;

        int slotW = chartW / n;
        int barW = Math.max(10, slotW - 6);

        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < n; i++) {
            Assignment a = assignments.get(i);

            double score = student.getScore(a.getName());
            int barH = (int) ((score / maxScore) * chartH);

            int x = padL + i * slotW + (slotW - barW) / 2;
            int y = padTop + (chartH - barH);

            Color barColor = palette[i % palette.length];
            g2.setColor(barColor);
            g2.fillRect(x, y, barW, barH);

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, barW, barH);

            // score label above bar
            String scoreStr = String.format("%.0f", score);
            int sw = fm.stringWidth(scoreStr);
            g2.drawString(scoreStr, x + (barW - sw) / 2, y - 4);

            // assignment label
            String label = a.getName();

            int lw = fm.stringWidth(label);
            g2.drawString(label, x + (barW - lw) / 2, padTop + chartH + 15);
        }
    }
}
