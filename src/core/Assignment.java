package core;


public class Assignment {
    protected String name;
    protected double weight;
    protected double maxPoints;
    protected String note;

    public Assignment(String name) {
        this.name = name;
        this.maxPoints = 100;
    }

    public Assignment(String name, int maxPoints) {
        this.name = name;
        this.maxPoints = maxPoints;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
