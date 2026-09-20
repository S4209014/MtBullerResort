package com.resort;

public class LiftPass implements Pricable {
    public static final double DAY_PRICE = 26.0;
    public static final double SEASON_PRICE = 200.0;

    private int personId;
    private boolean customer; // true = the booking holder
    private String name;
    private LiftPassType type;
    private int days;

    public LiftPass(int personId, boolean customer, String name, LiftPassType type, int days) {
        this.personId = personId;
        this.customer = customer;
        this.name = name;
        this.type = type;
        this.days = days;
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

    public LiftPassType getType() {
        return type;
    }

    public int getDays() {
        return days;
    }

    public void setType(LiftPassType type) {
        this.type = type;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void addDays(int extra) {
        this.days = this.days + extra;
    }

    public boolean samePerson(int id, boolean isCustomer) {
        return personId == id && customer == isCustomer;
    }

    @Override
    public double getPrice() {
        if (type == LiftPassType.SEASON) {
            return SEASON_PRICE;
        }
        return days * DAY_PRICE;
    }

    public String summary() {
        if (type == LiftPassType.SEASON) {
            return name + ": season pass  -  $" + String.format("%.2f", getPrice());
        }
        return name + ": " + days + " day passes  -  $" + String.format("%.2f", getPrice());
    }
}
