package com.resort;

public class Lesson implements Pricable {
    private int personId;
    private boolean customer;
    private String name;
    private SkiLevel level;
    private int count;

    public Lesson(int personId, boolean customer, String name, SkiLevel level, int count) {
        this.personId = personId;
        this.customer = customer;
        this.name = name;
        this.level = level;
        this.count = count;
    }

    public int getPersonId() {
        return personId;
    }

    public boolean isCustomer() {
        return customer;
    }

    public String getName() {
        return name;
    }

    public SkiLevel getLevel() {
        return level;
    }

    public int getCount() {
        return count;
    }

    public void addCount(int extra) {
        this.count = this.count + extra;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean samePerson(int id, boolean isCustomer) {
        return personId == id && customer == isCustomer;
    }

    @Override
    public double getPrice() {
        return count * level.getLessonPrice();
    }

    public String summary() {
        return name + ": " + count + " lesson(s) @ " + level + " level - $"
                + String.format("%.2f", getPrice());
    }
}
