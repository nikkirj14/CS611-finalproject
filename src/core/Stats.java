package core;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Stats {
    
    

    public void statsByStudent(Student student) {

    }

    public Map<String, Map<String, Double>> statsByAssignment(Course course){
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

    public Double calculateMedian (ArrayList<Double> scores) {
        Collections.sort(scores);
        int n = scores.size();
        if (n % 2 == 1) {
            return scores.get(n / 2);
        } else {
            return (scores.get(n / 2 - 1) + scores.get(n / 2)) / 2.0;
        }
    }


}
