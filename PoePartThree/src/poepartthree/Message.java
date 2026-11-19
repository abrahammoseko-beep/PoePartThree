package poepartthree;

import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Student
 */

public class Message {
  
    ArrayList<String> sentMessages = new ArrayList<>();
    ArrayList<String> messageIdsList = new ArrayList<>();
    ArrayList<String> msgHash = new ArrayList<>();
    ArrayList<String> recipientNumber = new ArrayList<>();
    ArrayList<String> messageStatus = new ArrayList<>();
    ArrayList<String> senders = new ArrayList<>();
    ArrayList<String> disregardedMessages = new ArrayList<>();
    ArrayList<String> storedMessages = new ArrayList<>();
    HashSet<String> messageIds = new HashSet<>();
    ArrayList<String> storedIds = new ArrayList<>();
    ArrayList<String> storedHashes = new ArrayList<>();
    ArrayList<String> storedSenders = new ArrayList<>();
    ArrayList<String> storedRecipients = new ArrayList<>();

    // Part 3: JSON file used to store messages
    String storageFile = "storedMessages.json";
    Random random = new Random();
    int numMessageSent = 0;

    // Loads the stored messages from the JSON file when the app starts
    public Message() {
        loadStoredMessages();
    }

    // Lets the unit tests use a temporary JSON file
    public Message(String file) {
        this.storageFile = file;
        loadStoredMessages();
    }

    //  helper to show long text  
    private void showText(String title, String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(720, 320));
        JOptionPane.showMessageDialog(null, scroll, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // sending  
    public void sendingMessages() {
        int numberOfMessages;
        while (true) {
            String input = JOptionPane.showInputDialog(null,
                    "How many messages do you want to send? ",
                    "Send Messages",
                    JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return;
            }
            try {
                numberOfMessages = Integer.parseInt(input.trim());
                if (numberOfMessages < 1) {
                    JOptionPane.showMessageDialog(null, "Please enter a number greater than 0", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        for (int i = 1; i <= numberOfMessages; i++) {
            String messageID = generateMessageID();
            if (checkMessageID(messageID)) {
                JOptionPane.showMessageDialog(null, messageIDGenerated(messageID), "Message ID",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            String recipient;
            while (true) {
                recipient = JOptionPane.showInputDialog(null,
                        "Message " + i + " of " + numberOfMessages
                        + "\n\nEnter recipient cell number:",
                        "Recipient", JOptionPane.QUESTION_MESSAGE);
                if (recipient == null) {
                    return;
                }
                recipient = recipient.trim();
                String recipientResults = checkRecipientCell(recipient);
                if (recipientResults.equals("Cell phone number successfully captured.")) {
                    break;
                }
                JOptionPane.showMessageDialog(null, recipientResults, "Invalid number",
                        JOptionPane.ERROR_MESSAGE);
            }
            String messageText;
            while (true) {
                messageText = JOptionPane.showInputDialog(null,
                        "Enter your message (1 to 250 characters long): ",
                        "Message", JOptionPane.QUESTION_MESSAGE);
                if (messageText == null) {
                    return;
                }
                if (messageText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Please enter a message of 1 to 250 characters long",
                            "Invalid message", JOptionPane.ERROR_MESSAGE);
                } else if (messageText.length() > 250) {
                    JOptionPane.showMessageDialog(null,
                            checkMessageLength(messageText),
                            "Invalid message", JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            }
            String sender = JOptionPane.showInputDialog(null, "Enter sender's name:", "Sender",
                    JOptionPane.QUESTION_MESSAGE);
            if (sender == null || sender.trim().isEmpty()) {
                sender = "unknown";
            }
            sender = sender.trim();
            int messageNumber = sentMessages.size() + 1;
            String messageHash = createMessageHash(messageID, messageNumber, messageText);
            String status = SentMessage();
            messageIds.add(messageID);
            switch (status) {
                case "Sent" -> {
                    sentMessages.add(messageText);
                    messageIdsList.add(messageID);
                    msgHash.add(messageHash);
                    recipientNumber.add(recipient);
                    senders.add(sender);
                    messageStatus.add(status);
                    numMessageSent++;
                }
                case "Disregard" ->
                    disregardedMessages.add(messageText);
                case "Stored" ->
                    addStored(messageID, messageHash, sender, recipient, messageText);
                default -> {
                }
            }
        }
        showText("Sent Messages", printMessages());
        JOptionPane.showMessageDialog(null,
                "Total messages sent: " + returnTotalMessages(), "Total",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public int returnTotalMessages() {
        return numMessageSent;
    }

    public boolean checkMessageID(String messageID) {
        return messageID != null && messageID.length() <= 10;
    }

    public String messageIDGenerated(String messageID) {
        return "Message ID generated: " + messageID;
    }

    public String checkMessageLength(String messageText) {
        if (messageText.length() > 250) {
            return "Message exceeds 250 characters by " + (messageText.length() - 250)
                    + "; please reduce the size.";
        }
        return "Message ready to send.";
    }

    public String createMessageHash(String messageID, int messageNumber, String messageText) {
        String firstTwoNumbers = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        String hash = firstTwoNumbers + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }

    public String generateMessageID() {
        String messageID;
        do {
            long number = 1000000000L + random.nextLong(9000000000L);
            messageID = String.valueOf(number);
        } while (messageIds.contains(messageID));
        return messageID;
    }

 
    public String checkRecipientCell(String cellphone) {
        if (cellphone.matches("^\\+27[0-9]{9}$") || cellphone.matches("^0[0-9]{9}$")) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }

    // Returns "Sent", "Disregard" or "Stored"
    public String SentMessage() {
        Object[] option = {"Sent", "Disregard", "Store"};
        while (true) {
            int choice = JOptionPane.showOptionDialog(null,
                    "Choose what you want to do with this message!", "Message Options",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, option, option[0]);
            switch (choice) {
                case 0 -> {
                    JOptionPane.showMessageDialog(null, "Message sent successfully",
                            "Sent", JOptionPane.INFORMATION_MESSAGE);
                    return "Sent";
                }
                case 1 -> {
                    String pressed = JOptionPane.showInputDialog(null,
                            "Press 0 to delete the message.",
                            "Disregard Message", JOptionPane.QUESTION_MESSAGE);
                    if (pressed != null && pressed.trim().equals("0")) {
                        JOptionPane.showMessageDialog(null, "Message deleted",
                                "Deleted", JOptionPane.INFORMATION_MESSAGE);
                        return "Disregard";
                    }
                    // NO: go back to the options
                }
                case 2 -> {
                    JOptionPane.showMessageDialog(null, "Message successfully stored");
                    return "Stored";
                }
                default -> {
                    // dialog closed: treat as disregarded
                    JOptionPane.showMessageDialog(null, "No option chosen, message disregarded");
                    return "Disregard";
                }
            }
        }
    }

    public String printMessages() {
        StringBuilder allMessages = new StringBuilder();
        if (sentMessages.isEmpty()) {
            return "No sent messages.";
        }
        for (int i = 0; i < sentMessages.size(); i++) {
            allMessages.append("==============================");
            allMessages.append("\nMessage Number: ").append(i + 1);
            allMessages.append("\nMessage ID: ").append(messageIdsList.get(i));
            allMessages.append("\nMessage Hash: ").append(msgHash.get(i));
            allMessages.append("\nRecipient: ").append(recipientNumber.get(i));
            allMessages.append("\nMessage: ").append(sentMessages.get(i));
            allMessages.append("\nStatus: ").append(messageStatus.get(i));
            allMessages.append("\n\n");
        }
        return allMessages.toString();
    }

    // ---------- management menu ----------
    public void messageManagementMenu() {
        while (true) {
            String menu = "1. Show disregarded messages\n"
                    + "2. Show all stored messages\n"
                    + "3. Delete a message by Hash ID\n"
                    + "4. Display full report\n"
                    + "5. Search for a message by Hash ID\n"
                    + "6. Display the longest message\n"
                    + "7. Back to menu\n\n"
                    + "Choose an option (1-7):";
            String option = JOptionPane.showInputDialog(null, menu, "Message Management",
                    JOptionPane.QUESTION_MESSAGE);
            if (option == null) {
                return;
            }
            switch (option.trim()) {
                case "1" ->
                    showDisregardedMessages();
                case "2" ->
                    showAllStoredMessages();
                case "3" ->
                    deleteMessageByHash();
                case "4" ->
                    displayFullReport();
                case "5" ->
                    searchMessageByHashID();
                case "6" ->
                    findLongestMessage();
                case "7" -> {
                    return;
                }
                default ->
                    JOptionPane.showMessageDialog(null, "Invalid choice.", "Error",
                            JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void findLongestMessage() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to analyse.");
            return;
        }
        int maxLength = -1;
        int index = -1;
        for (int i = 0; i < sentMessages.size(); i++) {
            int currentLength = sentMessages.get(i).length();
            if (currentLength > maxLength) {
                maxLength = currentLength;
                index = i;
            }
        }
        String text = "Length: " + maxLength + " characters long"
                + "\nHash ID: " + msgHash.get(index)
                + "\nMessage ID: " + messageIdsList.get(index)
                + "\nFrom: " + senders.get(index)
                + "\nTo: " + recipientNumber.get(index)
                + "\nMessage: " + sentMessages.get(index);
        showText("Longest Message Sent", text);
    }

    public void showDisregardedMessages() {
        if (disregardedMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No disregarded messages yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < disregardedMessages.size(); i++) {
            sb.append(i + 1).append(". ").append(disregardedMessages.get(i)).append("\n");
        }
        showText("Disregarded Messages", sb.toString());
    }

    public void showAllStoredMessages() {
        if (storedMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No stored messages to display.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < storedMessages.size(); i++) {
            sb.append(i + 1).append(". ").append(storedMessages.get(i)).append("\n");
        }
        showText("Stored Messages", sb.toString());
    }

    public void searchMessageByHashID() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages to search.");
            return;
        }
        String hashId = JOptionPane.showInputDialog(null, "Enter the Hash ID to search:");
        if (hashId == null) {
            return;
        }
        hashId = hashId.trim();
        if (hashId.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Hash ID cannot be empty.");
            return;
        }
        int index = msgHash.indexOf(hashId.toUpperCase());
        if (index == -1) {
            JOptionPane.showMessageDialog(null, "No message exists with this Hash ID: " + hashId);
            return;
        }
        String text = "Hash ID: " + msgHash.get(index)
                + "\nMessage ID: " + messageIdsList.get(index)
                + "\nFrom: " + senders.get(index)
                + "\nTo: " + recipientNumber.get(index)
                + "\nMessage: " + sentMessages.get(index)
                + "\nLength: " + sentMessages.get(index).length() + " characters long";
        showText("Message Details", text);
    }

    public void displayFullReport() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-5s | %-15s | %-15s | %-20s | %-12s | %-12s |%n",
                "No.", "Sender", "Recipient", "Message", "Message ID", "Hash ID"));
        for (int i = 0; i < sentMessages.size(); i++) {
            String message = sentMessages.get(i);
            if (message.length() > 20) {
                message = message.substring(0, 17) + "...";
            }
            sb.append(String.format("| %-5d | %-15s | %-15s | %-20s | %-12s | %-12s |%n",
                    i + 1,
                    senders.get(i),
                    recipientNumber.get(i),
                    message,
                    messageIdsList.get(i),
                    msgHash.get(i)));
        }
        sb.append("\nTotal sent messages: ").append(sentMessages.size());
        sb.append("\nTotal disregarded messages: ").append(disregardedMessages.size());
        sb.append("\nTotal stored messages: ").append(storedMessages.size());
        showText("Full Message Report", sb.toString());
    }

    public void deleteMessageByHash() {
        String hash = JOptionPane.showInputDialog(null, "Enter Hash ID to delete:");
        if (hash == null) {
            return;
        }
        int index = msgHash.indexOf(hash.trim().toUpperCase());
        if (index != -1) {
            disregardedMessages.add(sentMessages.get(index));
            messageIds.remove(messageIdsList.get(index));
            sentMessages.remove(index);
            senders.remove(index);
            recipientNumber.remove(index);
            messageIdsList.remove(index);
            msgHash.remove(index);
            messageStatus.remove(index);
            numMessageSent--;
            JOptionPane.showMessageDialog(null, "Message deleted and moved to disregarded.");
        } else {
            JOptionPane.showMessageDialog(null, "Message not found.");
        }
    }
 
    private static final String JSON_STRING = "\"((?:[^\"\\\\]|\\\\.)*)\"";

    private static final Pattern JSON_ENTRY = Pattern.compile(
            "\\{\\s*\"id\"\\s*:\\s*" + JSON_STRING
            + "\\s*,\\s*\"hash\"\\s*:\\s*" + JSON_STRING
            + "\\s*,\\s*\"sender\"\\s*:\\s*" + JSON_STRING
            + "\\s*,\\s*\"recipient\"\\s*:\\s*" + JSON_STRING
            + "\\s*,\\s*\"message\"\\s*:\\s*" + JSON_STRING + "\\s*\\}");

    private void addStored(String id, String hash, String sender, String recipient, String text) {
        storedMessages.add(text);
        storedIds.add(id);
        storedHashes.add(hash);
        storedSenders.add(sender);
        storedRecipients.add(recipient);
        storeMessage();
    }
 
    public void addMessage(String sender, String recipient, String text, String flag) {
        String id = generateMessageID();
        messageIds.add(id);
        String hash = createMessageHash(id, sentMessages.size() + 1, text);
        switch (flag) {
            case "Sent" -> {
                sentMessages.add(text);
                messageIdsList.add(id);
                msgHash.add(hash);
                recipientNumber.add(recipient);
                senders.add(sender);
                messageStatus.add("Sent");
                numMessageSent++;
            }
            case "Stored" ->
                addStored(id, hash, sender, recipient, text);
            case "Disregard" ->
                disregardedMessages.add(text);
            default -> {
            }
        }
    }
 
    public void storeMessage() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            json.append("  {\"id\": \"").append(escapeJson(storedIds.get(i)))
                    .append("\", \"hash\": \"").append(escapeJson(storedHashes.get(i)))
                    .append("\", \"sender\": \"").append(escapeJson(storedSenders.get(i)))
                    .append("\", \"recipient\": \"").append(escapeJson(storedRecipients.get(i)))
                    .append("\", \"message\": \"").append(escapeJson(storedMessages.get(i)))
                    .append("\"}");
            if (i < storedMessages.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        json.append("]");
        try {
            Files.writeString(Path.of(storageFile), json.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not save messages to the JSON file.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    public void loadStoredMessages() {
        storedMessages.clear();
        storedIds.clear();
        storedHashes.clear();
        storedSenders.clear();
        storedRecipients.clear();
        Path path = Path.of(storageFile);
        if (!Files.exists(path)) {
            return;
        }
        try {
            String content = Files.readString(path);
            Matcher m = JSON_ENTRY.matcher(content);
            while (m.find()) {
                storedIds.add(unescapeJson(m.group(1)));
                storedHashes.add(unescapeJson(m.group(2)));
                storedSenders.add(unescapeJson(m.group(3)));
                storedRecipients.add(unescapeJson(m.group(4)));
                storedMessages.add(unescapeJson(m.group(5)));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not read the JSON file.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private String unescapeJson(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                sb.append(next == 'n' ? '\n' : next);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

 
    public String getStoredSendersAndRecipients() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < storedMessages.size(); i++) {
            sb.append(i + 1).append(". Sender: ").append(storedSenders.get(i))
                    .append(" | Recipient: ").append(storedRecipients.get(i)).append("\n");
        }
        return sb.toString();
    }
 
    public String getLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        int index = 0;
        for (int i = 1; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).length() > storedMessages.get(index).length()) {
                index = i;
            }
        }
        return storedMessages.get(index);
    }
 
    public String searchByMessageID(String id) {
        int index = messageIdsList.indexOf(id);
        if (index != -1) {
            return "Recipient: " + recipientNumber.get(index) + "\nMessage: " + sentMessages.get(index);
        }
        index = storedIds.indexOf(id);
        if (index != -1) {
            return "Recipient: " + storedRecipients.get(index) + "\nMessage: " + storedMessages.get(index);
        }
        return "No message found with ID: " + id;
    }
 
    public String searchByRecipient(String recipient) {
        ArrayList<String> found = new ArrayList<>();
        for (int i = 0; i < sentMessages.size(); i++) {
            if (recipientNumber.get(i).equals(recipient)) {
                found.add(sentMessages.get(i));
            }
        }
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedRecipients.get(i).equals(recipient)) {
                found.add(storedMessages.get(i));
            }
        }
        if (found.isEmpty()) {
            return "No messages found for " + recipient;
        }
        return String.join("\n", found);
    }
 
    public String deleteStoredMessageByHash(String hash) {
        int index = storedHashes.indexOf(hash.trim().toUpperCase());
        if (index == -1) {
            return "Message not found.";
        }
        String text = storedMessages.get(index);
        disregardedMessages.add(text);
        messageIds.remove(storedIds.get(index));
        storedMessages.remove(index);
        storedIds.remove(index);
        storedHashes.remove(index);
        storedSenders.remove(index);
        storedRecipients.remove(index);
        storeMessage();
        return "Message: \"" + text + "\" successfully deleted.";
    }
 
    public String buildStoredReport() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-4s | %-12s | %-15s | %-12s | %-14s | %s%n",
                "No.", "Sender", "Recipient", "Message ID", "Hash ID", "Message"));
        for (int i = 0; i < storedMessages.size(); i++) {
            sb.append(String.format("| %-4d | %-12s | %-15s | %-12s | %-14s | %s%n",
                    i + 1,
                    storedSenders.get(i),
                    storedRecipients.get(i),
                    storedIds.get(i),
                    storedHashes.get(i),
                    storedMessages.get(i)));
        }
        sb.append("\nTotal stored messages: ").append(storedMessages.size());
        return sb.toString();
    }

    public String[] getAllMessageHashes() {
        ArrayList<String> all = new ArrayList<>(msgHash);
        all.addAll(storedHashes);
        return all.toArray(new String[0]);
    }

    public String[] getAllMessageIDs() {
        ArrayList<String> all = new ArrayList<>(messageIdsList);
        all.addAll(storedIds);
        return all.toArray(new String[0]);
    }

    public String buildSentReport() {
        if (sentMessages.isEmpty()) {
            return "No sent messages.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-4s | %-14s | %-15s | %s%n", "No.", "Message Hash", "Recipient", "Message"));
        for (int i = 0; i < sentMessages.size(); i++) {
            sb.append(String.format("| %-4d | %-14s | %-15s | %s%n",
                    i + 1, msgHash.get(i), recipientNumber.get(i), sentMessages.get(i)));
        }
        sb.append("\nTotal sent messages: ").append(sentMessages.size());
        return sb.toString();
    }

    // Main menu option 4
    public void storedMessagesMenu() {
        while (true) {
            String menu = "1. Display sender and recipient of all stored messages\n"
                    + "2. Display the longest stored message\n"
                    + "3. Search for a message ID\n"
                    + "4. Search messages by recipient\n"
                    + "5. Delete a message using its hash\n"
                    + "6. Display report of all stored messages\n"
                    + "7. Back to menu\n\n"
                    + "Choose an option (1-7):";
            String option = JOptionPane.showInputDialog(null, menu, "Stored Messages",
                    JOptionPane.QUESTION_MESSAGE);
            if (option == null) {
                return;
            }
            switch (option.trim()) {
                case "1" ->
                    showText("Stored Messages", getStoredSendersAndRecipients());
                case "2" ->
                    showText("Longest Stored Message", getLongestStoredMessage());
                case "3" -> {
                    String id = JOptionPane.showInputDialog(null, "Enter the message ID:");
                    if (id != null) {
                        showText("Search Result", searchByMessageID(id.trim()));
                    }
                }
                case "4" -> {
                    String recipient = JOptionPane.showInputDialog(null, "Enter the recipient cell number:");
                    if (recipient != null) {
                        showText("Messages for " + recipient.trim(), searchByRecipient(recipient.trim()));
                    }
                }
                case "5" -> {
                    String hash = JOptionPane.showInputDialog(null, "Enter the message hash to delete:");
                    if (hash != null) {
                        JOptionPane.showMessageDialog(null, deleteStoredMessageByHash(hash));
                    }
                }
                case "6" ->
                    showText("Stored Messages Report", buildStoredReport());
                case "7" -> {
                    return;
                }
                default ->
                    JOptionPane.showMessageDialog(null, "Invalid choice.", "Error",
                            JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}