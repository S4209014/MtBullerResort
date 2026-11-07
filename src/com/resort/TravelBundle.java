package com.resort;

import java.time.LocalDate;
import java.util.ArrayList;

public class TravelBundle implements Pricable {
    private int id;
    private Customer customer;
    private LocalDate startDate;
    private int nights;
    private Accommodation accommodation;
    private ArrayList<FamilyMember> family = new ArrayList<>();
    private ArrayList<LiftPass> liftPasses = new ArrayList<>();
    private ArrayList<Lesson> lessons = new ArrayList<>();

    public TravelBundle(int id, Customer customer, LocalDate startDate, int nights) {
        this.id = id;
        this.customer = customer;
        this.startDate = startDate;
        this.nights = nights;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getNights() {
        return nights;
    }

    public LocalDate getEndDate() {
        return startDate.plusDays(nights);
    }

    public Accommodation getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(Accommodation accommodation) {
        this.accommodation = accommodation;
    }

    public ArrayList<FamilyMember> getFamily() {
        return family;
    }

    public void addFamilyMember(FamilyMember member) {
        family.add(member);
    }

    public int partySize() {
        return 1 + family.size();
    }

    public ArrayList<LiftPass> getLiftPasses() {
        return liftPasses;
    }

    public ArrayList<Lesson> getLessons() {
        return lessons;
    }

    public LiftPass findPass(int personId, boolean isCustomer) {
        for (int i = 0; i < liftPasses.size(); i++) {
            if (liftPasses.get(i).samePerson(personId, isCustomer)) {
                return liftPasses.get(i);
            }
        }
        return null;
    }

    public Lesson findLesson(int personId, boolean isCustomer) {
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).samePerson(personId, isCustomer)) {
                return lessons.get(i);
            }
        }
        return null;
    }

    public int totalDayPasses() {
        int total = 0;
        for (int i = 0; i < liftPasses.size(); i++) {
            LiftPass pass = liftPasses.get(i);
            if (pass.getType() == LiftPassType.DAY) {
                total = total + pass.getDays();
            }
        }
        return total;
    }

    public double getLiftPassTotal() {
        double dayTotal = 0;
        double seasonTotal = 0;
        for (int i = 0; i < liftPasses.size(); i++) {
            LiftPass pass = liftPasses.get(i);
            if (pass.getType() == LiftPassType.SEASON) {
                seasonTotal = seasonTotal + pass.getPrice();
            } else {
                dayTotal = dayTotal + pass.getPrice();
            }
        }
        if (totalDayPasses() >= 5) {
            dayTotal = dayTotal * 0.9;
        }
        return dayTotal + seasonTotal;
    }

    public double getLessonTotal() {
        double total = 0;
        for (int i = 0; i < lessons.size(); i++) {
            total = total + lessons.get(i).getPrice();
        }
        return total;
    }

    public double getStayTotal() {
        if (accommodation == null) {
            return 0;
        }
        return accommodation.getPricePerNight() * nights;
    }

    @Override
    public double getPrice() {
        return getStayTotal() + getLiftPassTotal() + getLessonTotal();
    }

    private String familyLine() {
        if (family.isEmpty()) {
            return "none";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < family.size(); i++) {
            FamilyMember member = family.get(i);
            if (i > 0) {
                sb.append("  ");
            }
            sb.append(member.getName()).append(" (").append(member.getLevel()).append(");");
        }
        return sb.toString();
    }

    private String passLine() {
        if (liftPasses.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < liftPasses.size(); i++) {
            if (i > 0) {
                sb.append(",  ");
            }
            sb.append(liftPasses.get(i).summary());
        }
        sb.append("]");
        return sb.toString();
    }

    private String lessonLine() {
        if (lessons.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lessons.size(); i++) {
            if (i > 0) {
                sb.append(",  ");
            }
            sb.append(lessons.get(i).summary());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public String toString() {
        String room = accommodation == null ? "no room" : accommodation.getId();
        return String.format("B%03d · %s · %s", id, customer.getName(), room);
    }

    /** Per-person table plus the cost breakdown, for the Bundles tab details area. */
    public String costBreakdown() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Bundle B%03d · %s (C%03d)%n", id, customer.getName(), customer.getId()));
        if (accommodation != null) {
            sb.append(String.format("Stay   %s, sleeps %d%n", accommodation.getLabel(), accommodation.getCapacity()));
        }
        sb.append(String.format("       %s -> %s (%d nights)%n%n", startDate, getEndDate(), nights));

        sb.append(String.format("%-20s %-14s %-12s %s%n", "PERSON", "LEVEL", "LIFT", "LESSONS"));
        sb.append(personRow(customer.getId(), true, customer.getName(), customer.getLevel()));
        for (FamilyMember member : family) {
            sb.append(personRow(member.getId(), false, member.getName(), member.getLevel()));
        }
        sb.append("\n");

        double dayRaw = 0;
        int seasonCount = 0;
        for (LiftPass pass : liftPasses) {
            if (pass.getType() == LiftPassType.SEASON) {
                seasonCount++;
            } else {
                dayRaw += pass.getPrice();
            }
        }
        int dayCount = totalDayPasses();
        boolean discount = dayCount >= 5;
        double dayCost = discount ? dayRaw * 0.9 : dayRaw;

        if (accommodation != null) {
            sb.append(String.format("%-18s %-22s $%,9.2f%n", "Accommodation",
                    nights + " x $" + String.format("%.2f", accommodation.getPricePerNight()), getStayTotal()));
        }
        if (dayCount > 0) {
            String label = dayCount + " x $" + (int) LiftPass.DAY_PRICE + (discount ? " - 10%" : "");
            sb.append(String.format("%-18s %-22s $%,9.2f%n", "Day passes", label, dayCost));
        }
        if (seasonCount > 0) {
            sb.append(String.format("%-18s %-22s $%,9.2f%n", "Season pass",
                    seasonCount + " x $" + (int) LiftPass.SEASON_PRICE, seasonCount * LiftPass.SEASON_PRICE));
        }
        if (!lessons.isEmpty()) {
            StringBuilder calc = new StringBuilder();
            for (Lesson lesson : lessons) {
                if (lesson.getCount() == 0) {
                    continue;
                }
                if (calc.length() > 0) {
                    calc.append("+");
                }
                calc.append(lesson.getCount()).append("x").append((int) lesson.getLevel().getLessonPrice());
            }
            sb.append(String.format("%-18s %-22s $%,9.2f%n", "Lessons", calc, getLessonTotal()));
        }
        sb.append("\n");
        sb.append(String.format("%-41s $%,9.2f", "TOTAL", getPrice()));
        return sb.toString();
    }

    private String personRow(int personId, boolean isCustomer, String name, SkiLevel level) {
        LiftPass pass = findPass(personId, isCustomer);
        Lesson lesson = findLesson(personId, isCustomer);
        String lift = "-";
        if (pass != null) {
            lift = pass.getType() == LiftPassType.SEASON ? "Season" : pass.getDays() + " days";
        }
        String lessonCount = lesson == null ? "0" : String.valueOf(lesson.getCount());
        return String.format("%-20s %-14s %-12s %s%n", name, level, lift, lessonCount);
    }

    public String prettyPrint() {
        String acc = "(none yet)";
        if (accommodation != null) {
            acc = accommodation.getLabel() + "  [" + accommodation.getId() + "]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Bundle #").append(id).append("\n");
        sb.append("\tCustomer: ").append(customer.getName())
                .append("  (ID ").append(customer.getId()).append(")\n");
        sb.append("\tDates: ").append(startDate).append("  for  ").append(nights)
                .append(" nights,  ending  ").append(getEndDate()).append("\n");
        sb.append("\tFamily members (").append(family.size()).append("):  ").append(familyLine()).append("\n");
        sb.append("\tAccommodation: ").append(acc).append("\n");
        sb.append("\tLift passes: ").append(passLine()).append("\n");
        sb.append("\tLessons: ").append(lessonLine()).append("\n");
        sb.append("\tTotal price: $").append(String.format("%.2f", getPrice()));
        return sb.toString();
    }
}
