

import java.util.HashMap;

public class Student {

    protected String studentId;
    protected String name;
    protected String email;
    protected boolean active;
    protected HashMap<String, Double> scores;
    protected String note;
    protected double finalPercent;
    protected String letterGrade;

    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
    }

    public Student(String studentId, String name, String email, HashMap<String, Double> scores) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.scores = scores;
    }

    public void setScore(String assignmentName, double score) {
        scores.put(assignmentName, score);
    }

    public double getScore(String assignmentName) {
        return scores.get(assignmentName);
    }
    
}
