package pet_hosting;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;
 

public class validation {

    public static boolean isValidName(String name) {
        return name != null && name.matches("^[A-Za-z\\s]{2,50}$");
    }

   
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^[6-9]\\d{9}$");
    }

    
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$");
    }

  
    public static boolean isValidPassword(String password) {
        return password != null &&
               password.length() >= 6 &&
               password.matches(".*[A-Za-z].*") &&
               password.replaceAll("\\D", "").length() >= 2;
    }


    public static boolean isValidInteger(String input) {
        if (input == null) return false;
        return input.matches("-?\\d+");
    }

    public static boolean isValidDate(String dateStr) {
        try {
            if (dateStr == null) return false;
            LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public static String getSafeInput(Scanner sc, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = sc.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println(" Please try again.");
                    continue;
                }

                return input;

            } catch (NoSuchElementException e) {
                System.out.println("\n Please try again.");
                
                sc = new Scanner(System.in);
            } catch (IllegalStateException e) {
                System.out.println("\n try again");
                sc = new Scanner(System.in);
            }
        }
    }
   

public static boolean isFutureDate(String dateStr) {
    try {
        if (dateStr == null) return false;

        LocalDate inputDate = LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return inputDate.isAfter(LocalDate.now());

    } catch (DateTimeParseException e) {
        return false;
    }
}

}
