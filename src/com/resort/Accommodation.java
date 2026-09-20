package com.resort;

import java.time.LocalDate;
import java.util.ArrayList;

public abstract class Accommodation implements Pricable {
    private String id;
    private String label;
    private int capacity;
    private double pricePerNight;
    private ArrayList<Booking> bookings = new ArrayList<>();

    public Accommodation(String id, String label, int capacity, double pricePerNight) {
        this.id = id;
        this.label = label;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    public boolean canFit(int people) {
        return people <= capacity;
    }

    public boolean isFree(LocalDate start, int nights) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).overlaps(start, nights)) {
                return false;
            }
        }
        return true;
    }

    public void addBooking(LocalDate start, int nights) {
        bookings.add(new Booking(start, nights));
    }

    public void clearBookings() {
        bookings.clear();
    }

    @Override
    public double getPrice() {
        return pricePerNight;
    }

    @Override
    public String toString() {
        return String.format("[%s]  %s  -  Capacity: %d  -  $%.2f/night",
                id, label, capacity, pricePerNight);
    }
}
