package com.resort;

import java.util.ArrayList;

public class Customer {
    private int id;
    private String name;
    private String contact; // email
    private String phone;
    private SkiLevel level;
    private ArrayList<FamilyMember> family = new ArrayList<>();

    public Customer(int id, String name, String contact, SkiLevel level) {
        this(id, name, contact, "", level);
    }

    public Customer(int id, String name, String contact, String phone, SkiLevel level) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
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
        return String.format("%-5s  %-22s  %-28s  %-12s  %s", "ID", "Name", "Email", "Phone", "Level");
    }

    public String tableRow() {
        return String.format("#%-4d  %-22s  %-28s  %-12s  %s", id, name, contact, phone, level);
    }

    @Override
    public String toString() {
        return String.format("C%03d - %s", id, name);
    }
}
