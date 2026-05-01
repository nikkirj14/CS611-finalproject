package core;


import java.util.ArrayList;

public class GradeRange {
    protected String letter;
    protected double minPercent;
    protected double maxPercent;

    public GradeRange(String letter, double min, double max) {
        this.letter = letter;
        this.minPercent = min;
        this.maxPercent = max;
    }

    public void setRange(double min, double max) {
        this.minPercent = min;
        this.maxPercent = max;
    }

    public double getMin() {
        return minPercent;
    }

    public double getMax() {
        return maxPercent;
    }
}
