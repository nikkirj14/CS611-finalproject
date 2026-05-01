// student class for identity and grade data

package core;

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

    // constructor with empty score map
    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.active = true;
        this.scores = new HashMap<String, Double>();
    }

    // constructor with a provided score map
    public Student(String studentId, String name, String email, HashMap<String, Double> scores) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.active = true;
        if (scores == null) {
            this.scores = new HashMap<String, Double>();
        } else {
            this.scores = scores;
        }
    }

    // set one assignment score
    public void setScore(String assignmentName, double score) {
        scores.put(assignmentName, score);
    }

    public boolean hasScore(String assignmentName) {
        return scores != null && scores.containsKey(assignmentName);
    }

    // if there is no score yet, treat as 0
    public double getScore(String assignmentName) {
        Double v = scores.get(assignmentName);
        if (v == null) {
            return 0.0;
        }
        return v.doubleValue();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public HashMap<String, Double> getScores() {
        return scores;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getFinalPercent() {
        return finalPercent;
    }

    public void setFinalPercent(double finalPercent) {
        this.finalPercent = finalPercent;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public void setLetterGrade(String letterGrade) {
        this.letterGrade = letterGrade;
    }
}
