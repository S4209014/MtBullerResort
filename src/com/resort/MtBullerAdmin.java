package com.resort;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class MtBullerAdmin {
    private MtBullerResort resort = new MtBullerResort();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new MtBullerAdmin().start();
    }

    private void start() {
        resort.setupDefaults();
        System.out.println("===================================");
        System.out.println("Welcome to the Mt Buller Custom Travel Bundle System");
        System.out.println("====================================");
        System.out.println();

        boolean keepGoing = true;
        while (keepGoing) {
            printMenu();
            int choice = readInt("Enter your choice (integer between 1 and 11): ", 1, 11);
            System.out.println();
            try {
                if (choice == 1) {
                    displayAllAccommodations();
                } else if (choice == 2) {
                    displayAvailable();
                } else if (choice == 3) {
                    addCustomer();
                } else if (choice == 4) {
                    listCustomers();
                } else if (choice == 5) {
                    addBundle();
                } else if (choice == 6) {
                    listBundles();
                } else if (choice == 7) {
                    addLiftPass();
                } else if (choice == 8) {
                    addLessons();
                } else if (choice == 9) {
                    saveBundles();
                } else if (choice == 10) {
                    readBundles();
                } else if (choice == 11) {
                    keepGoing = false;
                    System.out.println("Catch you on the slopes.");
                }
            } catch (DateInPastException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Something went sideways: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("----------- MAIN MENU -----------");
        System.out.println("1. Display all accomodations");
        System.out.println("2. Display available accommodations");
        System.out.println("3. Add customer");
        System.out.println("4. List customers");
        System.out.println("5. Create a bundle");
        System.out.println("6. List bundles (with customer & family member details)");
        System.out.println("7. Add a lift pass to a bundle");
        System.out.println("8. Add lessons to a bundle");
        System.out.println("9. Save bundles to a database");
        System.out.println("10. Read bundles from database");
        System.out.println("11. Quit");
        System.out.println("------------------------------------");
    }

    private void displayAllAccommodations() {
        System.out.println("--- All Accommodations ---");
        ArrayList<Accommodation> rooms = resort.getAccommodations();
        for (int i = 0; i < rooms.size(); i++) {
            System.out.println(rooms.get(i));
        }
    }

    private void displayAvailable() {
        LocalDate start = readStayDate(
                "Enter the check-in date you are looking for (format yyyy-MM-dd, e.g. 2026-09-01): ",
                false);
        int nights = readInt("Enter the number of nights (whole number between 1 and 60): ", 1, 60);
        int people = readInt("Total number of people (whole number between 1 and 20): ", 1, 20);
        ArrayList<Accommodation> rooms = resort.availableRooms(start, nights, people);
        if (rooms.isEmpty()) {
            System.out.println("Nothing free for that crew on those dates. Buller's a popular hill.");
            return;
        }
        System.out.println("--- All Accommodations ---");
        for (int i = 0; i < rooms.size(); i++) {
            System.out.println(rooms.get(i));
        }
    }

    private void addCustomer() {
        String name = readText("Enter customer's full name: ");
        String contact = readText("Enter customer's contact details (email or phone): ");
        SkiLevel level = readLevel("Enter the customer's skiing level (options: 1-Beginner, 2-Intermediate, 3-Expert): ");
        Customer customer = resort.addCustomer(name, contact, level);
        System.out.println("Customer added successfully: " + customer.shortLine());
        System.out.println("(They only hit the database later if you actually make them a bundle.)");
    }

    private void listCustomers() {
        System.out.println("-- All Customers --");
        ArrayList<Customer> list = resort.getCustomers();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).shortLine());
        }
    }

    private void addBundle() {
        int customerId = readInt("Enter the customer ID for this bundle (whole number between 1 and 2147483647): ",
                1, Integer.MAX_VALUE);
        Customer customer = resort.findCustomer(customerId);
        if (customer == null) {
            System.out.println("No customer with that ID. Try listing them first.");
            return;
        }

        LocalDate start = readStayDate(
                "Enter the bundle start date (format yyyy-MM-dd, e.g. 2026-09-01): ", true);
        int days = readInt("Enter the duration of the stay in days (whole number between 1 and 60): ", 1, 60);
        int familyCount = readInt(
                "Enter the number of family members joining this bundle (max 5) (whole number between 0 and 5): ",
                0, 5);

        TravelBundle bundle = resort.createBundle(customer, start, days);
        for (int i = 1; i <= familyCount; i++) {
            System.out.println("Details for family member " + i + " of " + familyCount + ":");
            String fname = readText("Enter family member's name: ");
            SkiLevel level = readLevel("Enter family member's skiing level (options: 1-Beginner, 2-Intermediate, 3-Expert): ");
            FamilyMember member = resort.makeFamilyMember(fname, level);
            bundle.addFamilyMember(member);
            customer.addFamilyMember(member);
        }

        int people = bundle.partySize();
        System.out.println("Accommodations available for " + people + " people ("
                + start + " to " + bundle.getEndDate() + ")");
        ArrayList<Accommodation> rooms = resort.availableRooms(start, days, people);
        if (rooms.isEmpty()) {
            System.out.println("No rooms that fit. Bundle abandoned - try different dates maybe.");
            return;
        }
        for (int i = 0; i < rooms.size(); i++) {
            System.out.println(rooms.get(i));
        }

        String accId = readText("Enter the ID of the accommodation to book: (e.g AP01) ");
        Accommodation room = resort.findAccommodation(accId);
        if (room == null) {
            System.out.println("That ID isn't in the list.");
            return;
        }
        boolean listed = false;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId().equalsIgnoreCase(accId.trim())) {
                listed = true;
            }
        }
        if (!listed) {
            System.out.println("That place isn't available for this stay.");
            return;
        }

        resort.attachRoom(bundle, room);
        System.out.println("Accommodation " + room.getId() + " has been attached to the bundle");
        System.out.println("Bundle created successfully");
        System.out.println(bundle.prettyPrint());
    }

    private void listBundles() {
        System.out.println("-- All Travel Bundles --");
        ArrayList<TravelBundle> list = resort.getBundles();
        if (list.isEmpty()) {
            System.out.println("No bundles yet. Go make one, it's why we're here.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).prettyPrint());
            System.out.println();
        }
    }

    private void addLiftPass() {
        if (resort.getBundles().isEmpty()) {
            System.out.println("No bundles to decorate with lift passes.");
            return;
        }
        listBundles();
        int bundleId = readInt("Enter the bundle ID: ", 1, Integer.MAX_VALUE);
        TravelBundle bundle = resort.findBundle(bundleId);
        if (bundle == null) {
            System.out.println("Can't find that bundle.");
            return;
        }

        ArrayList<PersonPick> people = listPeople(bundle);
        int pick = readInt("Select the person (whole number between 1 and " + people.size() + "): ",
                1, people.size());
        PersonPick person = people.get(pick - 1);

        System.out.println("Choose lift pass type: 1-Day pass(es) at $26.00/day, 2-Season pass (30 days) at $200.00 flat");
        int typeChoice = readInt("Enter your choice: ", 1, 2);

        LiftPass existing = bundle.findPass(person.id, person.customer);
        if (typeChoice == 2) {
            if (existing == null) {
                bundle.getLiftPasses().add(new LiftPass(person.id, person.customer, person.name,
                        LiftPassType.SEASON, 30));
            } else {
                existing.setType(LiftPassType.SEASON);
                existing.setDays(30);
            }
        } else {
            int extra = readInt("Enter the number of day-passes to buy for " + person.name + ": ", 1, 60);
            double rawCost = extra * LiftPass.DAY_PRICE;
            if (existing != null && existing.getType() == LiftPassType.DAY) {
                rawCost = (existing.getDays() + extra) * LiftPass.DAY_PRICE;
            }
            if (rawCost >= 180) {
                System.out.println("Heads up: that's getting near (or over) the $200 season pass. Might be nicer value.");
            }

            if (existing == null) {
                bundle.getLiftPasses().add(new LiftPass(person.id, person.customer, person.name,
                        LiftPassType.DAY, extra));
            } else if (existing.getType() == LiftPassType.DAY) {
                // bump the old number instead of adding a second line
                existing.addDays(extra);
            } else {
                existing.setType(LiftPassType.DAY);
                existing.setDays(extra);
            }
        }

        System.out.println("Lift pass added. Bundle lift pass total is now $"
                + String.format("%.2f", bundle.getLiftPassTotal()));
    }

    private void addLessons() {
        if (resort.getBundles().isEmpty()) {
            System.out.println("No bundles yet, so no one to teach.");
            return;
        }
        int bundleId = readInt("Enter the bundle ID: ", 1, Integer.MAX_VALUE);
        TravelBundle bundle = resort.findBundle(bundleId);
        if (bundle == null) {
            System.out.println("Can't find that bundle.");
            return;
        }

        ArrayList<PersonPick> people = listPeople(bundle);
        int pick = readInt("Select the person (whole number between 1 and " + people.size() + "): ",
                1, people.size());
        PersonPick person = people.get(pick - 1);

        int count = readInt("Enter the number of lessons for " + person.name + " (level: "
                + person.level + ", $" + person.level.getLessonPrice()
                + " each) (whole number between 0 and 30): ", 0, 30);

        Lesson existing = bundle.findLesson(person.id, person.customer);
        if (existing == null) {
            bundle.getLessons().add(new Lesson(person.id, person.customer, person.name, person.level, count));
        } else {
            // same line gets bumped, e.g. 9 lessons then 2 more -> 11
            existing.addCount(count);
        }

        System.out.println("Lessons added. Bundle lesson total is now $"
                + String.format("%.2f", bundle.getLessonTotal()));
    }

    private void saveBundles() {
        try {
            resort.saveToDatabase();
            System.out.println("Bundles saved to the database.");
        } catch (Exception e) {
            System.out.println("Could not save: " + e.getMessage());
        }
    }

    private void readBundles() {
        try {
            resort.reloadFromDatabase();
            listBundles();
        } catch (Exception e) {
            System.out.println("Could not read the database: " + e.getMessage());
        }
    }

    private ArrayList<PersonPick> listPeople(TravelBundle bundle) {
        ArrayList<PersonPick> people = new ArrayList<>();
        Customer customer = bundle.getCustomer();
        people.add(new PersonPick(customer.getId(), true, customer.getName(), customer.getLevel()));
        for (int i = 0; i < bundle.getFamily().size(); i++) {
            FamilyMember member = bundle.getFamily().get(i);
            people.add(new PersonPick(member.getId(), false, member.getName(), member.getLevel()));
        }
        System.out.println("People in this bundle:");
        for (int i = 0; i < people.size(); i++) {
            PersonPick p = people.get(i);
            String role = p.customer ? "customer" : "family member";
            System.out.println((i + 1) + ". [" + p.id + "] " + p.name + " (" + role + ")");
        }
        return people;
    }

    private LocalDate readStayDate(String prompt, boolean limitTwoYears) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(line);
                if (date.isBefore(LocalDate.now())) {
                    throw new DateInPastException("The date cannot be in the past. Please try again");
                }
                if (limitTwoYears && date.isAfter(LocalDate.now().plusYears(2))) {
                    System.out.println("Let's not plan that far ahead - 2 years out is the limit.");
                    continue;
                }
                return date;
            } catch (DateInPastException e) {
                System.out.println(e.getMessage());
            } catch (DateTimeParseException e) {
                System.out.println("That doesn't look like yyyy-MM-dd. Example: 2026-09-01");
            }
        }
    }

    private SkiLevel readLevel(String prompt) {
        while (true) {
            System.out.print(prompt);
            SkiLevel level = SkiLevel.fromChoice(scanner.nextLine());
            if (level != null) {
                return level;
            }
            System.out.println("Please enter 1, 2 or 3.");
        }
    }

    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                return line;
            }
            System.out.println("Need something here.");
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Please enter a whole number between " + min + " and " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    // tiny helper so we can pick a person off a bundle
    private static class PersonPick {
        int id;
        boolean customer;
        String name;
        SkiLevel level;

        PersonPick(int id, boolean customer, String name, SkiLevel level) {
            this.id = id;
            this.customer = customer;
            this.name = name;
            this.level = level;
        }
    }
}
