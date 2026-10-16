import dao.DatabaseConnection;
import dao.UserDao;
import ui.MainMenu;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseConnection.initialize();

        try (Scanner scanner = new Scanner(System.in)) {
            new MainMenu(scanner, new UserDao()).show();
        }
    }
}
