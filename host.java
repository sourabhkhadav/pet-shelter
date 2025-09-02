package pet_hosting;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class host {

   public static void signup_user() throws Exception {
    Scanner sc = new Scanner(System.in);
    int signupAttempts = 0;

    while (signupAttempts < 3) {
        try {
            String username = "", email = "", password = "", phone = "";

          
            for (int i = 0; i < 3; i++) {
                username = validation.getSafeInput(sc, "Enter Host Name: ");
                if (username.equalsIgnoreCase("exit")) return;
                if (validation.isValidName(username)) break;
                System.out.println("Invalid name. Try again.");
                if (i == 2) return;
            }

         
            for (int i = 0; i < 3; i++) {
                email = validation.getSafeInput(sc, "Enter Gmail Address: ");
                if (email.equalsIgnoreCase("exit")) return;

                if (!validation.isValidEmail(email)) {
                    System.out.println("Invalid email. Must be a valid Gmail.");
                    if (i == 2) return;
                    continue;
                }

               
                Connection checkCon = jdbc.Getconnection();
                PreparedStatement checkPs = checkCon.prepareStatement("SELECT user_id FROM user WHERE user_email = ?");
                checkPs.setString(1, email);
                ResultSet checkRs = checkPs.executeQuery();
                if (checkRs.next()) {
                    System.out.println("Email already registered. Please login or try a different email.");
                    checkRs.close();
                    checkPs.close();
                    checkCon.close();
                    return;
                }
                checkRs.close();
                checkPs.close();
                checkCon.close();
                break;
            }

            
            for (int i = 0; i < 3; i++) {
                password = validation.getSafeInput(sc, "Enter Password (Min 6 chars, 2 digits): ");
                if (password.equalsIgnoreCase("exit")) return;
                if (validation.isValidPassword(password)) break;
                System.out.println("Invalid password. Must contain min 6 chars, at least 2 digits.");
                if (i == 2) return;
            }

          
            for (int i = 0; i < 3; i++) {
                phone = validation.getSafeInput(sc, "Enter Phone Number (10 digits): ");
                if (phone.equalsIgnoreCase("exit")) return;
                if (validation.isValidPhoneNumber(phone)) break;
                System.out.println("Invalid phone. Must be a 10-digit number starting with 6-9.");
                if (i == 2) return;
            }

            Connection con = jdbc.Getconnection();
            String query = "INSERT INTO user(user_name, user_email, user_password, user_phone, user_roll, user_status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, phone);
            ps.setString(5, "host");
            ps.setString(6, "active");

            int result = ps.executeUpdate();
            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int hostId = rs.getInt(1);
                    System.out.println("Host registration successful! Your ID: " + hostId);
                    rs.close();
                    ps.close();
                    con.close();
                    showHostMenu(hostId);
                    return;
                }
            } else {
                System.out.println("Registration failed. Try again.");
            }

            ps.close();
            con.close();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Email already registered. Try logging in.");
        } catch (Exception e) {
            System.out.println("Signup failed: " + e.getMessage());
        }

        signupAttempts++;
        if (signupAttempts == 3) {
            System.out.println("Signup failed 3 times. Returning to main menu.");
            return;
        }
    }
}



    public static void loginuser() throws Exception {
        Scanner sc = new Scanner(System.in);
        int attempts = 0;

        while (attempts < 3) {
            String email = "", password = "";

            for (int i = 0; i < 3; i++) {
                email = validation.getSafeInput(sc, "Enter Gmail Address: ");
                if (email.equalsIgnoreCase("exit")) return;
                if (validation.isValidEmail(email)) break;
                System.out.println("Invalid Gmail. Try again.");
                if (i == 2) return;
            }

            for (int i = 0; i < 3; i++) {
                password = validation.getSafeInput(sc, "Enter Password: ");
                if (password.equalsIgnoreCase("exit")) return;
                if (validation.isValidPassword(password)) break;
                System.out.println("Invalid Password Format.");
                if (i == 2) return;
            }

            Connection con = jdbc.Getconnection();
            String query = "SELECT * FROM user WHERE user_email = ? AND user_password = ? AND user_roll = 'host' AND user_status = 'active'";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int hostId = rs.getInt("user_id");
                String name = rs.getString("user_name");
                System.out.println("Login successful! Welcome, " + name);
                showHostMenu(hostId);
                break;
            } else {
                System.out.println("Login failed or account is blocked.");
                attempts++;
            }

            rs.close();
            ps.close();
            con.close();
        }

        if (attempts >= 3) {
            System.out.println("Too many failed attempts. Returning to main menu...");
        }
    }

    public static void showHostMenu(int hostId) throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nHost Menu:");
            System.out.println("1. Accept or Reject Bookings");
            System.out.println("2. Create Shelter Listing");
            System.out.println("3. View My Shelter Listings");
            System.out.println("4. Delete Shelter");
            System.out.println("5. View All Bookings");
            System.out.println("6. Exit");

            String choice = validation.getSafeInput(sc, "Enter choice: ");
            if (choice.equalsIgnoreCase("exit")) return;
            if (!validation.isValidInteger(choice)) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            switch (Integer.parseInt(choice)) {
                case 1 -> approvereject(hostId);
                case 2 -> createHostListing(hostId);
                case 3 -> viewHostListings(hostId);
                case 4 -> deleteHostListing(hostId);
                case 5 -> viewAllBookings(hostId);
                case 6 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public static void approvereject(int host_id) throws Exception {
        Connection con = jdbc.Getconnection();
        PreparedStatement ps = con.prepareStatement(
            "SELECT b.booking_id, p.pet_name, b.start_date, b.end_date, b.total_price " +
            "FROM bookings b JOIN pets p ON b.pet_id = p.pet_id " +
            "WHERE b.host_id = ? AND b.status = 'pending'");
        ps.setInt(1, host_id);

        ResultSet rs = ps.executeQuery();
        int count = 0;

        System.out.println("\nPending Bookings:");
        while (rs.next()) {
            count++;
            System.out.println("Booking ID    : " + rs.getInt("booking_id"));
            System.out.println("Pet Name      : " + rs.getString("pet_name"));
            System.out.println("Start Date    : " + rs.getString("start_date"));
            System.out.println("End Date      : " + rs.getString("end_date"));
            System.out.println("Total Price  : " + rs.getDouble("total_price"));
            System.out.println("-----------------------------------");
        }

        if (count == 0) {
            System.out.println("No pending bookings available.");
            rs.close(); ps.close(); con.close(); return;
        }

        Scanner sc = new Scanner(System.in);
        int booking_id = -1;

        for (int i = 0; i < 3; i++) {
            String bookingIdStr = validation.getSafeInput(sc, "Enter Booking ID to respond: ");
            if (bookingIdStr.equalsIgnoreCase("exit")) return;
            if (!validation.isValidInteger(bookingIdStr)) {
                System.out.println("Invalid input. Enter ID.");
                if (i == 2) return;
                continue;
            }

            booking_id = Integer.parseInt(bookingIdStr);
            PreparedStatement check = con.prepareStatement(
                "SELECT booking_id FROM bookings WHERE booking_id = ? AND host_id = ? AND status = 'pending'");
            check.setInt(1, booking_id);
            check.setInt(2, host_id);
            ResultSet rsCheck = check.executeQuery();

            if (rsCheck.next()) {
                rsCheck.close(); check.close(); break;
            } else {
                System.out.println("Invalid booking ID or already processed.");
                rsCheck.close(); check.close();
                if (i == 2) return;
            }
        }

        String decision = "", new_status = "";
        for (int i = 0; i < 3; i++) {
            decision = validation.getSafeInput(sc, "Enter 'accept' to Approve or 'reject' to Reject: ").toLowerCase();
            if (decision.equalsIgnoreCase("exit")) return;
            if (decision.equals("accept") || decision.equals("reject")) {
                new_status = decision.equals("accept") ? "accepted" : "rejected";
                break;
            } else {
                System.out.println("Invalid input. Try again.");
                if (i == 2) return;
            }
        }

        PreparedStatement update = con.prepareStatement("UPDATE bookings SET status = ? WHERE booking_id = ?");
        update.setString(1, new_status);
        update.setInt(2, booking_id);
        int r = update.executeUpdate();

        System.out.println(r > 0
            ? "Booking ID " + booking_id + " has been " + new_status + " successfully."
            : "Update failed.");

        rs.close(); ps.close(); update.close(); con.close();
    }

    public static void createHostListing(int hostId) throws Exception {
        Scanner sc = new Scanner(System.in);

       

     LocalDate start = null, end = null;
    String startDate = "", endDate = "", size = "";;


for (int i = 0; i < 3; i++) {
    startDate = validation.getSafeInput(sc, "Enter Start Date (YYYY-MM-DD): ");
    if (startDate.equalsIgnoreCase("exit")) return;

    if (validation.isFutureDate(startDate)) {
        start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        break;
    }

    System.out.println("Invalid start date.  future  date and format YYYY-MM-DD.");
    if (i == 2) return;
}


for (int i = 0; i < 3; i++) {
    endDate = validation.getSafeInput(sc, "Enter End Date (YYYY-MM-DD): ");
    if (endDate.equalsIgnoreCase("exit")) return;

    if (validation.isFutureDate(endDate)) {
        end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        if (end.isAfter(start)) {
            break;
        } else {
            System.out.println("End date must be after start date.");
        }
    } else {
        System.out.println("Invalid end date.  future date and format YYYY-MM-DD.");
    }

    if (i == 2) return;
}


        for (int i = 0; i < 3; i++) {
            size = validation.getSafeInput(sc, "Enter Pet Size (small/medium/large): ").toLowerCase();
            if (size.equalsIgnoreCase("exit")) return;
            if (size.equals("small") || size.equals("medium") || size.equals("large")) break;
            System.out.println("Invalid size.");
        }

        System.out.print("Enter Price per Day: ");
        double price = sc.nextDouble();

        Connection con = jdbc.Getconnection();
        String sql = "INSERT INTO host_listings (host_id, available_from, available_to, pet_size_allowed, price_per_day, host_availability) VALUES (?, ?, ?, ?, ?, 'not-available')";
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, hostId);
        ps.setString(2, startDate);
        ps.setString(3, endDate);
        ps.setString(4, size);
        ps.setDouble(5, price);

        int rows = ps.executeUpdate();
        if (rows > 0) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Listing created successfully! Listing ID: " + rs.getInt(1));
            }
        } else {
            System.out.println("Failed to create listing.");
        }

        ps.close(); con.close();
    }

    public static void viewHostListings(int hostId) throws Exception {
        Connection con = jdbc.Getconnection();
        String sql = "SELECT listing_id, available_from, available_to, pet_size_allowed, price_per_day, host_availability FROM host_listings WHERE host_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, hostId);
        ResultSet rs = ps.executeQuery();
        boolean found = false;

        while (rs.next()) {
            found = true;
            System.out.println("Listing ID         : " + rs.getInt("listing_id"));
            System.out.println("Available From     : " + rs.getString("available_from"));
            System.out.println("Available To       : " + rs.getString("available_to"));
            System.out.println("Pet Size Allowed   : " + rs.getString("pet_size_allowed"));
            System.out.println("Price Per Day ₹    : " + rs.getDouble("price_per_day"));
            System.out.println("Host Availability  : " + rs.getString("host_availability"));
            System.out.println("---------------------------------------------");
        }

        if (!found) {
            System.out.println("You have not created any shelter listings yet.");
        }

        rs.close(); ps.close(); con.close();
    }

    public static void deleteHostListing(int hostId) throws Exception {
        Connection con = jdbc.Getconnection();
        Scanner sc = new Scanner(System.in);

        PreparedStatement ps = con.prepareStatement(
            "SELECT listing_id, available_from, available_to, pet_size_allowed, price_per_day, host_availability FROM host_listings WHERE host_id = ?"
        );
        ps.setInt(1, hostId);
        ResultSet rs = ps.executeQuery();

        boolean hasListings = false;
        System.out.println("Your Shelter Listings:");
        while (rs.next()) {
            hasListings = true;
            System.out.println("Listing ID       : " + rs.getInt("listing_id"));
            System.out.println("Available From   : " + rs.getString("available_from"));
            System.out.println("Available To     : " + rs.getString("available_to"));
            System.out.println("Pet Size Allowed : " + rs.getString("pet_size_allowed"));
            System.out.println("Price/Day ₹      : " + rs.getDouble("price_per_day"));
            System.out.println("Availability     : " + rs.getString("host_availability"));
            System.out.println("--------------------------------------------");
        }

        if (!hasListings) {
            System.out.println("You have no listings to delete.");
            return;
        }

        for (int i = 0; i < 3; i++) {
            String listingId = validation.getSafeInput(sc, "Enter Listing ID to delete: ");
            if (listingId.equalsIgnoreCase("exit")) return;
            if (validation.isValidInteger(listingId)) {
                int lid = Integer.parseInt(listingId);
                PreparedStatement delete = con.prepareStatement("DELETE FROM host_listings WHERE listing_id = ? AND host_id = ?");
                delete.setInt(1, lid);
                delete.setInt(2, hostId);
                int rows = delete.executeUpdate();
                System.out.println(rows > 0 ? "Listing deleted successfully." : "Invalid Listing ID or not your listing.");
                delete.close();
                break;
            } else {
                System.out.println("Invalid ID. Try again.");
                if (i == 2) return;
            }
        }

        rs.close(); ps.close(); con.close();
    }

    public static void viewAllBookings(int hostId) throws Exception {
        Connection con = jdbc.Getconnection();
        PreparedStatement ps = con.prepareStatement("""
            SELECT b.booking_id, b.pet_id, b.host_id, b.status,
                   host.user_name AS host_name,
                   owner.user_name AS owner_name
            FROM bookings b
            JOIN user host ON b.host_id = host.user_id
            JOIN user owner ON b.owner_id = owner.user_id
            WHERE b.host_id = ?
        """);

        ps.setInt(1, hostId);
        ResultSet rs = ps.executeQuery();

        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("Booking ID   : " + rs.getInt("booking_id"));
            System.out.println("Pet ID       : " + rs.getInt("pet_id"));
            System.out.println("Owner Name   : " + rs.getString("owner_name"));
            System.out.println("Host ID      : " + rs.getInt("host_id"));
            System.out.println("Host Name    : " + rs.getString("host_name"));
            System.out.println("Status       : " + rs.getString("status"));
            System.out.println("----------------------------------------");
        }

        if (!found) {
            System.out.println("No bookings found for you.");
        }

        rs.close(); ps.close(); con.close();
    }
}
