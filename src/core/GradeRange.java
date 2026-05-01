// grade range class for one letter bucket

package core;


public class GradeRange {
    private String letter;
    private double minPercent;
    private double maxPercent;

    // constructor
    public GradeRange(String letter, double min, double max) {
        this.letter = letter;
        this.minPercent = min;
        this.maxPercent = max;
    }

    // update min and max for this range
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

    public String getLetter() {
        return letter;
    }
}
