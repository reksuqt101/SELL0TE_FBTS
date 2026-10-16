package ui;

import dao.UserDao;
import model.User;

import java.util.List;
import java.util.Scanner;

/**
 * Menu shown after a successful login. The Boat Owner / Administrator is
 * recognized by role and gets an extra option to approve pending accounts.
 * Logging out returns to the main menu.
 */
public class UserMenu {

    private final Scanner scanner;
    private final User user;
    private final UserDao userDao;

    public UserMenu(Scanner scanner, User user, UserDao userDao) {
        this.scanner = scanner;
        this.user = user;
        this.userDao = userDao;
    }

    private boolean isAdmin() {
        return User.ROLE_OWNER_ADMIN.equals(user.roles());
    }

    public void show() {
        System.out.println();
        if (isAdmin()) {
            System.out.println("Welcome, " + user.username() + "! You are logged in as the " + User.ROLE_OWNER_ADMIN + ".");
        } else {
            System.out.println("Welcome, " + user.username() + "! (" + user.roles() + ")");
        }

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println();
            System.out.println(isAdmin() ? "===== ADMINISTRATOR MENU =====" : "===== USER MENU =====");
            System.out.println("1. View my profile");
            if (isAdmin()) {
                System.out.println("2. Approve pending accounts");
                System.out.println("3. Logout");
            } else {
                System.out.println("2. Logout");
            }
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            if (choice.equals("1")) {
                showProfile();
            } else if (isAdmin() && choice.equals("2")) {
                approvePending();
            } else if ((isAdmin() && choice.equals("3")) || (!isAdmin() && choice.equals("2"))) {
                System.out.println("You have been logged out.");
                loggedIn = false;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private void showProfile() {
        System.out.println("ID:       " + user.id());
        System.out.println("Username: " + user.username());
        System.out.println("Role:     " + user.roles());
        System.out.println("Status:   " + user.status());
    }

    private void approvePending() {
        List<User> pending = userDao.findPending();
        if (pending.isEmpty()) {
            System.out.println("No pending accounts.");
            return;
        }
        System.out.println("--- Pending accounts ---");
        for (User u : pending) {
            System.out.println(u.id() + ". " + u.username() + " (" + u.roles() + ")");
        }
        System.out.print("Enter the ID to approve (or press Enter to go back): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return;
        }
        try {
            int id = Integer.parseInt(input);
            boolean listed = pending.stream().anyMatch(u -> u.id() == id);
            if (listed && userDao.approve(id)) {
                System.out.println("Account approved. Status is now ACTIVE.");
            } else {
                System.out.println("No pending account with that ID.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
        }
    }
}
