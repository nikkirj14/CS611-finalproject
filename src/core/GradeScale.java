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

    // constructor
    public GradeScale() {
        loadDefaultScale();
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

    // shift all ranges so top score maps to 100
    public void shiftToTopScore(double topScore) {
        double dif = 100 - topScore;
        for (GradeRange r : ranges) {
            r.setRange(r.getMin() - dif, r.getMax() - dif);
        }
    }

    // reset ranges to defaults
    public void resetToDefault() {
        loadDefaultScale();
    }

    // get letter grade based on a percent
    public String getLetter(double percent) {
        if (ranges == null || ranges.isEmpty()) {
            return null;
        }
        // if score is at or above the top boundary, return top letter
        if (percent == 100 || percent > ranges.get(0).getMax()) {
            return ranges.get(0).getLetter();
        }
        for (GradeRange r : ranges) {
            if ((r.getMax() > percent) && (r.getMin() <= percent)) {
                return r.getLetter();
            }
        }
        return null;
    }

    public ArrayList<GradeRange> getRanges() {
        return ranges;
    }

}
