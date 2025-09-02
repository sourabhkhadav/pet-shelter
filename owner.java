package pet_hosting;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class owner {
    public static int loggedInOwnerId = -1;

    public static void signup_user() throws Exception {
        Scanner sc = new Scanner(System.in);
        int signupAttempts = 0;

        while (signupAttempts < 3) {
            try {
                String username = "", userEmail = "", userPassword = "", userPhone = "";

                System.out.println("Enter Owner Name -- ");
                for (int i = 0; i < 3; i++) {
                    username = sc.nextLine().trim();
                    if (username.equalsIgnoreCase("exit"))
                        return;
                    if (validation.isValidName(username))
                        break;
                    if (i == 2)
                        return;
                    System.out.println("Invalid name. Try again:");
                }

                System.out.println("Enter Email --");
                for (int i = 0; i < 3; i++) {
                    userEmail = sc.nextLine().trim();
                    if (userEmail.equalsIgnoreCase("exit"))
                        return;
                    if (validation.isValidEmail(userEmail))
                        break;
                    if (i == 2)
                        return;
                    System.out.println("Invalid email. Try again:");
                }

                System.out.println("Enter Password -- ");
                for (int i = 0; i < 3; i++) {
                    userPassword = sc.nextLine().trim();
                    if (userPassword.equalsIgnoreCase("exit"))
                        return;
                    if (validation.isValidPassword(userPassword))
                        break;
                    if (i == 2)
                        return;
                    System.out.println("Invalid password. Try again:");
                }

                System.out.println("Enter Phone Number -- ");
                for (int i = 0; i < 3; i++) {
                    userPhone = sc.nextLine().trim();
                    if (userPhone.equalsIgnoreCase("exit"))
                        return;
                    if (validation.isValidPhoneNumber(userPhone))
                        break;
                    if (i == 2)
                        return;
                    System.out.println("Invalid phone. Try again:");
                }

                String roll = "owner";
                String status = "active";
                Connection con = jdbc.Getconnection();
                String sql = "INSERT INTO user (user_name, user_email, user_password, user_phone, user_status, user_roll) VALUES (?, ?, ?, ?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, userEmail);
                ps.setString(3, userPassword);
                ps.setString(4, userPhone);
                ps.setString(5, status);
                ps.setString(6, roll);

                int i = ps.executeUpdate();

                if (i > 0) {
                    PreparedStatement psId = con
                            .prepareStatement("SELECT user_id FROM user WHERE user_name = ? AND user_email = ?");
                    psId.setString(1, username);
                    psId.setString(2, userEmail);
                    ResultSet rs = psId.executeQuery();
                    if (rs.next()) {
                        loggedInOwnerId = rs.getInt("user_id");
                    }
                    psId.close();
                    System.out.println("Registration successful!");
                    ps.close();
                    con.close();
                    ownerMenu();
                    return;
                } else {
                    System.out.println("Something went wrong.");
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
        int loginAttempts = 0;

        while (loginAttempts < 3) {
            try {
                String usemail = "", password = "";

                System.out.println("Enter Owner email -- ");
                for (int i = 0; i < 3; i++) {
                    usemail = sc.nextLine().trim();
                    if (usemail.equalsIgnoreCase("exit"))
                        return;
                    if (validation.isValidEmail(usemail))
                        break;
                    if (i == 2)
                        return;
                    System.out.println("Invalid email. Try again:");
                }

                System.out.println("Enter Password -- ");
                for (int i = 0; i < 3; i++) {
                    password = sc.nextLine().trim();
                    if (password.equalsIgnoreCase("exit"))
                        return;
                    if (validation.isValidPassword(password))
                        break;
                    if (i == 2)
                        return;
                    System.out.println("Invalid password. Try again:");
                }
                String role = "owner";

                Connection con = jdbc.Getconnection();
                String sql = "SELECT user_id, user_name, user_roll FROM user WHERE user_email = ? AND user_password = ?and user_roll =? AND user_status='active'";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, usemail);
                ps.setString(2, password);
                ps.setString(3, role);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    loggedInOwnerId = rs.getInt("user_id");
                    System.out.println("Owner login successful!");
                    System.out.println(
                            "Welcome, " + rs.getString("user_name") + " (Role: " + rs.getString("user_roll") + ")");
                    ps.close();
                    con.close();
                    ownerMenu();
                    return;
                } else {
                    System.out.println("Invalid login or user may be blocked.");
                }

                ps.close();
                con.close();
            } catch (Exception e) {
                System.out.println("Login failed: " + e.getMessage());
            }

            loginAttempts++;
            if (loginAttempts == 3) {
                System.out.println("Login failed 3 times. Returning to main menu.");
                return;
            }
        }
    }

    public static void ownerMenu() throws Exception {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nOwner Actions:");
            System.out.println("1. Add Pet");
            System.out.println("2. View Available Hosts");
            System.out.println("3. Booking");
            System.out.println("4. View and Cancel Bookings");
            System.out.println("5. View My Pets");
            System.out.println("6. Delete Pet");
            System.out.println("7. Exit");

            String input = validation.getSafeInput(sc, "Enter your choice: ");
            if (!validation.isValidInteger(input)) {
                System.out.println("Invalid input. Try again.");
                continue;
            }
            int choice = Integer.parseInt(input);

            switch (choice) {
                case 1 -> addpet();
                case 2 -> viewAvailableHostsByDateAndSize();
                case 3 -> bookShelter();
                case 4 -> viewandcancel();
                case 5 -> viewMyPets();
                case 6 -> deleteMyPet();
                case 7 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public static void addpet() throws Exception {
        Scanner sc = new Scanner(System.in);

        String pet_name;
        while (true) {
            pet_name = validation.getSafeInput(sc, "Enter pet name: ");
            if (pet_name.equalsIgnoreCase("exit"))
                return;
            if (validation.isValidName(pet_name))
                break;
            System.out.println("Try again. Please enter a valid name.");
        }

        String pet_type;
        while (true) {
            pet_type = validation.getSafeInput(sc, "Enter pet type: ");
            if (pet_type.equalsIgnoreCase("exit"))
                return;
            if (validation.isValidName(pet_type))
                break;
            System.out.println("Try again. Please enter a valid pet type:");
        }

        String pet_breed;
        while (true) {
            pet_breed = validation.getSafeInput(sc, "Enter pet breed: ");
            if (pet_breed.equalsIgnoreCase("exit"))
                return;
            if (validation.isValidName(pet_breed))
                break;
            System.out.println("Try again. Please enter a valid pet breed:");
        }

        String pet_size;
        while (true) {
            pet_size = validation.getSafeInput(sc, "Enter pet size (small / medium / large): ").toLowerCase();
            if (pet_size.equalsIgnoreCase("exit"))
                return;
            if (pet_size.equals("small") || pet_size.equals("medium") || pet_size.equals("large"))
                break;
            System.out.println("Invalid size. Enter 'small', 'medium', or 'large':");
        }

        Connection con = jdbc.Getconnection();
        String query = "INSERT INTO pets(owner_id, pet_name, pet_type, pet_breed, pet_size) VALUES(?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, loggedInOwnerId);
        ps.setString(2, pet_name);
        ps.setString(3, pet_type);
        ps.setString(4, pet_breed);
        ps.setString(5, pet_size);

        int i = ps.executeUpdate();
        System.out.println(i > 0 ? "Pet registered successfully." : "Failed to register pet.");

        ps.close();
        con.close();
    }

    public static void bookShelter() throws Exception {
        Connection con = jdbc.Getconnection();
        Scanner sc = new Scanner(System.in);

        PreparedStatement ps1 = con.prepareStatement("SELECT pet_id, pet_name, pet_size FROM pets " +
                "WHERE owner_id = ? AND pet_id NOT IN (SELECT pet_id FROM bookings WHERE status  not IN ('pending', 'approved'))");
        ps1.setInt(1, loggedInOwnerId);
        ResultSet rs1 = ps1.executeQuery();

        System.out.println("\nYour Pets:");
        boolean hasPets = false;
        while (rs1.next()) {
            hasPets = true;
            System.out.println("----------------------------------------");
            System.out.println("Pet ID   : " + rs1.getInt("pet_id"));
            System.out.println("Pet Name : " + rs1.getString("pet_name"));
            System.out.println("Pet Size : " + rs1.getString("pet_size"));
        }
        if (!hasPets) {
            System.out.println("You have no pets registered.");
            con.close();
            return;
        }

        int petId;
        while (true) {
            String petInput = validation.getSafeInput(sc, "Enter Pet ID to book for: ");
            if (petInput.equalsIgnoreCase("exit"))
                return;
            if (!validation.isValidInteger(petInput)) {
                System.out.println("Invalid Pet ID. Try again.");
                continue;
            }
            petId = Integer.parseInt(petInput);
            PreparedStatement psCheck = con.prepareStatement("SELECT * FROM pets WHERE pet_id = ? AND owner_id = ?");
            psCheck.setInt(1, petId);
            psCheck.setInt(2, loggedInOwnerId);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next())
                break;
            else
                System.out.println("Invalid Pet ID. Try again.");
        }

        PreparedStatement ps2 = con.prepareStatement(
                "SELECT hl.host_id, hl.price_per_day, hl.available_from, hl.available_to " +
                        "FROM host_listings hl " +
                        "JOIN pets p ON p.pet_size = hl.pet_size_allowed " +
                        "JOIN user u ON u.user_id = hl.host_id " +
                        "WHERE p.pet_id = ? AND u.user_status != 'blocked'");
        ps2.setInt(1, petId);
        ResultSet rs2 = ps2.executeQuery();

        System.out.println("\nAvailable Hosts:");
        boolean foundHost = false;
        while (rs2.next()) {
            foundHost = true;
            System.out.println("------------------------------------");
            System.out.println("Host ID         : " + rs2.getInt("host_id"));
            System.out.println("Price per Day ₹ : " + rs2.getDouble("price_per_day"));
            System.out.println("Start Date      : " + rs2.getString("available_from"));
            System.out.println("End Date        : " + rs2.getString("available_to"));
        }
        if (!foundHost) {
            System.out.println("No suitable host available for this pet.");
            con.close();
            return;
        }

        int hostId;
        double pricePerDay;
        String startDate, endDate;
        while (true) {
            String hostInput = validation.getSafeInput(sc, "Enter Host ID to book: ");
            if (hostInput.equalsIgnoreCase("exit"))
                return;
            if (!validation.isValidInteger(hostInput)) {
                System.out.println("Invalid Host ID. Try again.");
                continue;
            }
            hostId = Integer.parseInt(hostInput);
            PreparedStatement ps3 = con.prepareStatement(
                    "SELECT price_per_day, available_from, available_to FROM host_listings WHERE host_id = ?");
            ps3.setInt(1, hostId);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                pricePerDay = rs3.getDouble("price_per_day");
                startDate = rs3.getString("available_from");
                endDate = rs3.getString("available_to");
                rs3.close();
                ps3.close();
                break;
            } else {
                System.out.println("Invalid Host ID. Try again.");
            }
        }

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        double totalPrice = pricePerDay * days;

        PreparedStatement ps4 = con.prepareStatement(
                "INSERT INTO bookings (pet_id, owner_id, host_id, start_date, end_date, total_price, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 'pending')");
        ps4.setInt(1, petId);
        ps4.setInt(2, loggedInOwnerId);
        ps4.setInt(3, hostId);
        ps4.setString(4, startDate);
        ps4.setString(5, endDate);
        ps4.setDouble(6, totalPrice);

        int i = ps4.executeUpdate();
        System.out.println(i > 0
                ? "Booking requested! Total ₹" + totalPrice + ". Status: Pending"
                : "Booking failed.");

        ps1.close();
        ps2.close();
        ps4.close();
        con.close();
    }

    public static void viewandcancel() throws Exception {
        Connection con = jdbc.Getconnection();
        Scanner sc = new Scanner(System.in);

        String query = "SELECT b.booking_id, p.pet_name, b.start_date, b.end_date, b.total_price, b.status FROM bookings b JOIN pets p ON b.pet_id = p.pet_id WHERE p.owner_id = ? AND (b.status = 'pending' OR b.status = 'accepted')";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setInt(1, loggedInOwnerId);
        ResultSet rs = ps.executeQuery();

        int count = 0;
        while (rs.next()) {
            count++;
            System.out.println("Booking ID   = " + rs.getInt("booking_id"));
            System.out.println("Pet Name     = " + rs.getString("pet_name"));
            System.out.println("Start Date   = " + rs.getString("start_date"));
            System.out.println("End Date     = " + rs.getString("end_date"));
            System.out.println("Total Price  = " + rs.getDouble("total_price"));
            System.out.println("Status       = " + rs.getString("status"));
            System.out.println("----------------------------------------");
        }

        if (count == 0) {
            System.out.println("You do not have any bookings.");
            ps.close();
            rs.close();
            con.close();
            return;
        }

        String choice;
        while (true) {
            choice = validation.getSafeInput(sc, "Do you want to cancel any booking? (yes/no): ").toLowerCase();
            if (choice.equalsIgnoreCase("exit")) {
                ps.close();
                rs.close();
                con.close();
                return;
            }
            if (choice.equals("yes") || choice.equals("no"))
                break;
            System.out.println("Invalid input. Please enter 'yes' or 'no'.");
        }

        if (choice.equals("yes")) {
            String bookingInput = validation.getSafeInput(sc, "Enter booking ID: ");
            if (bookingInput.equalsIgnoreCase("exit")) {
                ps.close();
                rs.close();
                con.close();
                return;
            }
            if (!validation.isValidInteger(bookingInput)) {
                System.out.println("Invalid booking ID.");
                ps.close();
                rs.close();
                con.close();
                return;
            }
            int booking_id = Integer.parseInt(bookingInput);

            PreparedStatement ps2 = con.prepareStatement(
                    "SELECT booking_id FROM bookings b JOIN pets p ON b.pet_id = p.pet_id WHERE b.booking_id = ? AND p.owner_id = ?");
            ps2.setInt(1, booking_id);
            ps2.setInt(2, loggedInOwnerId);
            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                PreparedStatement ps3 = con
                        .prepareStatement("UPDATE bookings SET status = 'cancelled' WHERE booking_id = ?");
                ps3.setInt(1, booking_id);
                int a = ps3.executeUpdate();
                System.out.println(a > 0 ? "Booking has been cancelled." : "Failed to cancel booking.");
                ps3.close();
            } else {
                System.out.println("Invalid booking ID or booking not owned by you.");
            }
            ps2.close();
            rs2.close();
        } else {
            System.out.println("No cancellation done.");
        }

        ps.close();
        rs.close();
        con.close();
    }

    public static void viewAvailableHostsByDateAndSize() throws Exception {
        Scanner sc = new Scanner(System.in);

        String startDate;
        while (true) {
            startDate = validation.getSafeInput(sc, "Enter Start Date (yyyy-mm-dd): ");
            if (startDate.equalsIgnoreCase("exit"))
                return;
            if (validation.isValidDate(startDate))
                break;
            System.out.println("Invalid format. Please enter date in yyyy-mm-dd format.");
        }

        String endDate;
        while (true) {
            endDate = validation.getSafeInput(sc, "Enter End Date (yyyy-mm-dd): ");
            if (endDate.equalsIgnoreCase("exit"))
                return;
            if (validation.isValidDate(endDate))
                break;
            System.out.println("Invalid format. Please enter date in yyyy-mm-dd format.");
        }

        String petSize;
        while (true) {
            petSize = validation.getSafeInput(sc, "Enter pet size (small / medium / large): ").toLowerCase();
            if (petSize.equalsIgnoreCase("exit"))
                return;
            if (petSize.equals("small") || petSize.equals("medium") || petSize.equals("large"))
                break;
            System.out.println("Invalid size. Enter 'small', 'medium', or 'large':");
        }

        Connection con = jdbc.Getconnection();
        String query = """
                    SELECT hl.listing_id, u.user_name, hl.available_from, hl.available_to, hl.pet_size_allowed, hl.price_per_day
                    FROM host_listings hl
                    JOIN user u ON u.user_id = hl.host_id
                    WHERE hl.host_availability = 'available'
                      AND hl.pet_size_allowed = ?
                      AND hl.available_from <= ?
                      AND hl.available_to >= ?
                """;

        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, petSize);
        ps.setString(2, startDate);
        ps.setString(3, endDate);

        ResultSet rs = ps.executeQuery();
        boolean found = false;
        System.out.println("\nAvailable Hosts Matching Your Criteria:\n");

        while (rs.next()) {
            found = true;
            System.out.println("Listing ID       : " + rs.getInt("listing_id"));
            System.out.println("Host Name        : " + rs.getString("user_name"));
            System.out.println("Available From   : " + rs.getDate("available_from"));
            System.out.println("Available To     : " + rs.getDate("available_to"));
            System.out.println("Pet Size Allowed : " + rs.getString("pet_size_allowed"));
            System.out.println("Price per Day ₹  : " + rs.getDouble("price_per_day"));
            System.out.println("----------------------------------------");
        }

        if (!found)
            System.out.println("No hosts available for the selected date and pet size.");

        rs.close();
        ps.close();
        con.close();
    }

    public static void viewMyPets() throws Exception {
        if (loggedInOwnerId == -1) {
            System.out.println("You must be logged in to view your pets.");
            return;
        }

        Connection con = jdbc.Getconnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT pet_id, pet_name, pet_type, pet_breed, pet_size FROM pets WHERE owner_id = ?");
        ps.setInt(1, loggedInOwnerId);
        ResultSet rs = ps.executeQuery();

        boolean found = false;
        System.out.println("\nYour Registered Pets:");
        while (rs.next()) {
            found = true;
            System.out.println("Pet ID     : " + rs.getInt("pet_id"));
            System.out.println("Name       : " + rs.getString("pet_name"));
            System.out.println("Type       : " + rs.getString("pet_type"));
            System.out.println("Breed      : " + rs.getString("pet_breed"));
            System.out.println("Size       : " + rs.getString("pet_size"));
            System.out.println("-----------------------------------");
        }

        if (!found)
            System.out.println("You have no pets registered.");

        rs.close();
        ps.close();
        con.close();
    }

    public static void deleteMyPet() throws Exception {
        if (loggedInOwnerId == -1) {
            System.out.println("You must be logged in to delete a pet.");
            return;
        }

        Connection con = jdbc.Getconnection();
        PreparedStatement ps = con.prepareStatement("SELECT pet_id, pet_name FROM pets WHERE owner_id = ?");
        ps.setInt(1, loggedInOwnerId);
        ResultSet rs = ps.executeQuery();

        boolean hasPets = false;
        System.out.println("\nYour Registered Pets:");
        while (rs.next()) {
            hasPets = true;
            System.out.println("Pet ID: " + rs.getInt("pet_id") + " | Name: " + rs.getString("pet_name"));
        }

        if (!hasPets) {
            System.out.println("You have no pets to delete.");
            ps.close();
            rs.close();
            con.close();
            return;
        }

        Scanner sc = new Scanner(System.in);
        int petId = -1;
        for (int i = 0; i < 3; i++) {
            String input = validation.getSafeInput(sc, "Enter Pet ID to delete: ");
            if (input.equalsIgnoreCase("exit")) {
                ps.close();
                rs.close();
                con.close();
                return;
            }
            if (validation.isValidInteger(input)) {
                petId = Integer.parseInt(input);
                PreparedStatement check = con.prepareStatement("SELECT * FROM pets WHERE pet_id = ? AND owner_id = ?");
                check.setInt(1, petId);
                check.setInt(2, loggedInOwnerId);
                ResultSet rsCheck = check.executeQuery();
                if (rsCheck.next()) {
                    rsCheck.close();
                    check.close();
                    break;
                }
                rsCheck.close();
                check.close();
            }
            System.out.println("Invalid Pet ID. Try again.");
            if (i == 2) {
                ps.close();
                rs.close();
                con.close();
                return;
            }
        }

        PreparedStatement checkBookings = con.prepareStatement("SELECT * FROM bookings WHERE pet_id = ?");
        checkBookings.setInt(1, petId);
        ResultSet rs2 = checkBookings.executeQuery();

        if (rs2.next()) {
            System.out.println("Cannot delete this pet. It has existing bookings.");
        } else {
            PreparedStatement delete = con.prepareStatement("DELETE FROM pets WHERE pet_id = ?");
            delete.setInt(1, petId);
            int rows = delete.executeUpdate();
            System.out.println(rows > 0 ? "Pet deleted successfully." : "Failed to delete pet.");
            delete.close();
        }

        ps.close();
        rs.close();
        rs2.close();
        checkBookings.close();
        con.close();
    }
}
