/*
 * Assignment class represents an assignment in a course, with name, weight, max points, and optional note.
 * It is used to store the assignment data for each course and to calculate student scores.
*/
package core;

public class Assignment {
    protected String name;
    protected double weight;
    protected double maxPoints;
    protected String note;

    // constructor with default max points
    public Assignment(String name) {
        this.name = name;
        this.maxPoints = 100;
    }

    public Assignment(String name, double weight, double maxPoints, String note) {
        this.name = name;
        this.weight = weight;
        this.maxPoints = maxPoints;
        this.note = note;
    }

    // constructor with custom max points
    public Assignment(String name, int maxPoints) {
        this.name = name;
        this.maxPoints = maxPoints;
    }

    // set assignment weight
    public void setWeight(double weight) {
        this.weight = weight;
    }

    
    public String getName() {
        return name;
    }

    public double getWeight() {
        return weight;
    }

    public double getMaxPoints() {
        return maxPoints;
    }

    public String getNote() {
        return note;
    }

}
