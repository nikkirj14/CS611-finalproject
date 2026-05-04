// grade scale class for letter grade boundaries

package core;

import java.util.ArrayList;
import java.util.Arrays;

public class GradeScale {
    public static final String LETTER_A_PLUS = "A+";
    public static final String LETTER_A = "A";
    public static final String LETTER_A_MINUS = "A-";
    public static final String LETTER_B_PLUS = "B+";
    public static final String LETTER_B = "B";
    public static final String LETTER_B_MINUS = "B-";
    public static final String LETTER_C_PLUS = "C+";
    public static final String LETTER_C = "C";
    public static final String LETTER_C_MINUS = "C-";
    public static final String LETTER_D = "D";
    public static final String LETTER_F = "F";

    protected ArrayList<GradeRange> ranges;
    protected ArrayList<GradeScaleObserver> observers;

    // constructor
    public GradeScale() {
        this.observers = new ArrayList<GradeScaleObserver>();
        loadDefaultScale();
    }

    // add a listener for boundary changes
    public void addObserver(GradeScaleObserver observer) {
        if (observer == null) {
            return;
        }
        observers.add(observer);
    }

    // remove a listener
    public void removeObserver(GradeScaleObserver observer) {
        observers.remove(observer);
    }

    // tell all listeners the scale changed
    private void notifyObservers() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).gradeScaleChanged();
        }
    }

    // load default letter ranges
    public void loadDefaultScale() {
        this.ranges = new ArrayList<GradeRange>(Arrays.asList(
                new GradeRange(LETTER_A_PLUS, 97, 100),
                new GradeRange(LETTER_A, 93, 97),
                new GradeRange(LETTER_A_MINUS, 90, 93),
                new GradeRange(LETTER_B_PLUS, 87, 90),
                new GradeRange(LETTER_B, 83, 87),
                new GradeRange(LETTER_B_MINUS, 80, 83),
                new GradeRange(LETTER_C_PLUS, 77, 80),
                new GradeRange(LETTER_C, 73, 77),
                new GradeRange(LETTER_C_MINUS, 70, 73),
                new GradeRange(LETTER_D, 60, 70),
                new GradeRange(LETTER_F, 0, 60)));
    }

    // shift ranges by top score; keep highest letter max at 100 for later higher scores
    public void shiftToTopScore(double topScore) {
        double dif = 100 - topScore;
        for (int i = 0; i < ranges.size(); i++) {
            GradeRange r = ranges.get(i);
            double oldMin = r.getMin();
            double oldMax = r.getMax();
            if (i == 0) {
                r.setRange(oldMin - dif, 100);
            } else {
                r.setRange(oldMin - dif, oldMax - dif);
            }
        }
        notifyObservers();
    }

    // reset ranges to defaults
    public void resetToDefault() {
        loadDefaultScale();
        notifyObservers();
    }

    // get letter grade based on a percent
    public String getLetter(double percent) {
        if (ranges == null || ranges.isEmpty()) {
            return null;
        }
        if (percent > 100) {
            return ranges.get(0).getLetter();
        }
        if (percent < 0) {
            return ranges.get(ranges.size() - 1).getLetter();
        }
        // inclusive min/max so values exactly on a boundary still match (needed after curve shift)
        for (GradeRange r : ranges) {
            if (percent >= r.getMin() && percent <= r.getMax()) {
                return r.getLetter();
            }
        }
        return null;
    }

    // update one letter range and validate the full scale
    public boolean updateRange(String letter, double newMin, double newMax) {
        GradeRange range = getRangeByLetter(letter);
        if (range == null) {
            return false;
        }
        if (newMin > newMax) {
            return false;
        }

        double oldMin = range.getMin();
        double oldMax = range.getMax();
        range.setRange(newMin, newMax);

        if (!isValidScale()) {
            range.setRange(oldMin, oldMax);
            return false;
        }
        notifyObservers();
        return true;
    }

    // check if ranges stay ordered and not overlapping
    public boolean isValidScale() {
        if (ranges == null || ranges.isEmpty()) {
            return false;
        }

        for (int i = 0; i < ranges.size(); i++) {
            GradeRange current = ranges.get(i);
            if (current.getMin() > current.getMax()) {
                return false;
            }

            if (i > 0) {
                GradeRange previous = ranges.get(i - 1);
                if (previous.getMin() < current.getMin()) {
                    return false;
                }
                if (previous.getMax() < current.getMax()) {
                    return false;
                }
                if (previous.getMin() < current.getMax()) {
                    return false;
                }
            }
        }
        return true;
    }

    // find a range by letter
    public GradeRange getRangeByLetter(String letter) {
        if (letter == null || ranges == null) {
            return null;
        }

        for (GradeRange range : ranges) {
            if (letter.equals(range.getLetter())) {
                return range;
            }
        }
        return null;
    }

    public ArrayList<GradeRange> getRanges() {
        return ranges;
    }

}
