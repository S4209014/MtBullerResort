package com.resort;

import java.time.LocalDate;

public class Booking {
    private LocalDate start;
    private int nights;

    public Booking(LocalDate start, int nights) {
        this.start = start;
        this.nights = nights;
    }

    public LocalDate getStart() {
        return start;
    }

    public int getNights() {
        return nights;
    }

    public LocalDate getCheckout() {
        return start.plusDays(nights);
    }

    public boolean overlaps(LocalDate otherStart, int otherNights) {
        LocalDate otherEnd = otherStart.plusDays(otherNights);
        // overlap if one starts before the other checks out
        return start.isBefore(otherEnd) && otherStart.isBefore(getCheckout());
    }
}
