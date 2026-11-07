package com.resort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MtBullerResort {
    public static final String DB_FILE = "bundles.db";
    private ArrayList<Accommodation> accommodations = new ArrayList<>();
    private ArrayList<Customer> customers = new ArrayList<>();
    private ArrayList<TravelBundle> bundles = new ArrayList<>();
    private int nextCustomerId = 1;
    private int nextBundleId = 1;
    private int nextFamilyId = 1;
    private BundleDatabase database = new BundleDatabase(DB_FILE);

    public ArrayList<Accommodation> getAccommodations() {
        return accommodations;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<TravelBundle> getBundles() {
        return bundles;
    }

    public void setupDefaults() {
        // always load rooms - the mountain needs somewhere to sleep
        if (accommodations.isEmpty()) {
            accommodations.add(new HotelRoom("HR01", 120.00));
            accommodations.add(new HotelRoom("HR02", 135.00));
            accommodations.add(new HotelRoom("HR03", 110.00));
            accommodations.add(new LodgeRoom("LR01", 80.00));
            accommodations.add(new LodgeRoom("LR02", 75.00));
            accommodations.add(new LodgeRoom("LR03", 90.00));
            accommodations.add(new Apartment("AP01", 210.00));
            accommodations.add(new Apartment("AP02", 250.00));
            accommodations.add(new Apartment("AP03", 190.00));
            accommodations.add(new Apartment("AP04", 230.00));
        }

        java.io.File dbFile = new java.io.File(DB_FILE);
        if (dbFile.exists()) {
            try {
                database.loadInto(this);
            } catch (Exception e) {
                System.out.println("Could not read the database, starting with the usual suspects instead.");
            }
        }

        // defaults if the db is missing or had no customers
        seedCustomers();
        bumpIds();
        sortCustomers();
    }

    private void seedCustomers() {
        if (findCustomer(1) == null) {
            customers.add(new Customer(1, "Alice Johnson", "alice.j@email.com", SkiLevel.INTERMEDIATE));
        }
        if (findCustomer(2) == null) {
            customers.add(new Customer(2, "Brian Smith", "brian.smith@email.com", SkiLevel.BEGINNER));
        }
        if (findCustomer(3) == null) {
            customers.add(new Customer(3, "David Lee", "david.lee@email.com", SkiLevel.EXPERT));
        }
    }

    private void bumpIds() {
        int maxC = 0;
        int maxB = 0;
        int maxF = 0;
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() > maxC) {
                maxC = customers.get(i).getId();
            }
            ArrayList<FamilyMember> fam = customers.get(i).getFamily();
            for (int j = 0; j < fam.size(); j++) {
                if (fam.get(j).getId() > maxF) {
                    maxF = fam.get(j).getId();
                }
            }
        }
        for (int i = 0; i < bundles.size(); i++) {
            if (bundles.get(i).getId() > maxB) {
                maxB = bundles.get(i).getId();
            }
            ArrayList<FamilyMember> fam = bundles.get(i).getFamily();
            for (int j = 0; j < fam.size(); j++) {
                if (fam.get(j).getId() > maxF) {
                    maxF = fam.get(j).getId();
                }
            }
        }
        nextCustomerId = maxC + 1;
        nextBundleId = maxB + 1;
        nextFamilyId = maxF + 1;
        if (nextCustomerId < 1) {
            nextCustomerId = 1;
        }
        if (nextBundleId < 1) {
            nextBundleId = 1;
        }
        if (nextFamilyId < 1) {
            nextFamilyId = 1;
        }
    }

    // keep the list in ID order so #4 is not sitting above Alice
    private void sortCustomers() {
        for (int i = 0; i < customers.size(); i++) {
            int smallest = i;
            for (int j = i + 1; j < customers.size(); j++) {
                if (customers.get(j).getId() < customers.get(smallest).getId()) {
                    smallest = j;
                }
            }
            Customer swap = customers.get(i);
            customers.set(i, customers.get(smallest));
            customers.set(smallest, swap);
        }
    }

    public Customer addCustomer(String name, String contact, SkiLevel level) {
        while (findCustomer(nextCustomerId) != null) {
            nextCustomerId++;
        }
        Customer customer = new Customer(nextCustomerId, name, contact, level);
        nextCustomerId++;
        customers.add(customer);
        sortCustomers();
        return customer;
    }

    public Customer findCustomer(int id) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == id) {
                return customers.get(i);
            }
        }
        return null;
    }

    public Accommodation findAccommodation(String id) {
        for (int i = 0; i < accommodations.size(); i++) {
            if (accommodations.get(i).getId().equalsIgnoreCase(id.trim())) {
                return accommodations.get(i);
            }
        }
        return null;
    }

    public TravelBundle findBundle(int id) {
        for (int i = 0; i < bundles.size(); i++) {
            if (bundles.get(i).getId() == id) {
                return bundles.get(i);
            }
        }
        return null;
    }

    public ArrayList<Accommodation> getAvailableRooms(LocalDate start, int nights, int people) {
        ArrayList<Accommodation> result = new ArrayList<>();
        for (int i = 0; i < accommodations.size(); i++) {
            Accommodation room = accommodations.get(i);
            if (room.canFit(people) && room.isFree(start, nights)) {
                result.add(room);
            }
        }
        return result;
    }

    public FamilyMember makeFamilyMember(String name, SkiLevel level) {
        FamilyMember member = new FamilyMember(nextFamilyId, name, level);
        nextFamilyId++;
        return member;
    }

    public TravelBundle createBundle(Customer customer, LocalDate start, int nights) {
        TravelBundle bundle = new TravelBundle(nextBundleId, customer, start, nights);
        nextBundleId++;
        return bundle;
    }

    public void attachRoom(TravelBundle bundle, Accommodation room) {
        room.addBooking(bundle.getStartDate(), bundle.getNights());
        bundle.setAccommodation(room);
        bundles.add(bundle);
    }

    public void addCustomerFromDb(Customer customer) {
        if (findCustomer(customer.getId()) == null) {
            customers.add(customer);
        }
    }

    // ---------- validation (used by the GUI, which just shows the message) ----------

    public static String validateName(String name) throws InvalidBookingException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidBookingException("Name cannot be empty.");
        }
        if (!name.trim().matches("[A-Za-z ]+")) {
            throw new InvalidBookingException("Name must contain letters and spaces only.");
        }
        return name.trim();
    }

    public static String validateEmail(String email) throws InvalidBookingException {
        if (email == null || !email.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new InvalidBookingException("Email must look like name@example.com.");
        }
        return email.trim();
    }

    public static String validatePhone(String phone) throws InvalidBookingException {
        if (phone == null || !phone.trim().matches("\\d{10}")) {
            throw new InvalidBookingException("Phone must be 10 digits with no spaces.");
        }
        return phone.trim();
    }

    public int peekNextCustomerId() {
        int id = nextCustomerId;
        while (findCustomer(id) != null) {
            id++;
        }
        return id;
    }

    public Customer addCustomer(String name, String email, String phone, SkiLevel level)
            throws InvalidBookingException {
        String cleanName = validateName(name);
        String cleanEmail = validateEmail(email);
        String cleanPhone = validatePhone(phone);
        while (findCustomer(nextCustomerId) != null) {
            nextCustomerId++;
        }
        Customer customer = new Customer(nextCustomerId, cleanName, cleanEmail, cleanPhone, level);
        nextCustomerId++;
        customers.add(customer);
        sortCustomers();
        return customer;
    }

    // ---------- bundles ----------

    /** Rooms that fit the party and are free, optionally filtered by type and a maximum nightly price. */
    public ArrayList<Accommodation> getAvailableRooms(LocalDate start, int nights, int people,
                                                      Class<? extends Accommodation> type, Double maxPrice) {
        ArrayList<Accommodation> result = new ArrayList<>();
        for (Accommodation room : getAvailableRooms(start, nights, people)) {
            if (type != null && !type.isInstance(room)) {
                continue;
            }
            if (maxPrice != null && room.getPricePerNight() > maxPrice) {
                continue;
            }
            result.add(room);
        }
        return result;
    }

    public TravelBundle addBundle(int customerId, LocalDate start, int nights, List<FamilyMember> family,
                                  String accommodationId) throws InvalidBookingException {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            throw new InvalidBookingException("Please select a customer.");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new DateInPastException("Start date cannot be in the past.");
        }
        if (start.isAfter(LocalDate.now().plusYears(2))) {
            throw new InvalidBookingException("Let's not plan that far ahead - 2 years out is the limit.");
        }
        if (nights < 1) {
            throw new InvalidBookingException("Duration must be at least 1 night.");
        }
        if (family.size() > 5) {
            throw new InvalidBookingException("A bundle can include at most 5 family members.");
        }
        if (accommodationId == null || findAccommodation(accommodationId) == null) {
            throw new InvalidBookingException("Please select an accommodation from the list.");
        }
        Accommodation room = findAccommodation(accommodationId);
        int party = 1 + family.size();
        if (!room.canFit(party)) {
            throw new InvalidBookingException(room.getId() + " only sleeps " + room.getCapacity()
                    + " but your party is " + party + ".");
        }
        if (!room.isFree(start, nights)) {
            throw new InvalidBookingException(room.getId() + " is already booked on those dates.");
        }

        TravelBundle bundle = createBundle(customer, start, nights);
        for (FamilyMember member : family) {
            FamilyMember saved = makeFamilyMember(member.getName(), member.getLevel());
            bundle.addFamilyMember(saved);
            customer.addFamilyMember(saved);
        }
        attachRoom(bundle, room);
        return bundle;
    }

    public void addLiftPass(TravelBundle bundle, int personId, boolean isCustomer, String name,
                            LiftPassType type, int days) throws InvalidBookingException {
        if (bundle == null) {
            throw new InvalidBookingException("Select a bundle in the list first.");
        }
        LiftPass existing = bundle.findPass(personId, isCustomer);
        if (type == LiftPassType.SEASON) {
            if (existing == null) {
                bundle.getLiftPasses().add(new LiftPass(personId, isCustomer, name, LiftPassType.SEASON, 30));
            } else {
                existing.setType(LiftPassType.SEASON);
                existing.setDays(30);
            }
            return;
        }
        if (days < 1) {
            throw new InvalidBookingException("Enter the number of days as a whole number, e.g. 3.");
        }
        if (existing == null) {
            bundle.getLiftPasses().add(new LiftPass(personId, isCustomer, name, LiftPassType.DAY, days));
        } else if (existing.getType() == LiftPassType.DAY) {
            existing.addDays(days);
        } else {
            existing.setType(LiftPassType.DAY);
            existing.setDays(days);
        }
    }

    public void addLessons(TravelBundle bundle, int personId, boolean isCustomer, String name,
                           SkiLevel level, int count) throws InvalidBookingException {
        if (bundle == null) {
            throw new InvalidBookingException("Select a bundle in the list first.");
        }
        if (count < 0) {
            throw new InvalidBookingException("Lessons cannot be negative.");
        }
        Lesson existing = bundle.findLesson(personId, isCustomer);
        if (existing == null) {
            bundle.getLessons().add(new Lesson(personId, isCustomer, name, level, count));
        } else {
            existing.addCount(count);
        }
    }

    public void addBundleFromDb(TravelBundle bundle) {
        // keep the customer's own family list in step with what the database held
        for (FamilyMember member : bundle.getFamily()) {
            boolean known = false;
            for (FamilyMember existing : bundle.getCustomer().getFamily()) {
                if (existing.getId() == member.getId()) {
                    known = true;
                }
            }
            if (!known) {
                bundle.getCustomer().addFamilyMember(member);
            }
        }
        bundles.add(bundle);
        if (bundle.getAccommodation() != null) {
            bundle.getAccommodation().addBooking(bundle.getStartDate(), bundle.getNights());
        }
    }

    public void saveToDatabase() throws Exception {
        database.save(bundles);
    }

    public int reloadFromDatabase() throws Exception {
        for (int i = 0; i < accommodations.size(); i++) {
            accommodations.get(i).clearBookings();
        }
        bundles.clear();
        database.loadInto(this);
        seedCustomers();
        bumpIds();
        sortCustomers();
        return bundles.size();
    }
}
