package com.resort;

public enum SkiLevel {
    BEGINNER(25.0),
    INTERMEDIATE(20.0),
    EXPERT(15.0);

    private final double lessonPrice;

    SkiLevel(double lessonPrice) {
        this.lessonPrice = lessonPrice;
    }

    public double getLessonPrice() {
        return lessonPrice;
    }

    // 1, 2, 3 or the name itself
    public static SkiLevel fromChoice(String input) {
        String text = input.trim().toUpperCase();
        if (text.equals("1") || text.equals("BEGINNER")) {
            return BEGINNER;
        }
        if (text.equals("2") || text.equals("INTERMEDIATE")) {
            return INTERMEDIATE;
        }
        if (text.equals("3") || text.equals("EXPERT")) {
            return EXPERT;
        }
        return null;
    }
}
