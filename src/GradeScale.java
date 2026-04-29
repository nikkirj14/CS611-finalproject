

import java.util.ArrayList;
import java.util.Arrays;

public class GradeScale {
    protected ArrayList<GradeRange> ranges;


    public GradeScale() {
        loadDefaultScale();
    }

    public void loadDefaultScale() {
        this.ranges = new ArrayList<GradeRange>(Arrays.asList(
            new GradeRange("A+", 97, 100),
            new GradeRange("A", 93, 97),
            new GradeRange("A-", 90, 93),
            new GradeRange("B+", 87, 90),
            new GradeRange("B", 83, 87),
            new GradeRange("B-", 80, 83),
            new GradeRange("C+", 77, 80),
            new GradeRange("C", 73, 77),
            new GradeRange("C-", 70, 73),
            new GradeRange("D+", 67, 70),
            new GradeRange("D", 63, 67),
            new GradeRange("D-", 60, 63),
            new GradeRange("F", 0, 60)
        ));
    }
    
    public void shiftToTopScore(double topScore) {
        double dif = 100 - topScore;
        for (GradeRange r : ranges) {
            r.setRange(r.getMin() - dif, r.getMax() - dif);
        }
    }

    public void resetToDefault() {
        loadDefaultScale();
    }

    public String getLetter(double percent) {
        for (GradeRange r : ranges) {
            if ((r.getMax() > percent) && (r.getMin() <= percent)) {
                return r.letter;
            }
        }
        return null;
    }

}
