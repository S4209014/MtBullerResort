package com.resort;

import java.util.ArrayList;

public class Customer {
    private int id;
    private String name;
    private String contact;
    private SkiLevel level;
    private ArrayList<FamilyMember> family = new ArrayList<>();

    public Customer(int id, String name, String contact, SkiLevel level) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public SkiLevel getLevel() {
        return level;
    }

    public ArrayList<FamilyMember> getFamily() {
        return family;
    }

    public void addFamilyMember(FamilyMember member) {
        family.add(member);
    }

    public static String tableHeader() {
        return String.format("%-5s  %-22s  %-28s  %s", "ID", "Name", "Contact", "Level");
    }

    public String tableRow() {
        return String.format("#%-4d  %-22s  %-28s  %s", id, name, contact, level);
    }
}
