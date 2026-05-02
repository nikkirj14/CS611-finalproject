package core;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Stats {

    public int activeCount;
    public double avgFinalPercent;
    public double medianFinalPercent;
    public double minFinalPercent;
    public double maxFinalPercent;
    public HashMap<String, Integer> letterCounts;

    // constructor
    public Stats() {
        letterCounts = new HashMap<String, Integer>();
    }

    public void statsByStudent(Student student) {

    }

    public Map<String, Map<String, Double>> statsByAssignment(Course course) {
        Map<String, Map<String, Double>> result = new HashMap<>();

        for (Assignment a : course.assignments) {
            Double sum = 0.0;

            ArrayList<Double> scores = new ArrayList<>();
            for (Student s : course.getActiveStudents()) {
                Double score = s.getScore(a.name);

                if (s.hasScore(a.name)) {
                    scores.add(score);
                    sum += score;
                }

            }

            if (!scores.isEmpty()) {
                Double min = Collections.min(scores);
                Double max = Collections.max(scores);
                Double average = sum / scores.size();
                Double median = calculateMedian(scores);
                Map<String, Double> stats = new HashMap<>();
                stats.put("min", min);
                stats.put("max", max);
                stats.put("average", average);
                stats.put("median", median);
                result.put(a.name, stats);
            }
        }

        return result;
    }

    public Double calculateMedian(ArrayList<Double> scores) {
        Collections.sort(scores);
        int n = scores.size();
        if (n % 2 == 1) {
            return scores.get(n / 2);
        } else {
            return (scores.get(n / 2 - 1) + scores.get(n / 2)) / 2.0;
        }
    }

    // fill course fields from active students only
    public void computeCourseOverview(Course course) {
        activeCount = 0;
        avgFinalPercent = 0.0;
        medianFinalPercent = 0.0;
        minFinalPercent = 0.0;
        maxFinalPercent = 0.0;
        letterCounts.clear();

        if (course == null) {
            return;
        }

        ArrayList<Student> active = course.getActiveStudents();
        activeCount = active.size();
        if (activeCount == 0) {
            return;
        }

        ArrayList<Double> finals = new ArrayList<Double>();
        double sum = 0.0;
        double min = active.get(0).getFinalPercent();
        double max = active.get(0).getFinalPercent();

        for (int i = 0; i < active.size(); i++) {
            Student s = active.get(i);
            double fp = s.getFinalPercent();
            finals.add(fp);
            sum += fp;
            if (fp < min) {
                min = fp;
            }
            if (fp > max) {
                max = fp;
            }

            String letter = s.getLetterGrade();
            if (letter != null) {
                Integer n = letterCounts.get(letter);
                if (n == null) {
                    letterCounts.put(letter, 1);
                } else {
                    letterCounts.put(letter, n + 1);
                }
            }
        }

        avgFinalPercent = sum / activeCount;
        medianFinalPercent = calculateMedian(finals);
        minFinalPercent = min;
        maxFinalPercent = max;
    }

}
