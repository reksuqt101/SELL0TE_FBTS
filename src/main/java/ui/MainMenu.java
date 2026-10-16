package ui;

import dao.UserDao;
import model.User;

import java.util.Optional;
import java.util.Scanner;

/**
 * Main menu: Register, Login, Exit.
 */
public class MainMenu {

    private final Scanner scanner;
    private final UserDao userDao;

    public MainMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== FISHING BOAT TRIP SYSTEM =====");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please enter 1, 2, or 3.");
            }
        }
    }

    private void register() {
        System.out.println();
        System.out.println("--- Register Account ---");

        String username = prompt("Username: ");
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }
        if (userDao.usernameExists(username)) {
            System.out.println("That username is already taken.");
            return;
        }

        String password = prompt("Password: ");
        if (password.length() < 4) {
            System.out.println("Password must be at least 4 characters.");
            return;
        }
        if (!password.equals(prompt("Confirm password: "))) {
            System.out.println("Passwords do not match. Registration cancelled.");
            return;
        }

        String role;
        String status;
        if (userDao.countUsers() == 0) {
            role = User.ROLE_OWNER_ADMIN;
            status = User.STATUS_ACTIVE;
            System.out.println("First account detected: you are registered as " + role + ".");
        } else {
            role = chooseRole();
            if (role == null) {
                return;
            }
            status = User.STATUS_PENDING;
        }

        User newUser = User.newUser(username, password, role, status);
        if (userDao.register(newUser)) {
            System.out.println("Account created! Role: " + role + ", Status: " + newUser.status());
        } else {
            System.out.println("That username is already taken.");
        }
    }

    private String chooseRole() {
        System.out.println("Select role:");
        System.out.println("1. " + User.ROLE_CAPTAIN);
        System.out.println("2. " + User.ROLE_CLERK);
        System.out.print("Choose role: ");
        return switch (scanner.nextLine().trim()) {
            case "1" -> User.ROLE_CAPTAIN;
            case "2" -> User.ROLE_CLERK;
            default -> {
                System.out.println("Invalid role. Registration cancelled.");
                yield null;
            }
        };
    }

    private void login() {
        System.out.println();
        System.out.println("--- Login ---");
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        Optional<User> result = userDao.login(username, password);
        if (result.isPresent()) {
            new UserMenu(scanner, result.get(), userDao).show();
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }
}
