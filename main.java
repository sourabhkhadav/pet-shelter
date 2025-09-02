package pet_hosting;

import java.util.Scanner;

public class main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String choice;

        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Admin");
            System.out.println("2. Owner");
            System.out.println("3. Host");
            System.out.println("4. Exit");

            int intChoice = -1;
            for (int i = 0; i < 3; i++) {
                choice = validation.getSafeInput(sc, "Select your role : ");
                if (choice.equalsIgnoreCase("exit")) return;
                if (validation.isValidInteger(choice)) {
                    intChoice = Integer.parseInt(choice);
                    break;
                } else {
                    System.out.println("Try again. Please enter an integer value.");
                }
            }

            if (intChoice == -1) {
                System.out.println("Too many invalid attempts. Exiting program...");
                return;
            }

            switch (intChoice) {
                case 1:
                    admin.adminn();
                    break;

                case 2:
                    while (true) {
                        System.out.println("\n-- Owner Menu --");
                        System.out.println("1. Signup");
                        System.out.println("2. Login");
                        System.out.println("3. Exit to Main Menu");

                        int ownerChoice = -1;
                        for (int i = 0; i < 3; i++) {
                            String input = validation.getSafeInput(sc, "Enter your choice : ");
                            if (input.equalsIgnoreCase("exit")) return;
                            if (validation.isValidInteger(input)) {
                                ownerChoice = Integer.parseInt(input);
                                break;
                            } else {
                                System.out.println("Invalid input. Please enter a number.");
                            }
                        }

                        if (ownerChoice == -1) {
                            System.out.println("Too many invalid attempts. Returning to main menu...");
                            break;
                        }

                        switch (ownerChoice) {
                            case 1 -> owner.signup_user();
                            case 2 -> owner.loginuser();
                            case 3 -> {
                                System.out.println("Returning to Main Menu...");
                                break;
                            }
                            default -> System.out.println("Invalid owner choice.");
                        }

                        if (ownerChoice == 3) break;
                    }
                    break;

                case 3:
                    while (true) {
                        System.out.println("\n-- Host Menu --");
                        System.out.println("1. Signup");
                        System.out.println("2. Login");
                        System.out.println("3. Exit to Main Menu");

                        int hostChoice = -1;
                        for (int i = 0; i < 3; i++) {
                            String input = validation.getSafeInput(sc, "Enter your choice : ");
                            if (input.equalsIgnoreCase("exit")) return;
                            if (validation.isValidInteger(input)) {
                                hostChoice = Integer.parseInt(input);
                                break;
                            } else {
                                System.out.println("Invalid input. Please enter a number.");
                            }
                        }

                        if (hostChoice == -1) {
                            System.out.println("Too many invalid attempts. Returning to main menu...");
                            break;
                        }

                        switch (hostChoice) {
                            case 1 -> host.signup_user();
                            case 2 -> host.loginuser();
                            case 3 -> {
                                System.out.println("Returning to Main Menu...");
                                break;
                            }
                            default -> System.out.println("Invalid host choice.");
                        }

                        if (hostChoice == 3) break;
                    }
                    break;

                case 4:
                    System.out.println("Exiting the program. Goodbye!");
                    return;

                default:
                    System.out.println("Invalid main menu choice.");
            }
        }
    }
}
