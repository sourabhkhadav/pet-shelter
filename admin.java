package pet_hosting;

import java.sql.*;
import java.util.Scanner;

public class admin {

    public static void adminn() throws Exception {
        Scanner sc = new Scanner(System.in);
        int attempts = 0;
        boolean loggedIn = false;

        while (attempts < 3) {
            String inputUsername = validation.getSafeInput(sc, "Enter admin name: ");
            if (inputUsername.equalsIgnoreCase("exit")) return;
            if (!validation.isValidName(inputUsername)) {
                System.out.println("Invalid name format.");
                attempts++;
                continue;
            }

            String inputPassword = validation.getSafeInput(sc, "Enter admin password: ");
            if (inputPassword.equalsIgnoreCase("exit")) return;
            if (!validation.isValidPassword(inputPassword)) {
                System.out.println("Invalid password format.");
                attempts++;
                continue;
            }

            try (
                Connection con = jdbc.Getconnection();
                PreparedStatement ps = con.prepareStatement("SELECT * FROM admin WHERE admin_username = ? AND admin_password = ?");
            ) {
                ps.setString(1, inputUsername);
                ps.setString(2, inputPassword);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    loggedIn = true;
                    System.out.println("Admin login successful.");
                    System.out.println("Welcome, " + rs.getString("admin_username"));
                    rs.close();
                    break;
                } else {
                    System.out.println("Incorrect username or password.");
                    attempts++;
                }
                rs.close();
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                return;
            }
        }

        if (!loggedIn) {
            System.out.println("-----  failed attempts  to main menu. -----");
            return;
        }

        
        while (true) {
            System.out.println("""
                -------------------------------------------
                --- Admin Menu ---
                1. View all users
                2. View all pets
                3. View all listings
                4. View all bookings
                5. Block user
                6. Unblock user
                7. Approve/Reject listings
                8. Get top host
                9. Exit
                -------------------------------------------
            """);

            String choiceInput = validation.getSafeInput(sc, "Enter your choice: ");
            if (choiceInput.equalsIgnoreCase("exit")) return;
            if (!validation.isValidInteger(choiceInput)) {
                System.out.println("Invalid input. Please enter a number between 1-9.");
                continue;
            }

            int choice = Integer.parseInt(choiceInput);
            switch (choice) {
                case 1 -> viewalluser();
                case 2 -> viewallpets();
                case 3 -> viewalllistning();
                case 4 -> viewAllBookings();
                case 5 -> blockuser();
                case 6 -> unblockuser();
                case 7 -> approveRejectListingsByAdmin();
                case 8 -> tophost();
                case 9 -> {
                    System.out.println("Exiting admin menu...");
                    return;
                }
                default -> System.out.println("Invalid choice. Enter a number between 1-9.");
            }
        }
    }

    public static void viewalluser() throws Exception {
        try (
            Connection con = jdbc.Getconnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM user");
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("user_id"));
                System.out.println("Name: " + rs.getString("user_name"));
                System.out.println("Email: " + rs.getString("user_email"));
                System.out.println("Role: " + rs.getString("user_roll"));
                System.out.println("Status: " + rs.getString("user_status"));
                System.out.println("--------------------------------");
            }
        }
    }

    public static void viewallpets() throws Exception {
        try (
            Connection con = jdbc.Getconnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM pets");
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                System.out.println("Pet ID     : " + rs.getInt("pet_id"));
                System.out.println("Owner ID   : " + rs.getInt("owner_id"));
                System.out.println("Pet Name   : " + rs.getString("pet_name"));
                System.out.println("Pet Type   : " + rs.getString("pet_type"));
                System.out.println("Pet Breed  : " + rs.getString("pet_breed"));
                System.out.println("Pet Size   : " + rs.getString("pet_size"));
                System.out.println("------------------------");
            }
        }
    }

    public static void viewalllistning() throws Exception {
        try (
            Connection con = jdbc.Getconnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM host_listings");
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                System.out.println("Listing ID         : " + rs.getInt("listing_id"));
                System.out.println("Host ID            : " + rs.getInt("host_id"));
                System.out.println("Available From     : " + rs.getDate("available_from"));
                System.out.println("Available To       : " + rs.getDate("available_to"));
                System.out.println("Pet Size Allowed   : " + rs.getString("pet_size_allowed"));
                System.out.println("Price Per Day      : " + rs.getDouble("price_per_day"));
                System.out.println("Host Availability  : " + rs.getString("host_availability"));
                System.out.println("---------------------------------------------------");
            }
        }
    }

    public static void viewAllBookings() throws Exception {
        try (
            Connection con = jdbc.Getconnection();
            PreparedStatement ps = con.prepareStatement("""
                SELECT b.booking_id, b.pet_id, b.owner_id, b.host_id,
                       b.start_date, b.end_date, b.status, b.total_price,
                       host.user_name AS host_name,
                       owner.user_name AS owner_name
                FROM bookings b
                JOIN user host ON b.host_id = host.user_id
                JOIN user owner ON b.owner_id = owner.user_id
            """);
            ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                System.out.println("Booking ID   : " + rs.getInt("booking_id"));
                System.out.println("Pet ID       : " + rs.getInt("pet_id"));
                System.out.println("Owner Name   : " + rs.getString("owner_name"));
                System.out.println("Host Name    : " + rs.getString("host_name"));
                System.out.println("Start Date   : " + rs.getString("start_date"));
                System.out.println("End Date     : " + rs.getString("end_date"));
                System.out.println("Status       : " + rs.getString("status"));
                System.out.println("Total Price  : " + rs.getDouble("total_price"));
                System.out.println("----------------------------------------");
            }
        }
    }

    public static void blockuser() throws Exception {
        Scanner sc = new Scanner(System.in);
        try (
            Connection con = jdbc.Getconnection();
            PreparedStatement ps = con.prepareStatement("SELECT user_id, user_name FROM user WHERE user_status = 'active'");
            ResultSet rs = ps.executeQuery()
        ) {
            System.out.println("Active Users:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("user_id") + " | Name: " + rs.getString("user_name"));
            }

            String input = validation.getSafeInput(sc, "Enter user ID to BLOCK ( 'exit' to cancel): ");
            if (input.equalsIgnoreCase("exit")) return;
            if (!validation.isValidInteger(input)) {
                System.out.println("Invalid input.  cancelled.");
                return;
            }

            int userId = Integer.parseInt(input);
            String confirm = validation.getSafeInput(sc, "Are you sure you want to BLOCK this user? (Y/N): ");
            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("Operation cancelled.");
                return;
            }

            PreparedStatement update = con.prepareStatement("UPDATE user SET user_status = 'blocked' WHERE user_id = ?");
            update.setInt(1, userId);
            int i = update.executeUpdate();
            update.close();

            System.out.println(i > 0 ? "User BLOCKED successfully." : "User not found or already blocked.");
        }
    }

    public static void unblockuser() throws Exception {
        Scanner sc = new Scanner(System.in);
        try (
            Connection con = jdbc.Getconnection();
            PreparedStatement ps = con.prepareStatement("SELECT user_id, user_name FROM user WHERE user_status = 'blocked'");
            ResultSet rs = ps.executeQuery()
        ) {
            System.out.println("Blocked Users:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("user_id") + " | Name: " + rs.getString("user_name"));
            }

            String input = validation.getSafeInput(sc, "Enter user ID to UNBLOCK (or type 'exit' to cancel): ");
            if (input.equalsIgnoreCase("exit")) return;
            if (!validation.isValidInteger(input)) {
                System.out.println("Invalid input. Operation cancelled.");
                return;
            }

            int userId = Integer.parseInt(input);
            String confirm = validation.getSafeInput(sc, "Are you sure you want to UNBLOCK this user? (Y/N): ");
            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("Operation cancelled.");
                return;
            }

            PreparedStatement update = con.prepareStatement("UPDATE user SET user_status = 'active' WHERE user_id = ?");
            update.setInt(1, userId);
            int i = update.executeUpdate();
            update.close();

            System.out.println(i > 0 ? "User UNBLOCKED successfully." : "User not found or already active.");
        }
    }

    public static void approveRejectListingsByAdmin() throws Exception {
        Scanner sc = new Scanner(System.in);
        try (
            Connection con = jdbc.Getconnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM host_listings WHERE host_availability = 'not-available'");
            ResultSet rs = ps.executeQuery()
        ) {
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("----------------------------------------------------------------------");
                System.out.println("Listing ID        : " + rs.getInt("listing_id"));
                System.out.println("Host ID           : " + rs.getInt("host_id"));
                System.out.println("Start Date        : " + rs.getDate("available_from"));
                System.out.println("End Date          : " + rs.getDate("available_to"));
                System.out.println("Pet Size Allowed  : " + rs.getString("pet_size_allowed"));
                System.out.println("Price per Day     : " + rs.getDouble("price_per_day"));
                System.out.println("Status            : " + rs.getString("host_availability"));
                System.out.println("------------------------------------------------------------------------");
            }

            if (count == 0) {
                System.out.println("No pending listings found.");
                return;
            }

            String listingid = validation.getSafeInput(sc, "Enter Listing ID to Approve/Reject (or type 'exit' to cancel): ");
            if (listingid.equalsIgnoreCase("exit")) return;
            if (!validation.isValidInteger(listingid)) {
                System.out.println("Invalid listing ID. Returning...");
                return;
            }

            int listingId = Integer.parseInt(listingid);
            String decision = validation.getSafeInput(sc, "Enter 'A' to Approve or 'R' to Reject (or type 'exit' to cancel): ").toUpperCase();
            if (decision.equalsIgnoreCase("exit")) return;

            String new_status = switch (decision) {
                case "A" -> "available";
                case "R" -> "rejected";
                default -> null;
            };

            if (new_status == null) {
                System.out.println("Invalid input. Returning...");
                return;
            }

            PreparedStatement uPs = con.prepareStatement("UPDATE host_listings SET host_availability = ? WHERE listing_id = ?");
            uPs.setString(1, new_status);
            uPs.setInt(2, listingId);
            int rows = uPs.executeUpdate();
            uPs.close();

            if (rows > 0)
                System.out.println("Listing ID " + listingId + " has been " + new_status + " successfully.");
            else
                System.out.println("Update failed. Listing ID might not exist.");
        }
    }

    public static void tophost() throws Exception {
        try (
            Connection con = jdbc.Getconnection();
            PreparedStatement ps1 = con.prepareStatement("SELECT host_id, COUNT(*) AS total_bookings FROM bookings GROUP BY host_id ORDER BY total_bookings DESC LIMIT 1");
            ResultSet rs1 = ps1.executeQuery()
        ) {
            if (rs1.next()) {
                int topHostId = rs1.getInt("host_id");
                int totalBookings = rs1.getInt("total_bookings");

                PreparedStatement ps2 = con.prepareStatement("SELECT SUM(total_price) AS total_revenue FROM bookings WHERE host_id = ? AND status = 'Accepted'");
                ps2.setInt(1, topHostId);
                ResultSet rs2 = ps2.executeQuery();

                double totalRevenue = rs2.next() ? rs2.getDouble("total_revenue") :0;

                System.out.println("Top Host:");
                System.out.println("Host ID        : " + topHostId);
                System.out.println("Total Bookings : " + totalBookings);
                System.out.println("Total Revenue  : " + totalRevenue);

                rs2.close();
                ps2.close();
            } else {
                System.out.println("No host bookings found.");
            }
        }
    }
}
