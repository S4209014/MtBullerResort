package com.resort;

public class FamilyMember {
    private int id;
    private String name;
    private SkiLevel level;

    public FamilyMember(int id, String name, SkiLevel level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SkiLevel getLevel() {
        return level;
    }
}
