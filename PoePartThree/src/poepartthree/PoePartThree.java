package poepartthree;

import javax.swing.JOptionPane;
import java.awt.Color;
import javax.swing.UIManager;
/**
 *
 * @author Student
 */

public class PoePartThree {

    public static void main(String[] args) {
        // Changing my JOptionPane color
        UIManager.put("OptionPane.background", new Color(25, 35, 60));
        UIManager.put("Panel.background", new Color(25, 35, 60));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("Label.foreground", Color.WHITE);
        UIManager.put("Button.background", new Color(70, 110, 200));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", Color.BLACK);
        UIManager.put("TextArea.background", new Color(240, 244, 255));
        UIManager.put("TextArea.foreground", Color.BLACK);
        
        Login register = new Login();
        Message msg = new Message();
        JOptionPane.showMessageDialog(null,
                "===== Register =====",
                "Registration",
                JOptionPane.INFORMATION_MESSAGE);
        String firstName = JOptionPane.showInputDialog(null, "Please enter your first name: ");
        String lastName = JOptionPane.showInputDialog(null, "Please enter your last name: ");
        register.setName(firstName, lastName);
        String name = JOptionPane.showInputDialog(null, "Please enter the username: ");
        String password = JOptionPane.showInputDialog(null, "Enter password");
        String cellphone = JOptionPane.showInputDialog(null, "Enter cellphone(+27*********)");
        JOptionPane.showMessageDialog(null, register.registerUser(name, password, cellphone));
        if (!register.registerStatus(name, password, cellphone)) {
            return;
        }
        JOptionPane.showMessageDialog(null,
                "========= Login =========\n"
              + "You have 3 attempts to login.",
                "Login",
                JOptionPane.INFORMATION_MESSAGE);
        int attemptsLeft = 3;
        boolean status = false;
        while (attemptsLeft > 0 && !status) {
            String loginUsername = JOptionPane.showInputDialog(null, "Enter username to Login: ");
            String loginPassword = JOptionPane.showInputDialog(null, "Enter password: ");
            status = register.loginUser(loginUsername, loginPassword);
            JOptionPane.showMessageDialog(null, register.returnLoginStatus(status));
            if (!status) {
                attemptsLeft--;
                if (attemptsLeft > 0) {
                    JOptionPane.showMessageDialog(null,
                            "Attempts remaining: " + attemptsLeft,
                            "Login",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        if (!status) {
            JOptionPane.showMessageDialog(null,
                    "You have used all 3 attempts. The application will now exit.",
                    "Login",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (status) {
            JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");
            int option;
            do {
                String menu = "===== Select any of the options below =====\n"
                        + "1. Send message\n"
                        + "2. Message management\n"
                        + "3. Exit\n"
                        + "4. Stored messages\n"
                        + "Select an option: ";
                String input = JOptionPane.showInputDialog(null,
                        menu,
                        "Main menu",
                        JOptionPane.QUESTION_MESSAGE);
                if (input == null) {
                    option = 3;
                } else {
                    try {
                        option = Integer.parseInt(input.trim());
                        switch (option) {
                            case 1 ->
                                msg.sendingMessages();
                            case 2 ->
                                msg.messageManagementMenu();
                            case 3 ->
                                JOptionPane.showMessageDialog(null, "Thank you for using QuickChat, Good bye!!");
                            case 4 ->
                                msg.storedMessagesMenu();
                            default ->
                                JOptionPane.showMessageDialog(null, "Invalid option!!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        option = 0;
                        JOptionPane.showMessageDialog(null, "Please enter a valid number", "Input Error",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            } while (option != 3);
        }
    }
}