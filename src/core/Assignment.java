// assignment class

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

    // constructor with custom max points
    public Assignment(String name, int maxPoints) {
        this.name = name;
        this.maxPoints = maxPoints;
    }

    // set assignment weight
    public void setWeight(double weight) {
        this.weight = weight;
    }

}
