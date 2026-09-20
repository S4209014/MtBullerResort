package com.resort;

import java.time.LocalDate;
import java.util.ArrayList;

public class TravelBundle implements Pricable {
    private int id;
    private Customer customer;
    private LocalDate startDate;
    private int days;
    private Accommodation accommodation;
    private ArrayList<FamilyMember> family = new ArrayList<>();
    private ArrayList<LiftPass> liftPasses = new ArrayList<>();
    private ArrayList<Lesson> lessons = new ArrayList<>();

    public TravelBundle(int id, Customer customer, LocalDate startDate, int days) {
        this.id = id;
        this.customer = customer;
        this.startDate = startDate;
        this.days = days;
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

    public int getDays() {
        return days;
    }

    public LocalDate getEndDate() {
        return startDate.plusDays(days);
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
        return accommodation.getPricePerNight() * days;
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

    public String prettyPrint() {
        String acc = "(none yet)";
        if (accommodation != null) {
            acc = accommodation.getLabel() + "  [" + accommodation.getId() + "]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Bundle #").append(id).append("\n");
        sb.append("\tCustomer: ").append(customer.getName())
                .append("  (ID ").append(customer.getId()).append(")\n");
        sb.append("\tDates: ").append(startDate).append("  for  ").append(days)
                .append(" days,  ending  ").append(getEndDate()).append("\n");
        sb.append("\tFamily members (").append(family.size()).append("):  ").append(familyLine()).append("\n");
        sb.append("\tAccommodation: ").append(acc).append("\n");
        sb.append("\tLift passes: ").append(passLine()).append("\n");
        sb.append("\tLessons: ").append(lessonLine()).append("\n");
        sb.append("\tTotal price: $").append(String.format("%.2f", getPrice()));
        return sb.toString();
    }
}
