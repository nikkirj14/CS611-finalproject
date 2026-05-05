/*
* LetterGradeBarChartPanel bar chart for letter grade counts in the stats by assignment panel. It takes a map of letter to count and an ordered list of letters to display, and draws a bar chart with the counts for each letter grade.
*/

package gui;

import core.GradeRange;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

public class LetterGradeBarChartPanel extends JPanel {

    private HashMap<String, Integer> counts;
    private ArrayList<String> letterOrder;
    private Color barColor;

    public LetterGradeBarChartPanel(HashMap<String, Integer> counts, ArrayList<GradeRange> ranges, Color barColor) {
        this.counts = counts != null ? counts : new HashMap<String, Integer>();
        this.letterOrder = new ArrayList<String>();
        this.barColor = barColor;
        if (ranges != null) {
            for (int i = 0; i < ranges.size(); i++) {
                letterOrder.add(ranges.get(i).getLetter());
            }
        }
        setOpaque(true);
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(520, 152));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int maxCount = 0;
        for (int i = 0; i < letterOrder.size(); i++) {
            Integer c = counts.get(letterOrder.get(i));
            int n = c != null ? c.intValue() : 0;
            if (n > maxCount) {
                maxCount = n;
            }
        }
        if (maxCount < 1) {
            maxCount = 1;
        }

        int w = getWidth();
        int h = getHeight();
        int padL = 16;
        int padR = 16;
        FontMetrics fm = g2.getFontMetrics();
        int padTop = fm.getHeight() + fm.getAscent() + 10;
        int labelH = 18;
        int chartBottom = h - labelH - 6;
        int chartTop = padTop;
        int chartH = Math.max(1, chartBottom - chartTop);

        int n = letterOrder.size();
        if (n == 0) {
            return;
        }

        int innerW = w - padL - padR;
        int slotW = innerW / n;
        int barW = Math.max(10, slotW - 6);

        g2.setStroke(new BasicStroke(1f));
        g2.setColor(barColor);

        for (int i = 0; i < n; i++) {
            String letter = letterOrder.get(i);
            Integer cc = counts.get(letter);
            int count = cc != null ? cc.intValue() : 0;

            int barH = (int) Math.round((double) count / (double) maxCount * chartH);
            int x = padL + i * slotW + (slotW - barW) / 2;
            int y = chartBottom - barH;

            g2.setColor(barColor);
            g2.fillRect(x, y, barW, barH);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, barW, barH);

            if (count > 0) {
                String num = String.valueOf(count);
                int nw = fm.stringWidth(num);
                int nx = x + (barW - nw) / 2;
                int ny = y - fm.getDescent() - 2;
                g2.drawString(num, nx, ny);
            }

            int lw = g2.getFontMetrics().stringWidth(letter);
            g2.drawString(letter, x + (barW - lw) / 2, chartBottom + 14);
        }
    }
}
