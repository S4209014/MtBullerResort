package com.resort;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class BundleDatabase {
    private String url;

    public BundleDatabase(String fileName) {
        this.url = "jdbc:sqlite:" + fileName;
    }

    public void save(ArrayList<TravelBundle> bundles) throws Exception {
        Connection conn = DriverManager.getConnection(url);
        conn.setAutoCommit(false);
        makeTables(conn);

        Statement wipe = conn.createStatement();
        wipe.executeUpdate("DELETE FROM lessons");
        wipe.executeUpdate("DELETE FROM lift_passes");
        wipe.executeUpdate("DELETE FROM family");
        wipe.executeUpdate("DELETE FROM bundles");
        wipe.close();

        PreparedStatement bundleStmt = conn.prepareStatement(
                "INSERT INTO bundles(id, customer_id, customer_name, contact, level, start_date, days, acc_id) VALUES(?,?,?,?,?,?,?,?)");
        PreparedStatement familyStmt = conn.prepareStatement(
                "INSERT INTO family(bundle_id, member_id, name, level) VALUES(?,?,?,?)");
        PreparedStatement passStmt = conn.prepareStatement(
                "INSERT INTO lift_passes(bundle_id, person_id, is_customer, name, pass_type, days) VALUES(?,?,?,?,?,?)");
        PreparedStatement lessonStmt = conn.prepareStatement(
                "INSERT INTO lessons(bundle_id, person_id, is_customer, name, level, lesson_count) VALUES(?,?,?,?,?,?)");

        for (int i = 0; i < bundles.size(); i++) {
            TravelBundle bundle = bundles.get(i);
            if (bundle.getAccommodation() == null) {
                continue;
            }
            Customer customer = bundle.getCustomer();
            bundleStmt.setInt(1, bundle.getId());
            bundleStmt.setInt(2, customer.getId());
            bundleStmt.setString(3, customer.getName());
            bundleStmt.setString(4, customer.getContact());
            bundleStmt.setString(5, customer.getLevel().name());
            bundleStmt.setString(6, bundle.getStartDate().toString());
            bundleStmt.setInt(7, bundle.getDays());
            bundleStmt.setString(8, bundle.getAccommodation().getId());
            bundleStmt.executeUpdate();

            for (int f = 0; f < bundle.getFamily().size(); f++) {
                FamilyMember member = bundle.getFamily().get(f);
                familyStmt.setInt(1, bundle.getId());
                familyStmt.setInt(2, member.getId());
                familyStmt.setString(3, member.getName());
                familyStmt.setString(4, member.getLevel().name());
                familyStmt.executeUpdate();
            }

            for (int p = 0; p < bundle.getLiftPasses().size(); p++) {
                LiftPass pass = bundle.getLiftPasses().get(p);
                passStmt.setInt(1, bundle.getId());
                passStmt.setInt(2, pass.getPersonId());
                passStmt.setInt(3, pass.isCustomer() ? 1 : 0);
                passStmt.setString(4, pass.getName());
                passStmt.setString(5, pass.getType().name());
                passStmt.setInt(6, pass.getDays());
                passStmt.executeUpdate();
            }

            for (int l = 0; l < bundle.getLessons().size(); l++) {
                Lesson lesson = bundle.getLessons().get(l);
                lessonStmt.setInt(1, bundle.getId());
                lessonStmt.setInt(2, lesson.getPersonId());
                lessonStmt.setInt(3, lesson.isCustomer() ? 1 : 0);
                lessonStmt.setString(4, lesson.getName());
                lessonStmt.setString(5, lesson.getLevel().name());
                lessonStmt.setInt(6, lesson.getCount());
                lessonStmt.executeUpdate();
            }
        }

        bundleStmt.close();
        familyStmt.close();
        passStmt.close();
        lessonStmt.close();
        conn.commit();
        conn.close();
    }

    // returns true if we actually pulled some bundles out
    public boolean loadInto(MtBullerResort resort) throws Exception {
        Connection conn = DriverManager.getConnection(url);
        makeTables(conn);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM bundles ORDER BY id");
        boolean any = false;

        while (rs.next()) {
            any = true;
            int customerId = rs.getInt("customer_id");
            Customer customer = resort.findCustomer(customerId);
            if (customer == null) {
                customer = new Customer(customerId, rs.getString("customer_name"),
                        rs.getString("contact"), SkiLevel.valueOf(rs.getString("level")));
                resort.addCustomerFromDb(customer);
            }

            TravelBundle bundle = new TravelBundle(rs.getInt("id"), customer,
                    LocalDate.parse(rs.getString("start_date")), rs.getInt("days"));
            Accommodation room = resort.findAccommodation(rs.getString("acc_id"));
            bundle.setAccommodation(room);

            PreparedStatement fam = conn.prepareStatement("SELECT * FROM family WHERE bundle_id = ?");
            fam.setInt(1, bundle.getId());
            ResultSet famRs = fam.executeQuery();
            while (famRs.next()) {
                FamilyMember member = new FamilyMember(famRs.getInt("member_id"),
                        famRs.getString("name"), SkiLevel.valueOf(famRs.getString("level")));
                bundle.addFamilyMember(member);
            }
            famRs.close();
            fam.close();

            PreparedStatement pass = conn.prepareStatement("SELECT * FROM lift_passes WHERE bundle_id = ?");
            pass.setInt(1, bundle.getId());
            ResultSet passRs = pass.executeQuery();
            while (passRs.next()) {
                bundle.getLiftPasses().add(new LiftPass(
                        passRs.getInt("person_id"),
                        passRs.getInt("is_customer") == 1,
                        passRs.getString("name"),
                        LiftPassType.valueOf(passRs.getString("pass_type")),
                        passRs.getInt("days")));
            }
            passRs.close();
            pass.close();

            PreparedStatement les = conn.prepareStatement("SELECT * FROM lessons WHERE bundle_id = ?");
            les.setInt(1, bundle.getId());
            ResultSet lesRs = les.executeQuery();
            while (lesRs.next()) {
                bundle.getLessons().add(new Lesson(
                        lesRs.getInt("person_id"),
                        lesRs.getInt("is_customer") == 1,
                        lesRs.getString("name"),
                        SkiLevel.valueOf(lesRs.getString("level")),
                        lesRs.getInt("lesson_count")));
            }
            lesRs.close();
            les.close();

            resort.addBundleFromDb(bundle);
        }

        rs.close();
        st.close();
        conn.close();
        return any;
    }

    private void makeTables(Connection conn) throws Exception {
        Statement st = conn.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS bundles ("
                + "id INTEGER PRIMARY KEY,"
                + "customer_id INTEGER,"
                + "customer_name TEXT,"
                + "contact TEXT,"
                + "level TEXT,"
                + "start_date TEXT,"
                + "days INTEGER,"
                + "acc_id TEXT)");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS family ("
                + "bundle_id INTEGER,"
                + "member_id INTEGER,"
                + "name TEXT,"
                + "level TEXT)");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS lift_passes ("
                + "bundle_id INTEGER,"
                + "person_id INTEGER,"
                + "is_customer INTEGER,"
                + "name TEXT,"
                + "pass_type TEXT,"
                + "days INTEGER)");
        st.executeUpdate("CREATE TABLE IF NOT EXISTS lessons ("
                + "bundle_id INTEGER,"
                + "person_id INTEGER,"
                + "is_customer INTEGER,"
                + "name TEXT,"
                + "level TEXT,"
                + "lesson_count INTEGER)");
        st.close();
    }
}
