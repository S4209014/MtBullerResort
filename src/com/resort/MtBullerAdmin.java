package com.resort;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class MtBullerAdmin {
    private static final String WELCOME = "Welcome to the Mt Buller Custom Travel Bundle System";
    private static final int WIDTH = WELCOME.length();
    private static final String RESET = "\u001B[0m";
    private static final String WHITE = "\u001B[97m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BOLD = "\u001B[1m";

    private MtBullerResort resort = new MtBullerResort();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new MtBullerAdmin().start();
    }

    private void start() {
        resort.setupDefaults();
        printWelcome();

        boolean keepGoing = true;
        while (keepGoing) {
            printMenu();
            int choice = readInt("Enter your choice (1-11): ", 1, 11);
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
                    ok("Catch you on the slopes.");
                }
            } catch (DateInPastException e) {
                err(e.getMessage());
            } catch (Exception e) {
                err("Something went sideways: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printWelcome() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(WHITE + BOLD + fill('='));
        System.out.println(WELCOME);
        System.out.println(fill('=') + RESET);
        System.out.println();
    }

    private void printMenu() {
        printHeader("MAIN MENU");
        System.out.println(YELLOW + " 1." + RESET + " Display all accomodations");
        System.out.println(YELLOW + " 2." + RESET + " Display available accommodations");
        System.out.println(YELLOW + " 3." + RESET + " Add customer");
        System.out.println(YELLOW + " 4." + RESET + " List customers");
        System.out.println(YELLOW + " 5." + RESET + " Create a bundle");
        System.out.println(YELLOW + " 6." + RESET + " List bundles (customer and family)");
        System.out.println(YELLOW + " 7." + RESET + " Add a lift pass to a bundle");
        System.out.println(YELLOW + " 8." + RESET + " Add lessons to a bundle");
        System.out.println(YELLOW + " 9." + RESET + " Save bundles to a database");
        System.out.println(YELLOW + "10." + RESET + " Read bundles from database");
        System.out.println(YELLOW + "11." + RESET + " Quit");
        printBar();
        System.out.println();
    }

    private void printHeader(String title) {
        System.out.println(WHITE + labelledBar(title) + RESET);
    }

    private void printBar() {
        System.out.println(WHITE + fill('=') + RESET);
    }

    private String fill(char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < WIDTH; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    // =====  TITLE  =====  same width as the welcome bars
    private String labelledBar(String title) {
        String mid = "  " + title + "  ";
        int leftover = WIDTH - mid.length();
        if (leftover < 2) {
            leftover = 2;
        }
        int left = leftover / 2;
        int right = leftover - left;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left; i++) {
            sb.append('=');
        }
        sb.append(mid);
        for (int i = 0; i < right; i++) {
            sb.append('=');
        }
        return sb.toString();
    }

    private void ok(String msg) {
        System.out.println(GREEN + msg + RESET);
    }

    private void err(String msg) {
        System.out.println(RED + msg + RESET);
    }

    private void displayAllAccommodations() {
        printHeader("ALL ACCOMMODATIONS");
        System.out.println();
        ArrayList<Accommodation> rooms = resort.getAccommodations();
        for (int i = 0; i < rooms.size(); i++) {
            System.out.println(rooms.get(i));
        }
    }

    private void displayAvailable() {
        LocalDate start = readStayDate("Enter check-in date (yyyy-MM-dd): ", false);
        int nights = readInt("Enter number of nights (1-60): ", 1, 60);
        int people = readInt("Total number of people (1-20): ", 1, 20);
        System.out.println();
        ArrayList<Accommodation> rooms = resort.availableRooms(start, nights, people);
        if (rooms.isEmpty()) {
            err("Nothing free for that crew on those dates. Buller's a popular hill.");
            return;
        }
        printHeader("AVAILABLE ACCOMMODATIONS");
        System.out.println();
        for (int i = 0; i < rooms.size(); i++) {
            System.out.println(rooms.get(i));
        }
    }

    private void addCustomer() {
        String name = readText("Enter customer's full name: ");
        String contact = readText("Enter contact (email or phone): ");
        SkiLevel level = readLevel("Enter ski level (1=Beginner, 2=Intermediate, 3=Expert): ");
        Customer customer = resort.addCustomer(name, contact, level);
        System.out.println();
        ok("Customer added successfully:");
        System.out.println(customer.shortLine());
        System.out.println("(Saved to the database only if they get a bundle.)");
    }

    private void listCustomers() {
        printHeader("ALL CUSTOMERS");
        System.out.println();
        ArrayList<Customer> list = resort.getCustomers();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).shortLine());
        }
    }

    private void addBundle() {
        int customerId = readInt("Enter customer ID: ", 1, Integer.MAX_VALUE);
        Customer customer = resort.findCustomer(customerId);
        if (customer == null) {
            err("No customer with that ID. Try listing them first.");
            return;
        }

        System.out.println();
        LocalDate start = readStayDate("Enter start date (yyyy-MM-dd): ", true);
        int days = readInt("Enter stay length in days (1-60): ", 1, 60);
        int familyCount = readInt("How many family members? (0-5): ", 0, 5);

        TravelBundle bundle = resort.createBundle(customer, start, days);
        for (int i = 1; i <= familyCount; i++) {
            System.out.println();
            System.out.println("Family member " + i + " of " + familyCount);
            String fname = readText("Enter name: ");
            SkiLevel level = readLevel("Enter ski level (1=Beginner, 2=Intermediate, 3=Expert): ");
            FamilyMember member = resort.makeFamilyMember(fname, level);
            bundle.addFamilyMember(member);
            customer.addFamilyMember(member);
        }

        int people = bundle.partySize();
        printHeader("ROOMS AVAILABLE");
        System.out.println("Rooms for " + people + " people  (" + start + " to " + bundle.getEndDate() + ")");
        System.out.println();
        ArrayList<Accommodation> rooms = resort.availableRooms(start, days, people);
        if (rooms.isEmpty()) {
            err("No rooms that fit. Bundle abandoned - try different dates maybe.");
            return;
        }
        for (int i = 0; i < rooms.size(); i++) {
            System.out.println(rooms.get(i));
        }
        System.out.println();

        String accId = readText("Enter accommodation ID (e.g. AP01): ");
        Accommodation room = resort.findAccommodation(accId);
        if (room == null) {
            err("That ID isn't in the list.");
            return;
        }
        boolean listed = false;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getId().equalsIgnoreCase(accId.trim())) {
                listed = true;
            }
        }
        if (!listed) {
            err("That place isn't available for this stay.");
            return;
        }

        resort.attachRoom(bundle, room);
        System.out.println();
        ok("Accommodation " + room.getId() + " has been attached to the bundle.");
        ok("Bundle created successfully.");
        System.out.println();
        System.out.println(bundle.prettyPrint());
    }

    private void listBundles() {
        printHeader("ALL TRAVEL BUNDLES");
        System.out.println();
        ArrayList<TravelBundle> list = resort.getBundles();
        if (list.isEmpty()) {
            err("No bundles yet. Go make one, it's why we're here.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).prettyPrint());
            System.out.println();
        }
    }

    private void addLiftPass() {
        if (resort.getBundles().isEmpty()) {
            err("No bundles to decorate with lift passes.");
            return;
        }
        listBundles();
        int bundleId = readInt("Enter the bundle ID: ", 1, Integer.MAX_VALUE);
        TravelBundle bundle = resort.findBundle(bundleId);
        if (bundle == null) {
            err("Can't find that bundle.");
            return;
        }

        ArrayList<PersonPick> people = listPeople(bundle);
        int pick = readInt("Select person (1-" + people.size() + "): ", 1, people.size());
        PersonPick person = people.get(pick - 1);

        System.out.println();
        System.out.println("Lift pass type:");
        System.out.println("  1 = Day pass  ($26.00/day)");
        System.out.println("  2 = Season pass  ($200.00 for 30 days)");
        int typeChoice = readInt("Enter your choice (1-2): ", 1, 2);

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
            int extra = readInt("Day passes for " + person.name + " (1-60): ", 1, 60);
            double rawCost = extra * LiftPass.DAY_PRICE;
            if (existing != null && existing.getType() == LiftPassType.DAY) {
                rawCost = (existing.getDays() + extra) * LiftPass.DAY_PRICE;
            }
            if (rawCost >= 180) {
                System.out.println("Heads up: that's close to the $200 season pass. Might be better value.");
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

        ok("Lift pass added. Bundle lift pass total is now $"
                + String.format("%.2f", bundle.getLiftPassTotal()));
    }

    private void addLessons() {
        if (resort.getBundles().isEmpty()) {
            err("No bundles yet, so no one to teach.");
            return;
        }
        int bundleId = readInt("Enter the bundle ID: ", 1, Integer.MAX_VALUE);
        TravelBundle bundle = resort.findBundle(bundleId);
        if (bundle == null) {
            err("Can't find that bundle.");
            return;
        }

        ArrayList<PersonPick> people = listPeople(bundle);
        int pick = readInt("Select person (1-" + people.size() + "): ", 1, people.size());
        PersonPick person = people.get(pick - 1);

        System.out.println();
        int count = readInt("Lessons for " + person.name + "  (" + person.level + ", $"
                + String.format("%.2f", person.level.getLessonPrice()) + " each)  (0-30): ", 0, 30);

        Lesson existing = bundle.findLesson(person.id, person.customer);
        if (existing == null) {
            bundle.getLessons().add(new Lesson(person.id, person.customer, person.name, person.level, count));
        } else {
            // same line gets bumped, e.g. 9 lessons then 2 more -> 11
            existing.addCount(count);
        }

        ok("Lessons added. Bundle lesson total is now $"
                + String.format("%.2f", bundle.getLessonTotal()));
    }

    private void saveBundles() {
        try {
            resort.saveToDatabase();
            ok(resort.getBundles().size() + " bundle(s) saved to the database.");
        } catch (Exception e) {
            err("Could not save: " + e.getMessage());
        }
    }

    private void readBundles() {
        try {
            resort.reloadFromDatabase();
            listBundles();
        } catch (Exception e) {
            err("Could not read the database: " + e.getMessage());
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
        System.out.println();
        System.out.println("People in this bundle:");
        System.out.println();
        for (int i = 0; i < people.size(); i++) {
            PersonPick p = people.get(i);
            String role = p.customer ? "customer" : "family";
            System.out.println((i + 1) + ".  [" + p.id + "]  " + p.name + "  (" + role + ")");
        }
        System.out.println();
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
                System.out.println("Use yyyy-MM-dd  (e.g. 2026-09-01)");
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
                    System.out.println("Please enter a number from " + min + " to " + max + ".");
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
